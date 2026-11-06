const merchant = require('../../../utils/merchant')

Page({
  data: {
    loading: true,
    shop: {},
    stats: {
      onSale: 0,
      pending: 0,
      views: 0,
      leads: 0
    }
  },

  onShow: function () {
    this.syncSession()
  },

  onPullDownRefresh: function () {
    this.syncSession().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  syncSession: function () {
    const that = this
    return merchant.refreshSession()
      .then(function (data) {
        if (!data || !data.bound) {
          wx.showToast({ title: (data && data.reason) || '请先绑定商户账号', icon: 'none' })
          setTimeout(function () {
            merchant.toLogin()
          }, 800)
          return
        }
        if (data.mustChangePassword) {
          wx.redirectTo({ url: merchant.LOGIN_PAGE + '?force=1' })
          return
        }
        that.setData({ shop: data, loading: false })
        that.loadStats()
      })
      .catch(function () {
        that.setData({ loading: false })
        wx.showToast({ title: '登录状态获取失败，请下拉刷新重试', icon: 'none' })
      })
  },

  loadStats: function () {
    const that = this
    merchant.merchantRequest('/stat/overview')
      .then(function (data) {
        const overview = data || {}
        that.setData({
          'stats.onSale': Number(overview.productOnSale || 0),
          'stats.pending': Number(overview.productPending || 0),
          'stats.views': Number(overview.viewTotal || 0)
        })
      })
      .catch(function () {})
    merchant.merchantRequest('/inquiry/counts')
      .then(function (data) {
        that.setData({ 'stats.leads': Number((data && data.pending) || 0) })
      })
      .catch(function () {})
  },

  goProducts: function (e) {
    const status = e.currentTarget.dataset.status
    let url = '/pages/merchant/products/products'
    if (status !== undefined && status !== '') {
      url += '?status=' + status
    }
    wx.navigateTo({ url: url })
  },

  goEdit: function () {
    wx.navigateTo({ url: '/pages/merchant/edit/edit' })
  },

  goInquiries: function () {
    wx.navigateTo({ url: '/pages/merchant/inquiries/inquiries' })
  },

  goShopEdit: function () {
    wx.navigateTo({ url: '/pages/merchant/shop/shop' })
  },

  goShop: function () {
    const id = this.data.shop.merchantId
    if (id) {
      wx.navigateTo({ url: '/pages/shop/shop?id=' + id })
    }
  },

  unbind: function () {
    wx.showModal({
      title: '解绑商户身份',
      content: '解绑后当前微信将退出商家中心，产品和询价数据不受影响，可随时重新绑定。',
      confirmText: '解绑',
      confirmColor: '#f56c6c',
      success: function (res) {
        if (!res.confirm) return
        wx.showLoading({ title: '处理中', mask: true })
        merchant.unbind()
          .then(function () {
            wx.hideLoading()
            merchant.clearSession()
            wx.showToast({ title: '已解绑', icon: 'success' })
            setTimeout(function () {
              wx.switchTab({ url: '/pages/mine/mine' })
            }, 600)
          })
          .catch(function () {
            wx.hideLoading()
          })
      }
    })
  }
})
