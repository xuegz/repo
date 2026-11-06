const {
  HOST,
  ensureLogin,
  openidRequest,
  uploadImage,
  checkChatNotice,
  getSubscribeConfig,
  requestSubscribe
} = require('../../utils/request')

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
    merchantId: 0,
    merchantName: '商家',
    merchantLogo: '',
    messages: [],
    input: '',
    scrollInto: '',
    sending: false,
    loading: true,
    empty: false,
    canSend: false
  },

  onLoad: function (options) {
    this.options = options || {}
    this.socket = null
    this.reconnectTimer = null
    this.pollTimer = null
    this.lastId = 0
    this.unloaded = false
    this.subscribeAsked = false
    this.init()
  },

  onShow: function () {
    if (this.data.conversationId) {
      this.connectWs()
      this.startPolling()
      this.markRead()
    }
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

  init: function () {
    const that = this
    const options = this.options
    const fallbackName = options.merchantName ? decodeURIComponent(options.merchantName) : '商家'
    this.setData({ merchantName: fallbackName })
    ensureLogin().then(function (openid) {
      that.openid = openid
      if (options.id && Number(options.id) > 0) {
        that.setData({
          conversationId: Number(options.id),
          merchantId: Number(options.merchantId || 0)
        })
        that.afterOpen()
        return
      }
      if (options.merchantId && Number(options.merchantId) > 0) {
        return openidRequest('/chat/open', 'POST', { merchantId: Number(options.merchantId) })
          .then(function (data) {
            that.setData({
              conversationId: data.conversationId,
              merchantId: data.merchantId,
              merchantName: data.merchantName || fallbackName,
              merchantLogo: data.merchantLogo || ''
            })
            wx.setNavigationBarTitle({ title: data.merchantName || fallbackName })
            that.afterOpen()
          })
      }
      wx.showToast({ title: '缺少会话参数', icon: 'none' })
    }).catch(function () {})
  },

  afterOpen: function () {
    this.loadMessages()
    this.connectWs()
    this.startPolling()
    this.markRead()
    const productId = Number(this.options.productId || 0)
    if (productId > 0) {
      this.doSend('product', '', productId)
    }
  },

  loadMessages: function (silent) {
    const that = this
    if (!silent) {
      this.setData({ loading: true })
    }
    return openidRequest('/chat/messages', 'GET', { conversationId: this.data.conversationId })
      .then(function (rows) {
        const list = decorate(rows || [])
        that.setData({ messages: list, loading: false, empty: list.length === 0 })
        if (list.length) {
          that.lastId = list[list.length - 1].id
          that.scrollToBottom()
        }
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  pullNew: function () {
    const that = this
    if (!this.data.conversationId) return
    openidRequest('/chat/messages', 'GET', {
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
    this.scrollToBottom()
  },

  scrollToBottom: function () {
    const list = this.data.messages
    if (!list.length) return
    this.setData({ scrollInto: 'msg-' + list[list.length - 1].id })
  },

  markRead: function () {
    if (!this.data.conversationId) return
    openidRequest('/chat/read', 'POST', { conversationId: this.data.conversationId })
      .then(function () { checkChatNotice() })
      .catch(function () {})
  },

  wsUrl: function () {
    return HOST.replace(/^http/, 'ws') + '/ws/chat?role=user&openid=' + encodeURIComponent(this.openid || '')
  },

  connectWs: function () {
    const that = this
    if (!this.openid || this.socket) return
    const task = wx.connectSocket({ url: this.wsUrl() })
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
    checkChatNotice()
  },

  onInput: function (e) {
    this.setData({ input: e.detail.value })
  },

  prepareSubscribe: function () {
    if (this.subscribeAsked) {
      return Promise.resolve()
    }
    this.subscribeAsked = true
    return getSubscribeConfig().then(function (config) {
      if (!config || !config.enabled || !config.templateId) return
      return requestSubscribe(config.templateId)
    }).catch(function () {})
  },

  sendText: function () {
    const text = (this.data.input || '').trim()
    if (!text) return
    const that = this
    this.prepareSubscribe().then(function () {
      that.doSend('text', text).then(function (msg) {
        if (msg) that.setData({ input: '' })
      })
    })
  },

  doSend: function (type, content, productId) {
    const that = this
    if (this.data.sending || !this.data.conversationId) {
      return Promise.resolve(null)
    }
    this.setData({ sending: true })
    return openidRequest('/chat/send', 'POST', {
      conversationId: this.data.conversationId,
      type: type,
      content: content || '',
      productId: productId || 0
    }).then(function (msg) {
      that.setData({ sending: false })
      that.mergeMessages([msg])
      that.markRead()
      checkChatNotice()
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
  },

  onBubbleLongPress: function (e) {
    if (e.currentTarget.dataset.sender !== 'merchant') return
    this.reportMerchant()
  },

  reportMerchant: function () {
    const that = this
    wx.showActionSheet({
      itemList: ['举报该会话'],
      success: function () {
        wx.showModal({
          title: '确认举报',
          content: '平台会核查该会话内容，确认提交举报？',
          confirmText: '提交举报',
          success: function (res) {
            if (!res.confirm) return
            openidRequest('/chat/report', 'POST', {
              conversationId: that.data.conversationId,
              reason: '用户举报会话内容'
            }).then(function () {
              wx.showToast({ title: '已提交，平台会尽快核查', icon: 'none' })
            }).catch(function () {})
          }
        })
      }
    })
  }
})
