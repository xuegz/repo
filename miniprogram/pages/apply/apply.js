const { request, HOST } = require('../../utils/request')

Page({
  data: {
    form: {
      name: '',
      contactName: '',
      phone: '',
      market: '',
      marketId: null,
      province: '',
      city: '',
      address: '',
      latitude: null,
      longitude: null,
      categoryId: null,
      intro: '',
      logo: ''
    },
    categories: [],
    markets: [],
    selectedCategory: '',
    selectedMarket: '',
    locationText: '',
    submitting: false
  },

  onLoad: function (options) {
    const that = this
    this.pendingMarketId = options && options.marketId ? Number(options.marketId) : 0
    request('/categories')
      .then(function (data) {
        that.setData({ categories: data || [] })
      })
      .catch(function () {})
    request('/markets')
      .then(function (data) {
        that.setData({ markets: data || [] })
        if (that.pendingMarketId) {
          that.applyMarket(that.pendingMarketId)
        }
      })
      .catch(function () {})
  },

  onMarketChange: function (e) {
    const market = this.data.markets[Number(e.detail.value)]
    if (market) {
      this.applyMarket(market.id)
    }
  },

  applyMarket: function (id) {
    const market = this.data.markets.find(function (item) {
      return Number(item.id) === Number(id)
    })
    if (!market) {
      return
    }
    const patch = {
      'form.marketId': market.id,
      'form.market': market.name,
      selectedMarket: market.name
    }
    if (!this.data.form.province && market.province) {
      patch['form.province'] = market.province
    }
    if (!this.data.form.city && market.city) {
      patch['form.city'] = market.city
    }
    this.setData(patch)
  },

  onInput: function (e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  onCategoryChange: function (e) {
    const index = Number(e.detail.value)
    const category = this.data.categories[index]
    this.setData({
      'form.categoryId': category.id,
      selectedCategory: category.name
    })
  },

  chooseLocation: function () {
    const that = this
    wx.chooseLocation({
      success: function (res) {
        const patch = {
          'form.latitude': res.latitude,
          'form.longitude': res.longitude,
          locationText: res.name || res.address || '已选择位置'
        }
        if (!that.data.form.address && res.address) {
          patch['form.address'] = res.address
        }
        that.setData(patch)
      }
    })
  },

  chooseLogo: function () {
    const that = this
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: function (res) {
        const temp = res.tempFiles[0].tempFilePath
        wx.showLoading({ title: '上传中' })
        wx.uploadFile({
          url: HOST + '/api/mp/upload',
          filePath: temp,
          name: 'file',
          success: function (up) {
            wx.hideLoading()
            try {
              const body = JSON.parse(up.data)
              if (body.code === 200) {
                that.setData({ 'form.logo': body.data.url })
                wx.showToast({ title: '上传成功', icon: 'success' })
              } else {
                wx.showToast({ title: body.msg || '上传失败', icon: 'none' })
              }
            } catch (err) {
              wx.showToast({ title: '上传失败', icon: 'none' })
            }
          },
          fail: function () {
            wx.hideLoading()
            wx.showToast({ title: '上传失败，请检查网络', icon: 'none' })
          }
        })
      }
    })
  },

  submit: function () {
    const f = this.data.form
    if (!f.name || !f.name.trim()) {
      wx.showToast({ title: '请填写商户名称', icon: 'none' })
      return
    }
    if (!f.contactName || !f.contactName.trim()) {
      wx.showToast({ title: '请填写联系人', icon: 'none' })
      return
    }
    if (!/^1\d{10}$/.test(f.phone || '')) {
      wx.showToast({ title: '请填写正确的手机号', icon: 'none' })
      return
    }

    const that = this
    this.setData({ submitting: true })
    request(HOST + '/api/auth/register', 'POST', this.data.form)
      .then(function () {
        that.setData({ submitting: false })
        wx.showModal({
          title: '提交成功',
          content: '资料已提交，平台将在 1 个工作日内完成审核。审核通过后，可用手机号登录商户端发布产品。',
          showCancel: false,
          success: function () {
            wx.switchTab({ url: '/pages/index/index' })
          }
        })
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  }
})
