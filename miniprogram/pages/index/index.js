const { request } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    city: '西安',
    statusBarHeight: 20,
    banners: [],
    ads: [],
    categories: [],
    markets: [],
    recommends: [],
    merchants: [],
    notices: []
  },

  onLoad: function () {
    const info = wx.getWindowInfo ? wx.getWindowInfo() : wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
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
        const cats = (data.categories || []).slice(0, 7).map(function (c) {
          return { id: c.id, name: c.name, firstLetter: c.name.substring(0, 1) }
        })
        that.setData({
          banners: data.banners || [],
          ads: data.ads || [],
          categories: cats,
          markets: data.markets || [],
          recommends: data.recommends || [],
          city: (data.markets && data.markets[0] && data.markets[0].city) || '西安'
        })
      })
      .catch(function () {})
      .then(function () {
        if (cb) cb()
      })
  },

  loadMerchants: function () {
    const that = this
    request('/merchants/page?pageNum=1&pageSize=4')
      .then(function (page) {
        that.setData({
          merchants: (page && page.records) || []
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
    wx.navigateTo({ url: '/pages/brands/brands' })
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

  goAllMarkets: function () {
    wx.navigateTo({ url: '/pages/markets/markets' })
  },

  /** 广告位跳转：支持 product?keyword=xxx 与小程序内页面路径 */
  goAd: function (e) {
    const link = e.currentTarget.dataset.link || ''
    if (!link) {
      return
    }
    if (link.indexOf('product?') === 0) {
      const match = link.match(/keyword=([^&]+)/)
      if (match) {
        wx.setStorageSync('searchKeyword', decodeURIComponent(match[1]))
      }
      wx.removeStorageSync('filterCategoryId')
      wx.switchTab({ url: '/pages/product/product' })
      return
    }
    if (link.indexOf('/pages/') === 0) {
      wx.navigateTo({ url: link })
      return
    }
    wx.setClipboardData({ data: link })
  },

  /** 品类跳转：通过 storage 传参后切到 tab 页 */
  goCategory: function (e) {
    wx.removeStorageSync('searchKeyword')
    wx.setStorageSync('filterCategoryId', e.currentTarget.dataset.id)
    wx.switchTab({ url: '/pages/product/product' })
  },

  goAllProducts: function () {
    wx.removeStorageSync('searchKeyword')
    wx.removeStorageSync('filterCategoryId')
    wx.switchTab({ url: '/pages/product/product' })
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  },

  onShareAppMessage: function () {
    return {
      title: '材路通 · 西安建材市场与商户导购',
      path: '/pages/index/index'
    }
  }
})
