const { request, getOpenid } = require('../../utils/request')

Page({
  data: {
    history: [],
    servicePhone: '400-000-0000'
  },

  onShow: function () {
    this.loadHistory()
  },

  loadHistory: function () {
    const that = this
    const openid = getOpenid()
    request('/history?openid=' + openid)
      .then(function (data) {
        that.setData({ history: data || [] })
      })
      .catch(function () {})
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  },

  callService: function () {
    wx.makePhoneCall({
      phoneNumber: this.data.servicePhone,
      fail: function () {}
    })
  },

  showTutorial: function () {
    wx.showModal({
      title: '使用教程',
      content: '1. 首页按品类浏览建材产品；2. 进入产品详情可查看档口电话并直接拨号；3. 右上角转发给同行即可快速调货；4. 个人中心可查看浏览记录。',
      showCancel: false
    })
  },

  showDisclaimer: function () {
    wx.showModal({
      title: '免责说明',
      content: '平台仅提供信息展示与撮合服务，产品信息由入驻商户自行发布，请交易前自行核实资质与价格。',
      showCancel: false
    })
  },

  clearHistory: function () {
    const that = this
    wx.showModal({
      title: '提示',
      content: '确定清除本机浏览记录？',
      success: function (res) {
        if (res.confirm) {
          wx.removeStorageSync('openid')
          that.setData({ history: [] })
          wx.showToast({ title: '已清除', icon: 'success' })
        }
      }
    })
  }
})
