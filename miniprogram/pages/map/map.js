const { openidRequest, getMemberAccess, guideMembership, reportCall } = require('../../utils/request')

const DEFAULT_CENTER = { latitude: 34.3086, longitude: 108.9402 }

function addressOf(merchant) {
  return [merchant.province, merchant.city, merchant.address].filter(Boolean).join(' ') || '地址待完善'
}

Page({
  data: {
    markers: [],
    includePoints: [],
    center: DEFAULT_CENTER,
    scale: 12,
    markets: [],
    marketOptions: ['全部市场'],
    market: '',
    merchantCount: 0,
    selected: {},
    loaded: false,
    memberActive: false,
    memberBound: false
  },

  onLoad: function (options) {
    this.entryMerchantId = options && options.merchantId ? Number(options.merchantId) : 0
    this.initialMarket = options && options.market ? decodeURIComponent(options.market) : ''
    const that = this
    getMemberAccess().then(function (access) {
      that.accessLoaded = true
      that.setData({
        memberActive: !!(access && access.memberActive),
        memberBound: !!(access && access.bound)
      })
      if (access && access.memberActive) {
        that.loadMerchants()
      } else {
        that.setData({ loaded: true })
      }
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
      if (changed && active) {
        that.loadMerchants()
      }
    })
  },

  goMember: function () {
    const that = this
    getMemberAccess(true).then(function (access) {
      if (access && access.memberActive) {
        that.setData({ memberActive: true, memberBound: !!(access && access.bound) })
        that.loadMerchants()
        return
      }
      guideMembership(access)
    })
  },

  loadMerchants: function () {
    const that = this
    openidRequest('/merchants', 'GET')
      .then(function (data) {
        const list = (data || []).map(function (merchant) {
          return Object.assign({}, merchant, { addressText: addressOf(merchant) })
        })
        that.allList = list

        const markets = []
        list.forEach(function (merchant) {
          if (merchant.market && markets.indexOf(merchant.market) === -1) {
            markets.push(merchant.market)
          }
        })
        const market = that.initialMarket && markets.indexOf(that.initialMarket) >= 0 ? that.initialMarket : ''
        that.setData({
          markets: markets,
          marketOptions: ['全部市场'].concat(markets),
          market: market,
          loaded: true
        })
        that.applyFilter()
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
  },

  /** 按市场筛选有坐标的商户，生成地图标记 */
  applyFilter: function () {
    const that = this
    const list = (this.allList || []).filter(function (merchant) {
      if (!merchant.latitude || !merchant.longitude) {
        return false
      }
      return !that.data.market || merchant.market === that.data.market
    })

    const markers = list.map(function (merchant) {
      return {
        id: Number(merchant.id),
        latitude: Number(merchant.latitude),
        longitude: Number(merchant.longitude),
        iconPath: '/assets/map/marker.png',
        width: 32,
        height: 40,
        callout: {
          content: merchant.name || '建材商户',
          color: '#1f2937',
          fontSize: 12,
          borderRadius: 8,
          bgColor: '#ffffff',
          padding: 8,
          display: 'BYCLICK'
        }
      }
    })
    const points = markers.map(function (marker) {
      return { latitude: marker.latitude, longitude: marker.longitude }
    })

    const patch = {
      markers: markers,
      includePoints: points,
      scale: 12,
      merchantCount: markers.length,
      selected: {}
    }
    if (points.length) {
      patch.center = points[0]
    }
    this.setData(patch, function () {
      if (that.entryMerchantId) {
        that.focusMerchant(that.entryMerchantId)
        that.entryMerchantId = 0
      }
    })
  },

  /** 选中并聚焦指定商户 */
  focusMerchant: function (id) {
    const merchant = (this.allList || []).find(function (item) {
      return Number(item.id) === Number(id)
    })
    if (!merchant || !merchant.latitude || !merchant.longitude) {
      return
    }
    this.setData({
      selected: merchant,
      center: { latitude: Number(merchant.latitude), longitude: Number(merchant.longitude) },
      scale: 17,
      includePoints: []
    })
  },

  onMarkerTap: function (e) {
    this.focusMerchant(e.detail.markerId)
  },

  onMarketTap: function () {
    const that = this
    wx.showActionSheet({
      itemList: this.data.marketOptions,
      success: function (res) {
        const market = res.tapIndex === 0 ? '' : that.data.marketOptions[res.tapIndex]
        that.setData({ market: market })
        that.applyFilter()
      }
    })
  },

  closeCard: function () {
    this.setData({ selected: {} })
  },

  callShop: function () {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const merchant = this.data.selected
    if (!merchant || !merchant.phone) {
      return
    }
    reportCall(2, merchant.id)
    wx.makePhoneCall({
      phoneNumber: merchant.phone,
      fail: function () {}
    })
  },

  openNav: function () {
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    const merchant = this.data.selected
    if (!merchant || !merchant.latitude || !merchant.longitude) {
      wx.showToast({ title: '该商户暂未标注位置', icon: 'none' })
      return
    }
    wx.openLocation({
      latitude: Number(merchant.latitude),
      longitude: Number(merchant.longitude),
      name: merchant.name,
      address: merchant.addressText,
      scale: 18,
      fail: function () {}
    })
  },

  goShop: function () {
    const merchant = this.data.selected
    if (!merchant || !merchant.id) {
      return
    }
    wx.navigateTo({ url: '/pages/shop/shop?id=' + merchant.id })
  },

  goShops: function () {
    wx.redirectTo({ url: '/pages/shops/shops' })
  }
})
