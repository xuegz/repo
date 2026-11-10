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
        that.baseMerchants = that.allMerchants
        that.setData({
          activeId: 0,
          activeName: '全部',
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
    const that = this
    const id = Number(e.currentTarget.dataset.id) || 0
    const name = e.currentTarget.dataset.name || '全部'
    this.setData({ activeId: id, activeName: name })
    if (!id) {
      this.baseMerchants = this.allMerchants || []
      this.applyFilter()
      return
    }
    const seq = (this._seq || 0) + 1
    this._seq = seq
    request('/merchants?categoryId=' + id)
      .then(function (list) {
        if (seq !== that._seq) return
        that.baseMerchants = list || []
        that.applyFilter()
      })
      .catch(function () {
        if (seq !== that._seq) return
        that.baseMerchants = []
        that.applyFilter()
      })
  },

  onKeywordInput: function (e) {
    this.setData({ keyword: e.detail.value })
    this.applyFilter()
  },

  applyFilter: function () {
    const keyword = (this.data.keyword || '').trim().toLowerCase()
    const list = (this.baseMerchants || []).filter(function (m) {
      if (keyword) {
        const text = ((m.name || '') + ' ' + (m.market || '')).toLowerCase()
        if (text.indexOf(keyword) < 0) {
          return false
        }
      }
      return true
    })
    this.setData({ list: list })
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  }
})
