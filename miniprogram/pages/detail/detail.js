const {
  request,
  openidRequest,
  reportView,
  reportForward,
  reportCall,
  toggleFavorite,
  checkFavorite,
  getSubscribeConfig,
  requestSubscribe
} = require('../../utils/request')

function todayStr() {
  const d = new Date()
  const m = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return d.getFullYear() + '-' + m + '-' + day
}

Page({
  data: {
    product: {},
    compareList: [],
    compareLoaded: false,
    compareSummary: null,
    favorited: false,
    today: '',
    inquiryShow: false,
    inquirySubmitting: false,
    inquiryBodyHeight: 240,
    inquiryForm: { quantity: '', contact: '', remark: '', expectDate: '' }
  },

  noop: function () {},

  onLoad: function (options) {
    this.setData({ today: todayStr() })
    const that = this
    getSubscribeConfig().then(function (config) {
      that.quoteSubscribe = config && config.enabled ? config.templateId : ''
    })
    if (options && options.id) {
      const id = Number(options.id)
      this.loadDetail(id)
      this.loadCompare(id)
      // 浏览即上报，用于商户端"被浏览"统计
      reportView(1, id)
    }
  },

  onShow: function () {
    if (this.data.product.id) {
      this.loadFavorite()
    }
  },

  loadFavorite: function () {
    const that = this
    checkFavorite(1, this.data.product.id)
      .then(function (favorited) {
        that.setData({ favorited: !!favorited })
      })
      .catch(function () {})
  },

  toggleFavorite: function () {
    const that = this
    const product = this.data.product
    if (!product.id) {
      return
    }
    toggleFavorite(1, product.id)
      .then(function (data) {
        that.setData({ favorited: !!(data && data.favorited) })
        wx.showToast({ title: data && data.favorited ? '已收藏' : '已取消收藏', icon: 'none' })
      })
      .catch(function () {})
  },

  loadDetail: function (id) {
    const that = this
    request('/product?id=' + id)
      .then(function (data) {
        that.setData({ product: data }, function () {
          that.buildCompareSummary()
        })
        that.loadFavorite()
        wx.setNavigationBarTitle({ title: data.name || '产品详情' })
      })
      .catch(function () {})
  },

  /** 同款比价：其他商户对该产品的报价 */
  loadCompare: function (id) {
    const that = this
    request('/product/compare?id=' + id)
      .then(function (data) {
        that.setData({ compareList: data || [], compareLoaded: true }, function () {
          that.buildCompareSummary()
        })
      })
      .catch(function () {
        that.setData({ compareLoaded: true, compareSummary: null })
      })
  },

  /** 比价摘要：最低价 + 与当前价的差异（仅在单位一致时给百分比） */
  buildCompareSummary: function () {
    const list = this.data.compareList || []
    const product = this.data.product || {}
    if (!this.data.compareLoaded || !list.length) {
      this.setData({ compareSummary: null })
      return
    }
    const priced = list.filter(function (item) {
      return Number(item.price) > 0
    })
    if (!priced.length) {
      this.setData({ compareSummary: null })
      return
    }

    let lowest = priced[0]
    for (let i = 1; i < priced.length; i++) {
      if (Number(priced[i].price) < Number(lowest.price)) {
        lowest = priced[i]
      }
    }

    const current = Number(product.price)
    let diffText = '规格/单位不同，价格仅供参考'
    if (current > 0) {
      if (lowest.unit && product.unit && lowest.unit === product.unit) {
        const diff = current - Number(lowest.price)
        diffText = diff > 0
          ? '比当前低 ' + Math.round((diff / current) * 100) + '%'
          : '当前已是较低报价'
      }
    } else {
      diffText = '当前商品面议，可电话询价'
    }

    this.setData({
      compareSummary: {
        lowest: lowest.price,
        unit: lowest.unit || '',
        count: list.length,
        diffText: diffText
      }
    })
  },

  callPhone: function () {
    const product = this.data.product
    if (!product.merchantPhone) {
      return
    }
    // 上报电话联系记录（从产品页拨给所属商户）
    reportCall(1, product.id, product.merchantId)
    wx.makePhoneCall({
      phoneNumber: product.merchantPhone,
      fail: function () {}
    })
  },

  callComparePhone: function (e) {
    const phone = e.currentTarget.dataset.phone
    if (!phone) {
      wx.showToast({ title: '该商户未留电话', icon: 'none' })
      return
    }
    // 上报电话联系记录（比价列表询价）
    reportCall(1, e.currentTarget.dataset.id, e.currentTarget.dataset.merchantId)
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: function () {}
    })
  },

  goShop: function () {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + this.data.product.merchantId })
  },

  /** 打开与商户的会话，并自动带上当前商品卡片 */
  goChat: function () {
    const product = this.data.product || {}
    if (!product.merchantId) {
      wx.showToast({ title: '该商品暂未关联商户', icon: 'none' })
      return
    }
    let url = '/pages/chat/chat?merchantId=' + product.merchantId
      + '&merchantName=' + encodeURIComponent(product.merchantName || '商户')
    if (product.id) {
      url += '&productId=' + product.id
    }
    wx.navigateTo({ url: url })
  },

  /** 到店导航：有坐标打开地图，无坐标复制地址 */
  openNav: function () {
    const p = this.data.product
    if (p.merchantLatitude && p.merchantLongitude) {
      wx.openLocation({
        latitude: Number(p.merchantLatitude),
        longitude: Number(p.merchantLongitude),
        name: p.merchantName,
        address: p.merchantAddress || '',
        scale: 16,
        fail: function () {}
      })
      return
    }
    if (p.merchantAddress) {
      wx.setClipboardData({
        data: p.merchantAddress,
        success: function () {
          wx.showToast({ title: '地址已复制', icon: 'success' })
        }
      })
    } else {
      wx.showToast({ title: '该商户未填写地址', icon: 'none' })
    }
  },

  // ---------------- 询价留资 ----------------
  openInquiry: function () {
    const info = wx.getWindowInfo ? wx.getWindowInfo() : wx.getSystemInfoSync()
    const height = Math.max(150, Math.min(240, Math.round(info.windowHeight * 0.34)))
    this.setData({
      inquiryShow: true,
      inquiryBodyHeight: height,
      inquiryForm: { quantity: '', contact: '', remark: '', expectDate: '' }
    })
  },

  closeInquiry: function () {
    this.setData({ inquiryShow: false })
  },

  onInquiryInput: function (e) {
    this.setData({ ['inquiryForm.' + e.currentTarget.dataset.field]: e.detail.value })
  },

  onExpectDateChange: function (e) {
    this.setData({ 'inquiryForm.expectDate': e.detail.value })
  },

  submitInquiry: function () {
    const f = this.data.inquiryForm
    if (!f.contact || !f.contact.trim()) {
      wx.showToast({ title: '请填写联系方式', icon: 'none' })
      return
    }
    if (this.data.inquirySubmitting) {
      return
    }
    const that = this
    this.setData({ inquirySubmitting: true })
    // 一次授权对应一条报价通知；未配置模板时直接提交
    requestSubscribe(this.quoteSubscribe).then(function () {
      return openidRequest('/inquiry', 'POST', {
        productId: that.data.product.id,
        merchantId: that.data.product.merchantId,
        quantity: f.quantity,
        contact: f.contact,
        remark: f.remark,
        expectDate: f.expectDate
      })
    })
      .then(function () {
        that.setData({ inquiryShow: false, inquirySubmitting: false })
        wx.showModal({
          title: '提交成功',
          content: '商户收到后会尽快报价，可在「我的-我的询价」查看报价；着急可直接电话联系档口。',
          showCancel: false
        })
      })
      .catch(function () {
        that.setData({ inquirySubmitting: false })
      })
  },

  goCompareShop: function (e) {
    wx.navigateTo({ url: '/pages/shop/shop?id=' + e.currentTarget.dataset.id })
  },

  /** 转发时上报，用于商户端"被转发"统计 */
  onShareAppMessage: function () {
    const product = this.data.product
    if (product.id) {
      reportForward(1, product.id)
    }
    return {
      title: product.name || '建材产品',
      path: '/pages/detail/detail?id=' + product.id
    }
  }
})
