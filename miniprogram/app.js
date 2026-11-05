const { ensureLogin, checkQuoteNotice } = require('./utils/request')

App({
  globalData: {
    // 当前指向 ECS 生产环境；仅开发版/体验版可用（需勾选"不校验合法域名"）
    baseUrl: 'http://47.111.99.105'
  },
  onLaunch: function () {
    // 启动即完成微信登录，后续 openid 相关请求直接复用
    ensureLogin().catch(function () {})
    this.refreshQuoteNotice()
  },
  onShow: function () {
    this.refreshQuoteNotice()
    const that = this
    if (this.quoteTimer) clearInterval(this.quoteTimer)
    this.quoteTimer = setInterval(function () {
      checkQuoteNotice()
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
  }
})
