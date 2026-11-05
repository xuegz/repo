const { openidRequest, toggleFavorite } = require('../../utils/request')

Page({
  data: {
    type: 1,
    list: [],
    loaded: false
  },

  onShow: function () {
    this.loadList()
  },

  onPullDownRefresh: function () {
    this.loadList(function () {
      wx.stopPullDownRefresh()
    })
  },

  selectType: function (e) {
    const type = Number(e.currentTarget.dataset.type)
    if (type === this.data.type) {
      return
    }
    const that = this
    this.setData({ type: type, list: [], loaded: false }, function () {
      that.loadList()
    })
  },

  loadList: function (cb) {
    const that = this
    openidRequest('/favorites', 'GET', { targetType: this.data.type })
      .then(function (data) {
        that.setData({ list: data || [], loaded: true })
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  removeFavorite: function (e) {
    const that = this
    const id = e.currentTarget.dataset.id
    toggleFavorite(this.data.type, id)
      .then(function (data) {
        if (data && data.favorited === false) {
          that.setData({
            list: that.data.list.filter(function (item) {
              return String(item.id) !== String(id)
            })
          })
          wx.showToast({ title: '已取消收藏', icon: 'none' })
        }
      })
      .catch(function () {})
  },

  goProduct: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  }
})
