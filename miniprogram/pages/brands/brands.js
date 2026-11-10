const { request } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    categories: [],
    list: [],
    activeId: 0,
    activeName: '全部',
    loaded: false
  },

  onLoad: function () {
    this.loadData()
  },

  onPullDownRefresh: function () {
    this.loadData(function () {
      wx.stopPullDownRefresh()
    })
  },

  loadData: function (cb) {
    const that = this
    Promise.all([request('/categories'), request('/merchants')])
      .then(function (res) {
        that.allMerchants = res[1] || []
        that.setData({
          categories: res[0] || [],
          loaded: true
        })
        that.applyFilter()
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  selectCategory: function (e) {
    const id = Number(e.currentTarget.dataset.id) || 0
    const name = e.currentTarget.dataset.name || '全部'
    this.setData({ activeId: id, activeName: name })
    this.applyFilter()
  },

  onKeywordInput: function (e) {
    this.setData({ keyword: e.detail.value })
    this.applyFilter()
  },

  applyFilter: function () {
    const that = this
    const keyword = (this.data.keyword || '').trim().toLowerCase()
    const activeId = this.data.activeId
    const list = (this.allMerchants || []).filter(function (m) {
      if (activeId && Number(m.categoryId) !== activeId) {
        return false
      }
      if (keyword) {
        const text = ((m.name || '') + ' ' + (m.market || '')).toLowerCase()
        if (text.indexOf(keyword) < 0) {
          return false
        }
      }
      return true
    })
    that.setData({ list: list })
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  }
})
