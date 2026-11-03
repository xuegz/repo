const { request } = require('../../utils/request')

const HISTORY_KEY = 'searchHistory'

Page({
  data: {
    keyword: '',
    categories: [],
    categoryId: 0,
    sort: '',
    list: [],
    pageNum: 1,
    pageSize: 10,
    finished: false,
    loading: false,
    loaded: false,
    history: [],
    hotWords: [],
    showPanel: true
  },

  onLoad: function () {
    this.loadCategories()
    this.setData({
      history: wx.getStorageSync(HISTORY_KEY) || []
    })
  },

  onShow: function () {
    const keyword = wx.getStorageSync('searchKeyword')
    const categoryRaw = wx.getStorageSync('filterCategoryId')
    let changed = false

    if (keyword) {
      this.setData({ keyword: keyword, categoryId: 0, showPanel: false })
      wx.removeStorageSync('searchKeyword')
      this.saveHistory(keyword)
      changed = true
    }
    if (categoryRaw !== '' && categoryRaw !== undefined && categoryRaw !== null) {
      this.setData({ categoryId: Number(categoryRaw), keyword: '', showPanel: false })
      wx.removeStorageSync('filterCategoryId')
      changed = true
    }
    if (changed || !this.data.loaded) {
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
    request('/categories')
      .then(function (data) {
        const cats = data || []
        that.setData({
          categories: cats,
          hotWords: cats.slice(0, 8).map(function (c) {
            return c.name
          })
        })
      })
      .catch(function () {})
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
    if (this.data.sort) {
      params.sort = this.data.sort
    }

    request('/products', 'GET', params)
      .then(function (data) {
        const records = data.records || []
        that.setData({
          list: that.data.list.concat(records),
          pageNum: that.data.pageNum + 1,
          finished: records.length < that.data.pageSize,
          loaded: true,
          showPanel: !that.data.keyword && !that.data.categoryId && records.length === 0
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

  saveHistory: function (word) {
    if (!word) {
      return
    }
    let history = wx.getStorageSync(HISTORY_KEY) || []
    history = history.filter(function (w) {
      return w !== word
    })
    history.unshift(word)
    history = history.slice(0, 10)
    wx.setStorageSync(HISTORY_KEY, history)
    this.setData({ history: history })
  },

  doSearch: function () {
    const word = (this.data.keyword || '').trim()
    if (!word) {
      wx.showToast({ title: '请输入搜索内容', icon: 'none' })
      return
    }
    this.saveHistory(word)
    this.setData({ showPanel: false })
    this.loadList(true)
  },

  tapWord: function (e) {
    const word = e.currentTarget.dataset.word
    this.saveHistory(word)
    this.setData({ keyword: word, showPanel: false })
    this.loadList(true)
  },

  clearHistory: function () {
    wx.removeStorageSync(HISTORY_KEY)
    this.setData({ history: [] })
  },

  setSort: function (e) {
    this.setData({ sort: e.currentTarget.dataset.sort })
    this.loadList(true)
  },

  selectCategory: function (e) {
    this.setData({
      categoryId: Number(e.currentTarget.dataset.id),
      showPanel: false
    })
    this.loadList(true)
  },

  goShops: function () {
    wx.navigateTo({ url: '/pages/shops/shops' })
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  }
})
