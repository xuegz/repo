const { request } = require('../../utils/request')

const PAGE_SIZE = 30

Page({
  data: {
    keyword: '',
    categories: [],
    list: [],
    total: 0,
    activeId: 0,
    activeName: '全部',
    loaded: false,
    loading: false,
    noMore: false
  },

  onLoad: function () {
    this._seq = 0
    this._page = 1
    this.loadCategories()
    this.reload()
  },

  onUnload: function () {
    if (this._timer) clearTimeout(this._timer)
  },

  onPullDownRefresh: function () {
    this.reload(function () {
      wx.stopPullDownRefresh()
    })
  },

  loadCategories: function () {
    const that = this
    request('/categories')
      .then(function (list) {
        that.setData({ categories: list || [] })
      })
      .catch(function () {})
  },

  /** 从第一页重新加载（切品类/搜索/下拉刷新） */
  reload: function (cb) {
    this._page = 1
    this.fetchPage(true, cb)
  },

  loadMore: function () {
    if (this.data.loading || this.data.noMore || !this.data.loaded) return
    this._page += 1
    this.fetchPage(false)
  },

  fetchPage: function (reset, cb) {
    const that = this
    const seq = (this._seq || 0) + 1
    this._seq = seq
    const params = ['pageNum=' + this._page, 'pageSize=' + PAGE_SIZE]
    const keyword = (this.data.keyword || '').trim()
    if (keyword) params.push('keyword=' + encodeURIComponent(keyword))
    if (this.data.activeId) params.push('categoryId=' + this.data.activeId)
    this.setData({ loading: true })
    request('/merchants/page?' + params.join('&'))
      .then(function (page) {
        if (seq !== that._seq) return
        const records = (page && page.records) || []
        const list = reset ? records : (that.data.list || []).concat(records)
        const total = (page && page.total) || 0
        that.setData({
          list: list,
          total: total,
          loaded: true,
          loading: false,
          noMore: list.length >= total || records.length < PAGE_SIZE
        })
      })
      .catch(function () {
        if (seq !== that._seq) return
        that.setData({
          loaded: true,
          loading: false,
          noMore: true
        })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  selectCategory: function (e) {
    const id = Number(e.currentTarget.dataset.id) || 0
    const name = e.currentTarget.dataset.name || '全部'
    if (id === this.data.activeId) return
    this.setData({ activeId: id, activeName: name, list: [], total: 0, loaded: false, noMore: false })
    this.reload()
  },

  onKeywordInput: function (e) {
    const that = this
    this.setData({ keyword: e.detail.value })
    if (this._timer) clearTimeout(this._timer)
    this._timer = setTimeout(function () {
      that.reload()
    }, 300)
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  }
})
