const { openidRequest, checkChatNotice } = require('../../utils/request')

function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

function timeText(value) {
  if (!value) return ''
  const date = new Date(String(value).replace(/-/g, '/').replace('T', ' ').split('.')[0])
  if (isNaN(date.getTime())) return ''
  const now = new Date()
  const sameDay = date.toDateString() === now.toDateString()
  if (sameDay) return pad(date.getHours()) + ':' + pad(date.getMinutes())
  const yesterday = new Date(now.getTime() - 86400000)
  if (date.toDateString() === yesterday.toDateString()) return '昨天'
  return (date.getMonth() + 1) + '-' + date.getDate()
}

Page({
  data: {
    list: [],
    loading: true
  },

  onShow: function () {
    this.load()
    this.startTimer()
    checkChatNotice()
  },

  onHide: function () {
    this.stopTimer()
  },

  onUnload: function () {
    this.stopTimer()
  },

  onPullDownRefresh: function () {
    const that = this
    this.load().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  onShareAppMessage: function () {
    return { title: '材路通', path: '/pages/index/index' }
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
    return openidRequest('/chat/list', 'GET', {})
      .then(function (rows) {
        that.setData({ list: that.decorate(rows || []), loading: false })
        checkChatNotice()
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  decorate: function (rows) {
    return rows.map(function (item) {
      const name = item.merchantName || '商户'
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
      url: '/pages/chat/chat?id=' + item.conversationId
        + '&merchantId=' + (item.merchantId || 0)
        + '&merchantName=' + encodeURIComponent(item.merchantName || '商户')
    })
  },

  goHome: function () {
    wx.switchTab({ url: '/pages/index/index' })
  }
})
