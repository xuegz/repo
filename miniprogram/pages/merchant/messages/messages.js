const merchant = require('../../../utils/merchant')
const { convTime } = require('../../../utils/format')

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
      that.connectWs()
    }).catch(function () {
      that.setData({ loading: false })
    })
  },

  onHide: function () {
    this.stopTimer()
    this.closeWs()
  },

  onUnload: function () {
    this.stopTimer()
    this.closeWs()
  },

  onPullDownRefresh: function () {
    this.load().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  startTimer: function () {
    const that = this
    this.stopTimer()
    // 15 秒兜底轮询；WS 推送到达时立即刷新
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

  // ---------------- WebSocket 实时刷新 ----------------
  connectWs: function () {
    const that = this
    const token = merchant.token()
    if (!token || this.socket) {
      return
    }
    const url = merchant.HOST.replace(/^http/, 'ws') + '/ws/chat?role=merchant&token=' + encodeURIComponent(token)
    const task = wx.connectSocket({ url: url })
    this.socket = task
    task.onMessage(function (res) {
      that.onPush(res.data)
    })
    task.onClose(function () {
      that.socket = null
      that.scheduleReconnect()
    })
    task.onError(function () {
      that.socket = null
      that.scheduleReconnect()
    })
  },

  closeWs: function () {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.socket) {
      try {
        this.socket.close()
      } catch (e) {}
      this.socket = null
    }
  },

  scheduleReconnect: function () {
    const that = this
    if (this.reconnectTimer) {
      return
    }
    this.reconnectTimer = setTimeout(function () {
      that.reconnectTimer = null
      if (that.timer) {
        // 页面仍在展示时才重连
        that.connectWs()
      }
    }, 5000)
  },

  /** 任意会话有新消息即刷新会话列表 */
  onPush: function (raw) {
    let data
    try {
      data = JSON.parse(raw)
    } catch (e) {
      return
    }
    if (data && data.event === 'message') {
      this.load(true)
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
        timeText: convTime(item.lastMessageAt),
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
