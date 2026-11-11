const { request, openidRequest, getMemberAccess, guideMembership } = require('../../utils/request')

/** 两点球面距离（公里），与后端 market 距离口径一致 */
function haversineKm(lat1, lng1, lat2, lng2) {
  const rad = Math.PI / 180
  const radLat1 = lat1 * rad
  const radLat2 = lat2 * rad
  const dLat = radLat2 - radLat1
  const dLng = (lng2 - lng1) * rad
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
  return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

Page({
  data: {
    keyword: '',
    markets: [],
    cities: [],
    // 首项为「全部城市」，与 cityIndex 下标对应
    cityOptions: ['全部城市'],
    cityIndex: 0,
    list: [],
    mapCount: 0,
    loaded: false,
    sortMode: 'hot',
    userLat: null,
    userLng: null,
    locating: false,
    memberActive: false,
    memberBound: false
  },

  onLoad: function () {
    this.loadMarkets()
    this.loadCities()
    const that = this
    getMemberAccess().then(function (access) {
      that.accessLoaded = true
      that.setData({
        memberActive: !!(access && access.memberActive),
        memberBound: !!(access && access.bound)
      })
      that.loadList()
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
        that.loadList()
      }
    })
  },

  goMember: function () {
    const that = this
    getMemberAccess(true).then(function (access) {
      if (access && access.memberActive) {
        that.setData({ memberActive: true, memberBound: !!(access && access.bound) })
        that.loadList()
        return
      }
      guideMembership(access)
    })
  },

  onPullDownRefresh: function () {
    this.loadList(function () {
      wx.stopPullDownRefresh()
    })
  },

  loadMarkets: function () {
    const that = this
    request('/markets')
      .then(function (data) {
        that.setData({ markets: data || [] })
        that.marketsReady = true
        that.maybeLocateCity()
      })
      .catch(function () {
        that.marketsReady = true
        that.maybeLocateCity()
      })
  },

  loadCities: function () {
    const that = this
    request('/cities')
      .then(function (data) {
        const cities = data || []
        that.setData({
          cities: cities,
          cityOptions: ['全部城市'].concat(cities)
        })
        that.citiesReady = true
        that.maybeLocateCity()
      })
      .catch(function () {
        that.citiesReady = true
        that.maybeLocateCity()
      })
  },

  /**
   * 已授权定位时静默取当前位置，自动选中最近市场所在城市
   * 不主动弹授权框，也不覆盖用户手动选择
   */
  maybeLocateCity: function () {
    if (this.cityAutoTried || !this.marketsReady || !this.citiesReady) {
      return
    }
    this.cityAutoTried = true
    if (this.data.cityIndex > 0) {
      return
    }
    const that = this
    wx.getSetting({
      success: function (res) {
        if (!res.authSetting || !res.authSetting['scope.userLocation']) {
          return
        }
        wx.getLocation({
          type: 'gcj02',
          success: function (loc) {
            that.tryAutoCity(loc.latitude, loc.longitude)
          },
          fail: function () {}
        })
      }
    })
  },

  /** 按坐标匹配最近市场所在城市并切换筛选（100 公里内才生效），返回是否切换 */
  tryAutoCity: function (lat, lng) {
    if (this.data.cityIndex > 0 || !lat || !lng) {
      return false
    }
    let best = null
    const markets = this.data.markets || []
    for (let i = 0; i < markets.length; i++) {
      const m = markets[i]
      if (!m.city || m.latitude == null || m.longitude == null) {
        continue
      }
      const km = haversineKm(lat, lng, Number(m.latitude), Number(m.longitude))
      if (!best || km < best.km) {
        best = { km: km, city: m.city }
      }
    }
    if (!best || best.km > 100) {
      return false
    }
    const index = this.data.cities.indexOf(best.city)
    if (index < 0) {
      return false
    }
    const that = this
    this.setData({ cityIndex: index + 1 }, function () {
      that.loadList()
    })
    return true
  },

  onCityChange: function (e) {
    const index = Number(e.detail.value) || 0
    const that = this
    this.setData({ cityIndex: index }, function () {
      that.loadList()
    })
  },

  loadList: function (cb) {
    const that = this
    const params = {}
    if (this.data.keyword) {
      params.keyword = this.data.keyword
    }
    const city = this.data.cityIndex > 0 ? this.data.cities[this.data.cityIndex - 1] : ''
    if (city) {
      params.city = city
    }
    if (this.data.sortMode === 'distance' && this.data.userLat != null) {
      params.lat = this.data.userLat
      params.lng = this.data.userLng
      params.sort = 'distance'
    }
    openidRequest('/merchants', 'GET', params)
      .then(function (data) {
        const list = data || []
        that.setData({
          list: list,
          mapCount: list.filter(function (item) {
            return item.latitude && item.longitude
          }).length,
          loaded: true
        })
      })
      .catch(function () {
        that.setData({ loaded: true })
      })
      .then(function () {
        if (cb) cb()
      })
  },

  selectSort: function (e) {
    const mode = e.currentTarget.dataset.mode
    const that = this
    if (mode === 'hot') {
      this.setData({ sortMode: 'hot' }, function () {
        that.loadList()
      })
      return
    }
    if (!this.data.memberActive) {
      this.goMember()
      return
    }
    if (this.data.userLat != null) {
      this.setData({ sortMode: 'distance' }, function () {
        that.loadList()
      })
      return
    }
    this.locateAndLoad()
  },

  locateAndLoad: function () {
    const that = this
    if (this.data.locating) {
      return
    }
    this.setData({ locating: true })
    wx.getLocation({
      type: 'gcj02',
      success: function (res) {
        that.setData({
          userLat: res.latitude,
          userLng: res.longitude,
          sortMode: 'distance',
          locating: false
        }, function () {
          // 自动切城会自行刷新列表，避免重复请求
          if (!that.tryAutoCity(res.latitude, res.longitude)) {
            that.loadList()
          }
        })
      },
      fail: function (err) {
        that.setData({ locating: false })
        const msg = (err && err.errMsg) || ''
        if (msg.indexOf('auth') > -1) {
          wx.showModal({
            title: '需要位置权限',
            content: '开启定位后才能按距离查找附近商户',
            confirmText: '去设置',
            success: function (res) {
              if (!res.confirm) {
                return
              }
              wx.openSetting({
                success: function (setting) {
                  if (setting.authSetting['scope.userLocation']) {
                    that.locateAndLoad()
                  }
                }
              })
            }
          })
          return
        }
        wx.showToast({ title: '定位失败，请稍后重试', icon: 'none' })
      }
    })
  },

  onKeywordInput: function (e) {
    this.setData({ keyword: e.detail.value })
  },

  doSearch: function () {
    this.loadList()
  },

  goMarket: function (e) {
    wx.navigateTo({ url: '/pages/market/market?id=' + e.currentTarget.dataset.id })
  },

  goShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  },

  goMap: function () {
    wx.navigateTo({ url: '/pages/map/map' })
  },

  goApply: function () {
    wx.navigateTo({ url: '/pages/apply/apply' })
  }
})
