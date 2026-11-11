const { openidRequest, getMemberAccess, guideMembership, reportCall } = require('../../utils/request')

Page({
  data: {
    market: {},
    merchants: [],
    productCount: 0,
    addressText: '',
    loaded: false,
    memberActive: false,
    memberBound: false
  },

  onLoad: function (options) {
    if (!options || !options.id) {
      wx.navigateBack()
      return
    }
    this.marketId = Number(options.id)
    const that = this
    getMemberAccess().then(function (access) {
      that.accessLoaded = true
      that.setData({
        memberActive: !!(access && access.memberActive),
        memberBound: !!(access && access.bound)
      })
      that.load()
    })
  },

  onShow: function () {
    if (!this.accessLoaded) {
      return
    }
    const that = this
    getMemberAccess(true).then(function (access) {
      const active = !!(access && access.memberActive)
      const changed = active !== that.data.memberActive
      that.setData({
        memberActive: active,
        memberBound: !!(access && access.bound)
      })
      if (changed) {
        that.load()
      }
    })
  },

  goMember: function () {
    const that = this
    getMemberAccess(true).then(function (access) {
      if (access && access.memberActive) {
        that.setData({ memberActive: true, memberBound: !!(access && access.bound) })
        that.load()
        return
      }
      guideMembership(access)
    })
  },

  onPullDownRefresh: function () {
    const that = this
    this.load(function () {
      wx.stopPullDownRefresh()
    })
  },

  load: function (cb) {
    const that = this
    openidRequest('/market?id=' + this.marketId, 'GET')
      .then(function (data) {
        const market = data.market || {}
        const merchants = data.merchants || []
        const addressParts = [market.province, market.city, market.address].filter(function (s) {
          return !!s
        })
        that.setData({
          market: market,
          merchants: merchants,
          productCount: data.productCount || 0,
          addressText: addressParts.length ? addressParts.join(' ') : '地址待完善',
          loaded: true
        })
        wx.setNavigationBarTitle({ title: market.name || '建材市场' })
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  },

  callShop: function (e) {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const index = e.currentTarget.dataset.index
    const merchant = this.data.merchants[index]
    if (!merchant || !merchant.phone) {
      wx.showToast({ title: '该商户暂未留电话', icon: 'none' })
      return
    }
    reportCall(2, merchant.id)
    wx.makePhoneCall({
      phoneNumber: merchant.phone,
      fail: function () {}
    })
  },

  openNav: function () {
    const market = this.data.market
    if (market.latitude && market.longitude) {
      wx.openLocation({
        latitude: Number(market.latitude),
        longitude: Number(market.longitude),
        name: market.name,
        address: this.data.addressText,
        scale: 16,
        fail: function () {}
      })
      return
    }
    if (this.data.addressText && this.data.addressText !== '地址待完善') {
      wx.setClipboardData({
        data: this.data.addressText,
        success: function () {
          wx.showToast({ title: '地址已复制', icon: 'success' })
        }
      })
    } else {
      wx.showToast({ title: '该市场暂未标注位置', icon: 'none' })
    }
  },

  goMap: function () {
    const name = this.data.market.name
    const url = name ? '/pages/map/map?market=' + encodeURIComponent(name) : '/pages/map/map'
    wx.navigateTo({ url: url })
  },

  goApply: function () {
    const id = this.data.market.id
    wx.navigateTo({ url: '/pages/apply/apply' + (id ? '?marketId=' + id : '') })
  },

  onShareAppMessage: function () {
    const market = this.data.market
    return {
      title: (market.name || '建材市场') + ' · 入驻商户',
      path: '/pages/market/market?id=' + market.id
    }
  }
})
