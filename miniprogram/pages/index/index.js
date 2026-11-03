const { request } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    banners: [],
    ads: [],
    categories: [],
    markets: [],
    recommends: [],
    merchants: [],
    merchantCount: 0,
    notices: []
  },

  onLoad: function () {
    this.loadData()
    this.loadMerchants()
    this.loadNotices()
  },

  loadNotices: function () {
    const that = this
    request('/notices')
      .then(function (data) {
        that.setData({ notices: data || [] })
      })
      .catch(function () {})
  },

  showNotice: function (e) {
    const notice = this.data.notices[e.currentTarget.dataset.index]
    if (!notice) {
      return
    }
    wx.showModal({
      title: notice.title,
      content: notice.content || '（无正文）',
      showCancel: false
    })
  },

  onPullDownRefresh: function () {
    const that = this
    this.loadData(function () {
      wx.stopPullDownRefresh()
    })
    this.loadMerchants()
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
          markets: data.markets || [],
          recommends: data.recommends || []
        })
      })
      .catch(function () {})
      .then(function () {
        if (cb) cb()
      })
  },

  loadMerchants: function () {
    const that = this
    request('/merchants')
      .then(function (data) {
        const list = data || []
        that.setData({
          merchantCount: list.length,
          merchants: list.slice(0, 4)
        })
      })
      .catch(function () {})
  },

  onKeywordInput: function (e) {
    this.setData({ keyword: e.detail.value })
  },

  goSearch: function () {
    const keyword = (this.data.keyword || '').trim()
    wx.removeStorageSync('filterCategoryId')
    if (keyword) {
      wx.setStorageSync('searchKeyword', keyword)
    } else {
      wx.removeStorageSync('searchKeyword')
    }
    wx.switchTab({ url: '/pages/product/product' })
  },

  goShops: function () {
    wx.navigateTo({ url: '/pages/shops/shops' })
  },

  goMap: function () {
    wx.navigateTo({ url: '/pages/map/map' })
  },

  goApply: function () {
    wx.navigateTo({ url: '/pages/apply/apply' })
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  },

  goMarket: function (e) {
    wx.navigateTo({ url: '/pages/market/market?id=' + e.currentTarget.dataset.id })
  },

  /** 品类跳转：通过 storage 传参后切到 tab 页 */
  goCategory: function (e) {
    wx.removeStorageSync('searchKeyword')
    wx.setStorageSync('filterCategoryId', e.currentTarget.dataset.id)
    wx.switchTab({ url: '/pages/product/product' })
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  }
})
