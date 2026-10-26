const { request } = require('../../utils/request')

Page({
  data: {
    banners: [],
    ads: [],
    categories: [],
    recommends: []
  },

  onLoad: function () {
    this.loadData()
  },

  onPullDownRefresh: function () {
    const that = this
    this.loadData(function () {
      wx.stopPullDownRefresh()
    })
  },

  loadData: function (cb) {
    const that = this
    request('/home')
      .then(function (data) {
        const cats = (data.categories || []).slice(0, 8).map(function (c) {
          return { id: c.id, name: c.name, firstLetter: c.name.substring(0, 1) }
        })
        that.setData({
          banners: data.banners || [],
          ads: data.ads || [],
          categories: cats,
          recommends: data.recommends || []
        })
      })
      .catch(function () {})
      .then(function () {
        if (cb) cb()
      })
  },

  goProduct: function () {
    wx.switchTab({ url: '/pages/product/product' })
  },

  /** 品类跳转：通过 storage 传参后切到 tab 页 */
  goCategory: function (e) {
    wx.setStorageSync('filterCategoryId', e.currentTarget.dataset.id)
    wx.switchTab({ url: '/pages/product/product' })
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  }
})
