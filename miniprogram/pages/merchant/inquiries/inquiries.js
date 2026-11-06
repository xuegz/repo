const merchant = require('../../../utils/merchant')

const STATUS_META = {
  0: { text: '待报价', cls: 'pending' },
  1: { text: '已报价', cls: 'online' },
  2: { text: '跟进中', cls: 'follow' }
}

function formatTime(t) {
  return t ? String(t).replace('T', ' ').slice(0, 16) : ''
}

function todayStr() {
  const d = new Date()
  const m = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return d.getFullYear() + '-' + m + '-' + day
}

function moneyText(v) {
  if (v === null || v === undefined || v === '') {
    return ''
  }
  return Number(v).toFixed(2)
}

Page({
  data: {
    filter: -1,
    tabs: [],
    list: [],
    pageNum: 1,
    pageSize: 10,
    finished: false,
    loading: false,
    loaded: false,
    showQuote: false,
    quotingId: 0,
    quoting: {},
    quoteForm: {
      quotePrice: '',
      quoteTotal: '',
      quoteRemark: '',
      quoteValidUntil: ''
    },
    minDate: todayStr(),
    submitting: false
  },

  onShow: function () {
    this.loadCounts()
    this.loadList(true)
  },

  onPullDownRefresh: function () {
    this.loadCounts()
    this.loadList(true, function () {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom: function () {
    if (!this.data.finished && !this.data.loading) {
      this.loadList(false)
    }
  },

  loadCounts: function () {
    const that = this
    merchant.merchantRequest('/inquiry/counts')
      .then(function (data) {
        const counts = data || {}
        that.setData({
          tabs: [
            { status: -1, label: '全部', count: Number(counts.all || 0) },
            { status: 0, label: '待报价', count: Number(counts.pending || 0) },
            { status: 1, label: '已报价', count: Number(counts.quoted || 0) },
            { status: 2, label: '跟进中', count: Number(counts.follow || 0) }
          ]
        })
      })
      .catch(function () {})
  },

  loadList: function (reset, cb) {
    const that = this
    if (reset) {
      this.setData({ list: [], pageNum: 1, finished: false })
    } else if (this.data.loading || this.data.finished) {
      if (cb) cb()
      return
    }
    const seq = (this.listSeq || 0) + 1
    this.listSeq = seq
    const pageNum = this.data.pageNum
    const params = { pageNum: pageNum, pageSize: this.data.pageSize }
    if (this.data.filter !== -1) {
      params.status = this.data.filter
    }
    this.setData({ loading: true })
    merchant.merchantRequest('/inquiry/page', 'GET', params)
      .then(function (data) {
        if (seq !== that.listSeq) return
        const records = ((data && data.records) || []).map(that.decorate)
        that.setData({
          list: reset ? records : that.data.list.concat(records),
          pageNum: pageNum + 1,
          finished: records.length < that.data.pageSize,
          loaded: true
        })
      })
      .catch(function () {
        if (seq === that.listSeq) {
          that.setData({ loaded: true })
        }
      })
      .then(function () {
        if (seq === that.listSeq) {
          that.setData({ loading: false })
          if (cb) cb()
        }
      })
  },

  decorate: function (item) {
    const meta = STATUS_META[item.status] || { text: '未知', cls: 'pending' }
    item.statusText = meta.text
    item.statusClass = meta.cls
    item.timeText = formatTime(item.createTime)
    item.quoteTimeText = formatTime(item.quoteTime)
    item.hasQuote = item.quotePrice != null || item.quoteTotal != null
    item.quotePriceText = moneyText(item.quotePrice)
    item.quoteTotalText = moneyText(item.quoteTotal)
    item.quoteExpired = !!(
      item.quoteValidUntil && String(item.quoteValidUntil).slice(0, 10) < todayStr()
    )
    return item
  },

  selectFilter: function (e) {
    const status = Number(e.currentTarget.dataset.status)
    if (status === this.data.filter) return
    const that = this
    this.setData({ filter: status }, function () {
      that.loadList(true)
    })
  },

  goProduct: function (e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({ url: '/pages/detail/detail?id=' + id })
    }
  },

  contactLead: function (e) {
    const contact = (e.currentTarget.dataset.contact || '').trim()
    if (!contact) {
      wx.showToast({ title: '客户未留联系方式', icon: 'none' })
      return
    }
    const digits = contact.replace(/[^\d]/g, '')
    if (/^1\d{10}$/.test(digits)) {
      wx.makePhoneCall({
        phoneNumber: digits,
        fail: function () {}
      })
      return
    }
    wx.setClipboardData({
      data: contact,
      success: function () {
        wx.showToast({ title: '联系方式已复制', icon: 'none' })
      }
    })
  },

  openQuote: function (e) {
    const index = Number(e.currentTarget.dataset.index)
    const item = this.data.list[index]
    if (!item) return
    this.setData({
      showQuote: true,
      quotingId: item.id,
      quoting: item,
      quoteForm: {
        quotePrice: item.quotePrice == null ? '' : String(item.quotePrice),
        quoteTotal: item.quoteTotal == null ? '' : String(item.quoteTotal),
        quoteRemark: item.quoteRemark || '',
        quoteValidUntil: item.quoteValidUntil || ''
      }
    })
  },

  closeQuote: function () {
    if (this.data.submitting) return
    this.setData({ showQuote: false })
  },

  noop: function () {},

  onQuoteInput: function (e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['quoteForm.' + field]: e.detail.value })
  },

  onValidUntilChange: function (e) {
    this.setData({ 'quoteForm.quoteValidUntil': e.detail.value })
  },

  submitQuote: function () {
    if (this.data.submitting) return
    const form = this.data.quoteForm
    const priceEmpty = form.quotePrice === '' || form.quotePrice === null
    const totalEmpty = form.quoteTotal === '' || form.quoteTotal === null
    if (priceEmpty && totalEmpty) {
      wx.showToast({ title: '请填写单价或总价', icon: 'none' })
      return
    }
    const quotePrice = priceEmpty ? null : Number(form.quotePrice)
    const quoteTotal = totalEmpty ? null : Number(form.quoteTotal)
    if ((quotePrice !== null && (isNaN(quotePrice) || quotePrice < 0))
      || (quoteTotal !== null && (isNaN(quoteTotal) || quoteTotal < 0))) {
      wx.showToast({ title: '报价金额格式不正确', icon: 'none' })
      return
    }
    const that = this
    this.setData({ submitting: true })
    merchant.merchantRequest('/inquiry/quote/' + this.data.quotingId, 'POST', {
      quotePrice: quotePrice,
      quoteTotal: quoteTotal,
      quoteRemark: form.quoteRemark || '',
      quoteValidUntil: form.quoteValidUntil || ''
    })
      .then(function () {
        that.setData({ submitting: false, showQuote: false })
        wx.showToast({ title: '报价已发送', icon: 'success' })
        that.loadCounts()
        that.loadList(true)
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  },

  handleLead: function (e) {
    const id = e.currentTarget.dataset.id
    const that = this
    wx.showModal({
      title: '标记跟进',
      content: '将该线索标记为跟进中，方便区分已处理的客户。',
      success: function (res) {
        if (!res.confirm) return
        merchant.merchantRequest('/inquiry/handle/' + id, 'POST')
          .then(function () {
            wx.showToast({ title: '已标记跟进', icon: 'success' })
            that.loadCounts()
            that.loadList(true)
          })
          .catch(function () {})
      }
    })
  },

  removeLead: function (e) {
    const id = e.currentTarget.dataset.id
    const that = this
    wx.showModal({
      title: '删除线索',
      content: '删除后不可恢复，确定删除该条询价线索？',
      confirmText: '删除',
      confirmColor: '#f56c6c',
      success: function (res) {
        if (!res.confirm) return
        merchant.merchantRequest('/inquiry/remove/' + id, 'POST')
          .then(function () {
            wx.showToast({ title: '已删除', icon: 'success' })
            that.loadCounts()
            that.loadList(true)
          })
          .catch(function () {})
      }
    })
  }
})
