const { request } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    categories: [],
    categoryId: 0,
    list: [],
    pageNum: 1,
    pageSize: 10,
    finished: false,
    loading: false,
    loaded: false
  },

  onLoad: function () {
    this.loadCategories()
    this.loadList(true)
  },

  /** 从首页品类入口进入时，读取筛选条件 */
  onShow: function () {
    const cid = wx.getStorageSync('filterCategoryId')
    if (cid) {
      wx.removeStorageSync('filterCategoryId')
      this.setData({ categoryId: Number(cid) })
      this.loadList(true)
    }
  },

  onPullDownRefresh: function () {
    const that = this
    this.loadList(true, function () {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom: function () {
    if (!this.data.finished && !this.data.loading) {
      this.loadList(false)
    }
  },

  loadCategories: function () {
    const that = this
    request('/categories').then(function (data) {
      that.setData({ categories: data || [] })
    }).catch(function () {})
  },

  loadList: function (reset, cb) {
    if (this.data.loading) {
      return
    }
    const that = this
    if (reset) {
      this.setData({ list: [], pageNum: 1, finished: false })
    }
    this.setData({ loading: true })

    const params = { pageNum: this.data.pageNum, pageSize: this.data.pageSize }
    if (this.data.keyword) {
      params.keyword = this.data.keyword
    }
    if (this.data.categoryId) {
      params.categoryId = this.data.categoryId
    }

    request('/products', 'GET', params)
      .then(function (data) {
        const records = data.records || []
        that.setData({
          list: that.data.list.concat(records),
          pageNum: that.data.pageNum + 1,
          finished: records.length < that.data.pageSize,
          loaded: true
        })
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        that.setData({ loading: false })
        if (cb) cb()
      })
  },

  onKeywordInput: function (e) {
    this.setData({ keyword: e.detail.value })
  },

  doSearch: function () {
    this.loadList(true)
  },

  selectCategory: function (e) {
    this.setData({ categoryId: Number(e.currentTarget.dataset.id) })
    this.loadList(true)
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  }
})
