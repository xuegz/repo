const { openidRequest, ensureLogin, uploadImage } = require('../../utils/request')
const merchant = require('../../utils/merchant')

Page({
  data: {
    history: [],
    servicePhone: '400-000-0000',
    loginState: 'loading',
    nickname: '',
    avatar: '',
    merchantBound: false,
    merchantShop: {}
  },

  onShow: function () {
    this.loadLogin()
    this.loadHistory()
    this.loadMerchant()
  },

  loadMerchant: function () {
    const that = this
    merchant.refreshSession()
      .then(function (data) {
        const bound = !!(data && data.bound)
        that.setData({
          merchantBound: bound,
          merchantShop: bound ? data : {}
        })
      })
      .catch(function () {
        that.setData({ merchantBound: false, merchantShop: {} })
      })
  },

  goMerchant: function () {
    const url = this.data.merchantBound
      ? '/pages/merchant/home/home'
      : '/pages/merchant/login/login'
    wx.navigateTo({ url: url })
  },

  loadLogin: function () {
    const that = this
    ensureLogin()
      .then(function () {
        that.setData({ loginState: 'ok' })
        that.loadProfile()
      })
      .catch(function () {
        that.setData({ loginState: 'fail' })
      })
  },

  loadProfile: function () {
    const that = this
    openidRequest('/profile', 'GET')
      .then(function (data) {
        if (!data) return
        that.setData({
          nickname: data.nickname || '',
          avatar: data.avatar || ''
        })
      })
      .catch(function () {})
  },

  onChooseAvatar: function (e) {
    const tempPath = e.detail.avatarUrl
    if (!tempPath) return
    const that = this
    wx.showLoading({ title: '上传中', mask: true })
    uploadImage(tempPath)
      .then(function (url) {
        that.setData({ avatar: url })
        wx.hideLoading()
        that.saveProfile({ avatar: url })
      })
      .catch(function (err) {
        wx.hideLoading()
        wx.showToast({ title: (err && err.message) || '头像上传失败', icon: 'none' })
      })
  },

  onNicknameBlur: function (e) {
    const nickname = (e.detail.value || '').trim()
    if (nickname === this.data.nickname) return
    this.setData({ nickname: nickname })
    this.saveProfile({ nickname: nickname })
  },

  saveProfile: function (data) {
    openidRequest('/profile', 'POST', data)
      .then(function () {
        wx.showToast({ title: '已保存', icon: 'success' })
      })
      .catch(function () {})
  },

  doLogin: function () {
    if (this.data.loginState === 'loading') return
    this.setData({ loginState: 'loading' })
    this.loadLogin()
  },

  loadHistory: function () {
    const that = this
    openidRequest('/history', 'GET')
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

  goFeedback: function () {
    wx.navigateTo({ url: '/pages/feedback/feedback' })
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
          openidRequest('/history/clear', 'POST')
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
