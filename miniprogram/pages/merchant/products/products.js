const merchant = require('../../../utils/merchant')

const STATUS_META = {
  0: { text: '待审核', cls: 'pending' },
  1: { text: '在售', cls: 'online' },
  2: { text: '未通过', cls: 'rejected' },
  3: { text: '已下架', cls: 'offline' }
}

function priceText(price, unit) {
  if (price === null || price === undefined || price === '') {
    return '面议'
  }
  const num = Number(price)
  if (isNaN(num) || num <= 0) {
    return '面议'
  }
  const text = num % 1 === 0 ? String(num) : num.toFixed(2)
  return '¥' + text + (unit ? '/' + unit : '')
}

Page({
  data: {
    status: -1,
    tabs: [],
    list: [],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    finished: false,
    loading: false,
    loaded: false
  },

  onLoad: function (options) {
    const raw = options && options.status
    const status = raw === undefined || raw === '' ? -1 : Number(raw)
    this.setData({ status: isNaN(status) ? -1 : status })
  },

  onShow: function () {
    this.loadCounts()
    this.loadList(true)
  },

  onPullDownRefresh: function () {
    const that = this
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
    merchant.merchantRequest('/product/status-count')
      .then(function (data) {
        const counts = data || {}
        let all = 0
        ;[0, 1, 2, 3].forEach(function (key) {
          all += Number(counts[key] || 0)
        })
        const tabs = [
          { status: -1, label: '全部', count: all },
          { status: 0, label: '待审核', count: Number(counts[0] || 0) },
          { status: 1, label: '在售', count: Number(counts[1] || 0) },
          { status: 2, label: '未通过', count: Number(counts[2] || 0) },
          { status: 3, label: '已下架', count: Number(counts[3] || 0) }
        ]
        that.setData({ tabs: tabs })
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
    if (this.data.status !== -1) {
      params.status = this.data.status
    }
    this.setData({ loading: true })
    merchant.merchantRequest('/product/page', 'GET', params)
      .then(function (data) {
        if (seq !== that.listSeq) return
        const records = ((data && data.records) || []).map(that.decorate)
        that.setData({
          list: reset ? records : that.data.list.concat(records),
          total: Number((data && data.total) || 0),
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
    const meta = STATUS_META[item.status] || { text: '未知', cls: 'offline' }
    item.statusText = meta.text
    item.statusClass = meta.cls
    item.priceText = priceText(item.price, item.unit)
    item.cover = item.imageList && item.imageList.length ? item.imageList[0] : ''
    return item
  },

  selectTab: function (e) {
    const status = Number(e.currentTarget.dataset.status)
    if (status === this.data.status) return
    const that = this
    this.setData({ status: status }, function () {
      that.loadList(true)
    })
  },

  goEdit: function (e) {
    wx.navigateTo({ url: '/pages/merchant/edit/edit?id=' + e.currentTarget.dataset.id })
  },

  goNew: function () {
    wx.navigateTo({ url: '/pages/merchant/edit/edit' })
  },

  toggleShelf: function (e) {
    const id = e.currentTarget.dataset.id
    const status = Number(e.currentTarget.dataset.status)
    const that = this
    const action = status === 1 ? '上架' : '下架'
    wx.showModal({
      title: action + '产品',
      content: status === 1 ? '上架后顾客可在小程序中看到该产品。' : '下架后顾客将无法看到该产品，可随时重新上架。',
      success: function (res) {
        if (!res.confirm) return
        wx.showLoading({ title: '处理中', mask: true })
        merchant.merchantRequest('/product/status', 'POST', { id: id, status: status })
          .then(function () {
            wx.hideLoading()
            wx.showToast({ title: '已' + action, icon: 'success' })
            that.loadCounts()
            that.loadList(true)
          })
          .catch(function () {
            wx.hideLoading()
          })
      }
    })
  },

  removeProduct: function (e) {
    const id = e.currentTarget.dataset.id
    const that = this
    wx.showModal({
      title: '删除产品',
      content: '删除后不可恢复，确定删除该产品？',
      confirmText: '删除',
      confirmColor: '#f56c6c',
      success: function (res) {
        if (!res.confirm) return
        wx.showLoading({ title: '处理中', mask: true })
        merchant.merchantRequest('/product/remove/' + id, 'POST')
          .then(function () {
            wx.hideLoading()
            wx.showToast({ title: '已删除', icon: 'success' })
            that.loadCounts()
            that.loadList(true)
          })
          .catch(function () {
            wx.hideLoading()
          })
      }
    })
  }
})
