const { request, reportView } = require('../../utils/request')

Page({
  data: {
    merchant: {},
    products: [],
    addressText: '-'
  },

  onLoad: function (options) {
    if (options && options.id) {
      const id = Number(options.id)
      this.loadShop(id)
      // 商户被浏览统计
      reportView(2, id)
    }
  },

  loadShop: function (id) {
    const that = this
    request('/merchant?id=' + id)
      .then(function (data) {
        const m = data.merchant || {}
        const addressParts = [m.province, m.city, m.address].filter(function (s) {
          return !!s
        })
        that.setData({
          merchant: m,
          products: data.products || [],
          addressText: addressParts.length ? addressParts.join(' ') : '-'
        })
        wx.setNavigationBarTitle({ title: m.name || '商户主页' })
      })
      .catch(function () {})
  },

  callPhone: function () {
    const phone = this.data.merchant.phone
    if (!phone) {
      return
    }
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: function () {}
    })
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  },

  onShareAppMessage: function () {
    const m = this.data.merchant
    return {
      title: m.name || '建材商户',
      path: '/pages/shop/shop?id=' + m.id
    }
  }
})
