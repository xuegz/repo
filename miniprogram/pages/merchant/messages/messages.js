const merchant = require('../../../utils/merchant')

function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

function timeText(value) {
  if (!value) return ''
  const date = new Date(String(value).replace(/-/g, '/').replace('T', ' ').split('.')[0])
  if (isNaN(date.getTime())) return ''
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return pad(date.getHours()) + ':' + pad(date.getMinutes())
  }
  const yesterday = new Date(now.getTime() - 86400000)
  if (date.toDateString() === yesterday.toDateString()) return '昨天'
  return (date.getMonth() + 1) + '-' + date.getDate()
}

Page({
  data: {
    loading: true,
    list: []
  },

  onShow: function () {
    const that = this
    merchant.refreshSession().then(function (data) {
      if (!data || !data.bound) {
        merchant.toLogin()
        return
      }
      that.load()
      that.startTimer()
    }).catch(function () {
      that.setData({ loading: false })
    })
  },

  onHide: function () {
    this.stopTimer()
  },

  onUnload: function () {
    this.stopTimer()
  },

  onPullDownRefresh: function () {
    this.load().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  startTimer: function () {
    const that = this
    this.stopTimer()
    this.timer = setInterval(function () {
      that.load(true)
    }, 15000)
  },

  stopTimer: function () {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
  },

  load: function (silent) {
    const that = this
    if (!silent) {
      this.setData({ loading: true })
    }
    return merchant.merchantRequest('/chat/list')
      .then(function (rows) {
        that.setData({ list: that.decorate(rows || []), loading: false })
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  decorate: function (rows) {
    return rows.map(function (item) {
      const name = item.userName || '微信访客'
      return Object.assign({}, item, {
        initial: name.charAt(0),
        timeText: timeText(item.lastMessageAt),
        badge: item.unread > 99 ? '99+' : String(item.unread || 0)
      })
    })
  },

  openChat: function (e) {
    const item = e.currentTarget.dataset.item
    if (!item) return
    wx.navigateTo({
      url: '/pages/merchant/chat/chat?id=' + item.conversationId
        + '&userName=' + encodeURIComponent(item.userName || '微信访客')
    })
  }
})
