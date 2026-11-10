const { ensureLogin, checkQuoteNotice, checkChatNotice } = require('./utils/request')

App({
  globalData: {
    // 生产 https 域名
    baseUrl: 'https://mycailutong.com'
  },
  onLaunch: function () {
    // 启动即完成微信登录，后续 openid 相关请求直接复用
    ensureLogin().catch(function () {})
    this.refreshQuoteNotice()
    this.refreshChatNotice()
  },
  onShow: function () {
    this.refreshQuoteNotice()
    this.refreshChatNotice()
    if (this.quoteTimer) clearInterval(this.quoteTimer)
    this.quoteTimer = setInterval(function () {
      checkQuoteNotice()
      checkChatNotice()
    }, 60000)
  },
  onHide: function () {
    if (this.quoteTimer) {
      clearInterval(this.quoteTimer)
      this.quoteTimer = null
    }
  },
  refreshQuoteNotice: function () {
    // 等 tabBar 渲染完成后再显示红点
    setTimeout(function () {
      checkQuoteNotice()
    }, 800)
  },
  refreshChatNotice: function () {
    setTimeout(function () {
      checkChatNotice()
    }, 900)
  }
})
