const {
  openidRequest,
  getMemberAccess,
  guideMembership,
  reportView,
  reportCall,
  toggleFavorite,
  checkFavorite
} = require('../../utils/request')

Page({
  data: {
    merchant: {},
    products: [],
    addressText: '-',
    favorited: false,
    memberActive: false,
    memberBound: false
  },

  onLoad: function (options) {
    if (options && options.id) {
      const id = Number(options.id)
      const that = this
      this.loadAccess().then(function () {
        that.loadShop(id)
      })
      this.loadFavorite(id)
      // 商户被浏览统计
      reportView(2, id)
    }
  },

  onShow: function () {
    if (this.data.merchant.id) {
      this.loadFavorite(this.data.merchant.id)
      this.refreshAccess(this.data.merchant.id)
    }
  },

  loadAccess: function () {
    const that = this
    return getMemberAccess().then(function (access) {
      that.setData({
        memberActive: !!(access && access.memberActive),
        memberBound: !!(access && access.bound)
      })
    })
  },

  refreshAccess: function (id) {
    const that = this
    return getMemberAccess().then(function (access) {
      const active = !!(access && access.memberActive)
      const changed = active !== that.data.memberActive
      that.setData({
        memberActive: active,
        memberBound: !!(access && access.bound)
      })
      if (changed) {
        that.loadShop(id)
      }
    })
  },

  goMember: function () {
    const that = this
    getMemberAccess(true).then(function (access) {
      that.setData({
        memberActive: !!(access && access.memberActive),
        memberBound: !!(access && access.bound)
      })
      if (access && access.memberActive) {
        if (that.data.merchant.id) {
          that.loadShop(that.data.merchant.id)
        }
        return
      }
      guideMembership(access)
    })
  },

  loadFavorite: function (id) {
    const that = this
    checkFavorite(2, id)
      .then(function (favorited) {
        that.setData({ favorited: !!favorited })
      })
      .catch(function () {})
  },

  toggleFavorite: function () {
    const that = this
    const merchant = this.data.merchant
    if (!merchant.id) {
      return
    }
    toggleFavorite(2, merchant.id)
      .then(function (data) {
        that.setData({ favorited: !!(data && data.favorited) })
        wx.showToast({ title: data && data.favorited ? '已收藏' : '已取消收藏', icon: 'none' })
      })
      .catch(function () {})
  },

  loadShop: function (id) {
    const that = this
    openidRequest('/merchant?id=' + id, 'GET')
      .then(function (data) {
        const m = data.merchant || {}
        const addressParts = [m.province, m.city, m.address].filter(function (s) {
          return !!s
        })
        that.setData({
          merchant: m,
          products: data.products || [],
          addressText: addressParts.length ? addressParts.join(' ') : '-'
        })
        wx.setNavigationBarTitle({ title: m.name || '商户主页' })
      })
      .catch(function () {})
  },

  callPhone: function () {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const merchant = this.data.merchant
    if (!merchant.phone) {
      return
    }
    // 上报电话联系记录（商户主页拨号）
    reportCall(2, merchant.id)
    wx.makePhoneCall({
      phoneNumber: merchant.phone,
      fail: function () {}
    })
  },

  goChat: function () {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const m = this.data.merchant
    if (!m || !m.id) {
      wx.showToast({ title: '商户信息加载中，请稍后', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: '/pages/chat/chat?merchantId=' + m.id
        + '&merchantName=' + encodeURIComponent(m.name || '商户')
    })
  },

  openNav: function () {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const m = this.data.merchant
    const addressText = this.data.addressText
    if (m.latitude && m.longitude) {
      wx.openLocation({
        latitude: Number(m.latitude),
        longitude: Number(m.longitude),
        name: m.name,
        address: addressText,
        scale: 16,
        fail: function () {}
      })
      return
    }
    // 未填坐标时复制地址，方便去地图 App 搜索
    if (addressText && addressText !== '-') {
      wx.setClipboardData({
        data: addressText,
        success: function () {
          wx.showToast({ title: '地址已复制', icon: 'success' })
        }
      })
    } else {
      wx.showToast({ title: '该商户未填写地址', icon: 'none' })
    }
  },

  goDetail: function (e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  },

  goMap: function () {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const merchant = this.data.merchant
    if (!merchant.id) {
      return
    }
    wx.navigateTo({ url: '/pages/map/map?merchantId=' + merchant.id })
  },

  goMarket: function () {
    const merchant = this.data.merchant
    if (!merchant.marketId) {
      return
    }
    wx.navigateTo({ url: '/pages/market/market?id=' + merchant.marketId })
  },

  onShareAppMessage: function () {
    const m = this.data.merchant
    return {
      title: m.name || '建材商户',
      path: '/pages/shop/shop?id=' + m.id
    }
  }
})
