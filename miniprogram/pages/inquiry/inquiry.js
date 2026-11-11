const {
  openidRequest,
  reportCall,
  getMemberAccess,
  guideMembership,
  getSubscribeConfig,
  requestSubscribe
} = require('../../utils/request')

const STATUS_META = {
  0: { text: '待报价', cls: 'pending' },
  1: { text: '已报价', cls: 'quoted' },
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

function markQuoteSeen(list) {
  let latest = 0
  ;(list || []).forEach(function (item) {
    if (!item.quoteTime) return
    const t = new Date(item.quoteTime).getTime()
    if (t && t > latest) latest = t
  })
  if (latest) {
    wx.setStorageSync('quoteSeenTime', latest)
  }
  wx.hideTabBarRedDot({ index: 3, fail: function () {} })
}

Page({
  data: {
    filter: -1,
    list: [],
    loaded: false,
    subscribeTipShow: false
  },

  onLoad: function () {
    const that = this
    getSubscribeConfig().then(function (config) {
      that.subscribeTemplateId = config && config.enabled ? config.templateId : ''
      if (that.subscribeTemplateId) {
        that.refreshSubscribeTip()
      }
    })
  },

  onShow: function () {
    this.loadList()
  },

  /** 有待报价询价时才提示开启提醒（一次授权只能推送一条） */
  refreshSubscribeTip: function () {
    if (this.reminderGranted) {
      return
    }
    const hasPending = (this.data.list || []).some(function (item) {
      return item.status === 0
    })
    this.setData({ subscribeTipShow: !!this.subscribeTemplateId && hasPending })
  },

  enableReminder: function () {
    const that = this
    requestSubscribe(this.subscribeTemplateId).then(function (status) {
      if (status === 'accept') {
        that.reminderGranted = true
        that.setData({ subscribeTipShow: false })
        wx.showToast({ title: '已开启报价提醒', icon: 'success' })
      } else {
        wx.showToast({ title: '未开启，可稍后再试', icon: 'none' })
      }
    })
  },

  onPullDownRefresh: function () {
    this.loadList(function () {
      wx.stopPullDownRefresh()
    })
  },

  selectFilter: function (e) {
    const status = Number(e.currentTarget.dataset.status)
    const that = this
    this.setData({ filter: status }, function () {
      that.loadList()
    })
  },

  loadList: function (cb) {
    const that = this
    const params = {}
    if (this.data.filter !== -1) {
      params.status = this.data.filter
    }
    openidRequest('/inquiries', 'GET', params)
      .then(function (data) {
        that.setData({ list: that.decorate(data || []), loaded: true })
        that.refreshSubscribeTip()
        markQuoteSeen(data)
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  decorate: function (list) {
    const today = todayStr()
    return list.map(function (item) {
      const meta = STATUS_META[item.status] || { text: '未知', cls: 'follow' }
      item.statusText = meta.text
      item.statusClass = meta.cls
      item.timeText = formatTime(item.createTime)
      item.quoteTimeText = formatTime(item.quoteTime)
      item.hasQuote = item.quotePrice != null || item.quoteTotal != null
      item.quotePriceText = moneyText(item.quotePrice)
      item.quoteTotalText = moneyText(item.quoteTotal)
      item.quoteExpired = !!(
        item.quoteValidUntil && String(item.quoteValidUntil).slice(0, 10) < today
      )
      return item
    })
  },

  goProduct: function (e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({ url: '/pages/detail/detail?id=' + id })
    }
  },

  goShop: function (e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({ url: '/pages/shop/shop?id=' + id })
    }
  },

  callMerchant: function (e) {
    const phone = e.currentTarget.dataset.phone
    if (!phone) {
      const that = this
      getMemberAccess(true).then(function (access) {
        if (access && access.memberActive) {
          that.loadList()
          wx.showToast({ title: '该商户暂未留电话', icon: 'none' })
          return
        }
        guideMembership(access)
      })
      return
    }
    reportCall(2, e.currentTarget.dataset.id)
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: function () {}
    })
  }
})
