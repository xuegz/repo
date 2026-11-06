const { ensureLogin } = require('../../../utils/request')
const merchant = require('../../../utils/merchant')

const HOME_PAGE = '/pages/merchant/home/home'

Page({
  data: {
    mode: 'bind',
    openid: '',
    username: '',
    password: '',
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    shopName: '',
    force: false,
    submitting: false
  },

  onLoad: function (options) {
    const force = !!(options && options.force === '1')
    const shop = merchant.cachedShop()
    const hasToken = !!merchant.token()
    this.setData({
      force: force,
      shopName: shop.shopName || '',
      mode: force && hasToken ? 'change' : 'bind'
    })
    if (!force && hasToken && shop.merchantId) {
      wx.redirectTo({ url: HOME_PAGE })
      return
    }
    const that = this
    ensureLogin()
      .then(function (openid) {
        that.setData({ openid: openid })
      })
      .catch(function () {})
  },

  onInput: function (e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [field]: e.detail.value })
  },

  submitBind: function () {
    if (this.data.submitting) return
    const openid = this.data.openid
    const username = (this.data.username || '').trim()
    const password = this.data.password || ''
    if (!openid) {
      wx.showToast({ title: '微信登录失败，请重试', icon: 'none' })
      return
    }
    if (!username) {
      wx.showToast({ title: '请输入商户账号', icon: 'none' })
      return
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }
    const that = this
    this.setData({ submitting: true })
    merchant.bindAccount(openid, username, password)
      .then(function (data) {
        merchant.saveSession(data)
        that.setData({
          submitting: false,
          shopName: data.shopName || that.data.shopName,
          username: '',
          password: ''
        })
        if (data.mustChangePassword) {
          that.setData({ mode: 'change', oldPassword: password })
          wx.showToast({ title: '请先修改初始密码', icon: 'none' })
          return
        }
        wx.showToast({ title: '绑定成功', icon: 'success' })
        setTimeout(function () {
          wx.redirectTo({ url: HOME_PAGE })
        }, 600)
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  },

  submitChange: function () {
    if (this.data.submitting) return
    const oldPassword = this.data.oldPassword || ''
    const newPassword = this.data.newPassword || ''
    const confirmPassword = this.data.confirmPassword || ''
    if (!oldPassword) {
      wx.showToast({ title: '请输入原密码', icon: 'none' })
      return
    }
    if (newPassword.length < 6 || newPassword.length > 32) {
      wx.showToast({ title: '新密码需 6-32 位', icon: 'none' })
      return
    }
    if (newPassword !== confirmPassword) {
      wx.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
      return
    }
    if (newPassword === oldPassword) {
      wx.showToast({ title: '新密码不能与原密码相同', icon: 'none' })
      return
    }
    const that = this
    this.setData({ submitting: true })
    merchant.accountRequest('/password', 'POST', {
      oldPassword: oldPassword,
      newPassword: newPassword
    })
      .then(function () {
        merchant.markPasswordChanged()
        that.setData({ submitting: false })
        wx.showToast({ title: '密码已修改', icon: 'success' })
        setTimeout(function () {
          wx.redirectTo({ url: HOME_PAGE })
        }, 600)
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  },

  showApplyTip: function () {
    wx.showModal({
      title: '还没有商户账号？',
      content: '请在「我的 → 商户入驻」提交入驻申请，审核通过后平台会用入驻手机号开通账号，初始密码为 123456。',
      showCancel: false
    })
  },

  backMine: function () {
    wx.switchTab({ url: '/pages/mine/mine' })
  }
})
