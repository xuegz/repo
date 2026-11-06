const merchant = require('../../../utils/merchant')
const { uploadImage } = require('../../../utils/request')

function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

function timeText(value) {
  if (!value) return ''
  const date = new Date(String(value).replace(/-/g, '/').replace('T', ' ').split('.')[0])
  if (isNaN(date.getTime())) return ''
  return pad(date.getHours()) + ':' + pad(date.getMinutes())
}

function decorate(rows) {
  return rows.map(function (item) {
    const view = Object.assign({}, item)
    if (item.type === 'product') {
      try {
        view.product = JSON.parse(item.content)
      } catch (e) {
        view.product = null
      }
    }
    view.timeText = timeText(item.createTime)
    return view
  })
}

Page({
  data: {
    conversationId: 0,
    userName: '微信访客',
    messages: [],
    input: '',
    scrollInto: '',
    sending: false,
    loading: true,
    empty: false,
    hasMore: false,
    loadingEarlier: false
  },

  onLoad: function (options) {
    const that = this
    this.options = options || {}
    this.socket = null
    this.reconnectTimer = null
    this.pollTimer = null
    this.lastId = 0
    this.firstId = 0
    this.nearBottom = true
    this.unloaded = false
    if (!merchant.token()) {
      merchant.toLogin()
      return
    }
    const id = Number(this.options.id || 0)
    const userName = this.options.userName ? decodeURIComponent(this.options.userName) : '微信访客'
    this.setData({ conversationId: id, userName: userName })
    wx.setNavigationBarTitle({ title: userName })
    if (!id) {
      wx.showToast({ title: '缺少会话参数', icon: 'none' })
      return
    }
    this.loadMessages()
  },

  onShow: function () {
    if (this.data.conversationId) {
      this.connectWs()
      this.startPolling()
      this.markRead()
    }
  },

  onReady: function () {
    this.measureViewport()
  },

  onHide: function () {
    this.closeWs()
    this.stopPolling()
  },

  onUnload: function () {
    this.unloaded = true
    this.closeWs()
    this.stopPolling()
  },

  loadMessages: function () {
    const that = this
    merchant.merchantRequest('/chat/messages', 'GET', { conversationId: this.data.conversationId })
      .then(function (rows) {
        const list = decorate(rows || [])
        that.setData({
          messages: list,
          loading: false,
          empty: list.length === 0,
          hasMore: list.length >= 50
        })
        if (list.length) {
          that.lastId = list[list.length - 1].id
          that.firstId = list[0].id
          that.scrollToBottom()
        }
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  loadEarlier: function () {
    if (this.data.loadingEarlier || !this.data.hasMore || !this.data.conversationId) return
    const anchor = this.firstId
    if (!anchor) return
    const that = this
    this.setData({ loadingEarlier: true })
    return merchant.merchantRequest('/chat/messages', 'GET', {
      conversationId: this.data.conversationId,
      beforeId: anchor
    }).then(function (rows) {
      const older = decorate(rows || [])
      const exists = {}
      const current = that.data.messages.slice()
      current.forEach(function (item) { exists[item.id] = true })
      const added = older.filter(function (item) { return !exists[item.id] })
      const merged = added.concat(current)
      that.firstId = merged.length ? merged[0].id : anchor
      that.setData({
        messages: merged,
        loadingEarlier: false,
        hasMore: older.length >= 50,
        scrollInto: 'msg-' + anchor
      })
    }).catch(function () {
      that.setData({ loadingEarlier: false })
    })
  },

  onScroll: function (e) {
    const detail = e.detail || {}
    if (this.viewportHeight && detail.scrollHeight) {
      this.nearBottom = detail.scrollTop + this.viewportHeight >= detail.scrollHeight - 80
    }
  },

  measureViewport: function () {
    const that = this
    wx.createSelectorQuery().select('.chat-scroll').boundingClientRect(function (rect) {
      if (rect) that.viewportHeight = rect.height
    }).exec()
  },

  pullNew: function () {
    const that = this
    if (!this.data.conversationId) return
    merchant.merchantRequest('/chat/messages', 'GET', {
      conversationId: this.data.conversationId,
      afterId: this.lastId || 0
    }).then(function (rows) {
      if (!rows || !rows.length) return
      that.mergeMessages(rows)
      that.markRead()
    }).catch(function () {})
  },

  startPolling: function () {
    const that = this
    this.stopPolling()
    this.pollTimer = setInterval(function () {
      that.pullNew()
    }, 8000)
  },

  stopPolling: function () {
    if (this.pollTimer) {
      clearInterval(this.pollTimer)
      this.pollTimer = null
    }
  },

  mergeMessages: function (rows) {
    const incoming = decorate(rows)
    const exists = {}
    const list = this.data.messages.slice()
    list.forEach(function (item) { exists[item.id] = true })
    const added = incoming.filter(function (item) { return !exists[item.id] })
    if (!added.length) return
    const merged = list.concat(added).sort(function (a, b) { return a.id - b.id })
    this.setData({ messages: merged, empty: false })
    this.lastId = merged[merged.length - 1].id
    // 商户正在上翻历史时不强行拉回底部
    const selfSent = added.some(function (item) { return item.sender === 'merchant' })
    if (this.nearBottom !== false || selfSent) {
      this.scrollToBottom()
    }
  },

  scrollToBottom: function () {
    const list = this.data.messages
    if (!list.length) return
    this.setData({ scrollInto: 'msg-' + list[list.length - 1].id })
  },

  markRead: function () {
    merchant.merchantRequest('/chat/read', 'POST', { conversationId: this.data.conversationId })
      .catch(function () {})
  },

  connectWs: function () {
    const that = this
    const token = merchant.token()
    if (!token || this.socket) return
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
    if (this.unloaded || this.reconnectTimer) return
    this.reconnectTimer = setTimeout(function () {
      that.reconnectTimer = null
      that.connectWs()
    }, 5000)
  },

  onPush: function (raw) {
    let data
    try {
      data = JSON.parse(raw)
    } catch (e) {
      return
    }
    if (!data || data.event !== 'message') return
    if (Number(data.conversationId) !== Number(this.data.conversationId)) return
    this.mergeMessages([data.message])
    this.markRead()
  },

  onInput: function (e) {
    this.setData({ input: e.detail.value })
  },

  sendText: function () {
    const text = (this.data.input || '').trim()
    if (!text) return
    const that = this
    this.doSend('text', text).then(function (msg) {
      if (msg) that.setData({ input: '' })
    })
  },

  doSend: function (type, content, productId) {
    const that = this
    if (this.data.sending || !this.data.conversationId) {
      return Promise.resolve(null)
    }
    this.setData({ sending: true })
    return merchant.merchantRequest('/chat/send', 'POST', {
      conversationId: this.data.conversationId,
      type: type,
      content: content || '',
      productId: productId || 0
    }).then(function (msg) {
      that.setData({ sending: false })
      that.mergeMessages([msg])
      return msg
    }).catch(function () {
      that.setData({ sending: false })
      return null
    })
  },

  chooseImage: function () {
    const that = this
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: function (res) {
        const file = res.tempFiles && res.tempFiles[0]
        if (!file) return
        wx.showLoading({ title: '上传中', mask: true })
        uploadImage(file.tempFilePath).then(function (url) {
          wx.hideLoading()
          that.doSend('image', url)
        }).catch(function () {
          wx.hideLoading()
        })
      }
    })
  },

  previewImage: function (e) {
    const url = e.currentTarget.dataset.url
    if (url) {
      wx.previewImage({ urls: [url], current: url })
    }
  },

  openProduct: function (e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({ url: '/pages/detail/detail?id=' + id })
    }
  }
})
