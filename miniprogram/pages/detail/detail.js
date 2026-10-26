const { request, reportView, reportForward } = require('../../utils/request')

Page({
  data: {
    product: {}
  },

  onLoad: function (options) {
    if (options && options.id) {
      const id = Number(options.id)
      this.loadDetail(id)
      // 浏览即上报，用于商户端"被浏览"统计
      reportView(1, id)
    }
  },

  loadDetail: function (id) {
    const that = this
    request('/product?id=' + id)
      .then(function (data) {
        that.setData({ product: data })
        wx.setNavigationBarTitle({ title: data.name || '产品详情' })
      })
      .catch(function () {})
  },

  callPhone: function () {
    const phone = this.data.product.merchantPhone
    if (!phone) {
      return
    }
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: function () {}
    })
  },

  goShop: function () {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + this.data.product.merchantId })
  },

  /** 转发时上报，用于商户端"被转发"统计 */
  onShareAppMessage: function () {
    const product = this.data.product
    if (product.id) {
      reportForward(1, product.id)
    }
    return {
      title: product.name || '建材产品',
      path: '/pages/detail/detail?id=' + product.id
    }
  }
})
