const { request } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    all: [],
    filtered: [],
    loaded: false
  },

  onLoad: function () {
    this.load()
  },

  onPullDownRefresh: function () {
    this.load(function () {
      wx.stopPullDownRefresh()
    })
  },

  load: function (cb) {
    const that = this
    request('/markets')
      .then(function (data) {
        const list = (data || []).slice().sort(function (a, b) {
          return (b.merchantCount || 0) - (a.merchantCount || 0)
        })
        that.setData({ all: list, loaded: true }, function () {
          that.applyFilter()
        })
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  onKeywordInput: function (e) {
    const that = this
    this.setData({ keyword: e.detail.value }, function () {
      that.applyFilter()
    })
  },

  clearKeyword: function () {
    const that = this
    this.setData({ keyword: '' }, function () {
      that.applyFilter()
    })
  },

  applyFilter: function () {
    const keyword = (this.data.keyword || '').trim().toLowerCase()
    const all = this.data.all || []
    const filtered = keyword
      ? all.filter(function (item) {
        return [item.name, item.address, item.city, item.intro].some(function (value) {
          return value && String(value).toLowerCase().indexOf(keyword) > -1
        })
      })
      : all
    this.setData({ filtered: filtered })
  },

  goMarket: function (e) {
    wx.navigateTo({ url: '/pages/market/market?id=' + e.currentTarget.dataset.id })
  },

  onShareAppMessage: function () {
    return {
      title: '西安建材市场大全 · 材路通',
      path: '/pages/markets/markets'
    }
  }
})
