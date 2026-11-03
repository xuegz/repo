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

  goApply: function () {
    wx.navigateTo({ url: '/pages/apply/apply' })
  },

  goShops: function () {
    wx.navigateTo({ url: '/pages/shops/shops' })
  },

  goInquiries: function () {
    wx.navigateTo({ url: '/pages/inquiry/inquiry' })
  },

  goFavorites: function () {
    wx.navigateTo({ url: '/pages/favorites/favorites' })
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
      content: '1. 首页按品类浏览建材产品；2. 进入产品详情可查看档口电话并直接拨号；3. 在产品详情底部可收藏产品、向商户发起询价；4.「我的询价」里能查看商户报价，「我的收藏」可找回产品和商户。',
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
      content: '确定清除浏览记录？收藏与询价记录不受影响。',
      success: function (res) {
        if (res.confirm) {
          request('/history/clear', 'POST', { openid: getOpenid() })
            .then(function () {
              that.setData({ history: [] })
              wx.showToast({ title: '已清除', icon: 'success' })
            })
            .catch(function () {})
        }
      }
    })
  }
})
