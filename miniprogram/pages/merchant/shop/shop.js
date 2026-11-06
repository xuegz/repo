const merchant = require('../../../utils/merchant')
const { uploadImage } = require('../../../utils/request')

Page({
  data: {
    loading: true,
    submitting: false,
    merchantId: 0,
    raw: {},
    form: {
      name: '',
      contactName: '',
      phone: '',
      market: '',
      categoryId: null,
      address: '',
      latitude: '',
      longitude: '',
      logo: '',
      intro: ''
    },
    categories: [],
    categoryLabels: [],
    categoryIndex: -1,
    selectedCategory: ''
  },

  onLoad: function () {
    const that = this
    merchant.refreshSession()
      .then(function (data) {
        if (!data || !data.bound) {
          wx.showToast({ title: (data && data.reason) || '请先绑定商户账号', icon: 'none' })
          setTimeout(function () {
            merchant.toLogin()
          }, 800)
          return
        }
        if (data.mustChangePassword) {
          wx.redirectTo({ url: merchant.LOGIN_PAGE + '?force=1' })
          return
        }
        that.setData({ merchantId: data.merchantId })
        return that.loadShop()
      })
      .catch(function () {
        that.setData({ loading: false })
        wx.showToast({ title: '店铺信息加载失败，请稍后重试', icon: 'none' })
      })
  },

  loadShop: function () {
    const that = this
    return Promise.all([
      merchant.merchantRequest('/shop/info'),
      merchant.merchantRequest('/categories').catch(function () {
        return []
      })
    ]).then(function (res) {
      const shop = res[0] || {}
      const cats = res[1] || []
      const nameById = {}
      cats.forEach(function (cat) {
        nameById[cat.id] = cat.name
      })
      const labels = cats.map(function (cat) {
        const parent = cat.parentId ? nameById[cat.parentId] : ''
        return (parent ? parent + ' / ' : '') + cat.name
      })
      let categoryIndex = -1
      for (let i = 0; i < cats.length; i += 1) {
        if (Number(cats[i].id) === Number(shop.categoryId)) {
          categoryIndex = i
          break
        }
      }
      that.setData({
        raw: shop,
        categories: cats,
        categoryLabels: labels,
        categoryIndex: categoryIndex,
        selectedCategory: categoryIndex >= 0 ? labels[categoryIndex] : '',
        form: {
          name: shop.name || '',
          contactName: shop.contactName || '',
          phone: shop.phone || '',
          market: shop.market || '',
          categoryId: shop.categoryId || null,
          address: shop.address || '',
          latitude: shop.latitude === null || shop.latitude === undefined ? '' : String(shop.latitude),
          longitude: shop.longitude === null || shop.longitude === undefined ? '' : String(shop.longitude),
          logo: shop.logo || '',
          intro: shop.intro || ''
        },
        loading: false
      })
    }).catch(function () {
      that.setData({ loading: false })
      wx.showToast({ title: '店铺信息加载失败', icon: 'none' })
    })
  },

  onInput: function (e) {
    this.setData({ ['form.' + e.currentTarget.dataset.field]: e.detail.value })
  },

  onCategoryChange: function (e) {
    const index = Number(e.detail.value)
    const category = this.data.categories[index]
    this.setData({
      categoryIndex: index,
      'form.categoryId': category ? category.id : null,
      selectedCategory: this.data.categoryLabels[index] || ''
    })
  },

  chooseLogo: function () {
    const that = this
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: function (res) {
        const file = res.tempFiles && res.tempFiles[0] && res.tempFiles[0].tempFilePath
        if (!file) return
        wx.showLoading({ title: '上传中', mask: true })
        uploadImage(file)
          .then(function (url) {
            wx.hideLoading()
            that.setData({ 'form.logo': url })
          })
          .catch(function (err) {
            wx.hideLoading()
            wx.showToast({ title: (err && err.message) || '图片上传失败', icon: 'none' })
          })
      }
    })
  },

  removeLogo: function () {
    this.setData({ 'form.logo': '' })
  },

  chooseLocation: function () {
    const that = this
    const options = {
      success: function (res) {
        const patch = {
          'form.latitude': Number(res.latitude).toFixed(6),
          'form.longitude': Number(res.longitude).toFixed(6)
        }
        if (!that.data.form.address) {
          patch['form.address'] = res.address || res.name || ''
        }
        that.setData(patch)
      },
      fail: function (err) {
        if (err && err.errMsg && err.errMsg.indexOf('cancel') >= 0) return
        wx.showToast({ title: '无法选点，请检查定位权限', icon: 'none' })
      }
    }
    const form = this.data.form
    if (form.latitude && form.longitude) {
      options.latitude = Number(form.latitude)
      options.longitude = Number(form.longitude)
    }
    wx.chooseLocation(options)
  },

  clearLocation: function () {
    this.setData({ 'form.latitude': '', 'form.longitude': '' })
  },

  preview: function () {
    if (this.data.merchantId) {
      wx.navigateTo({ url: '/pages/shop/shop?id=' + this.data.merchantId })
    }
  },

  submit: function () {
    if (this.data.submitting) return
    const form = this.data.form
    const name = (form.name || '').trim()
    if (!name) {
      wx.showToast({ title: '请填写门店名称', icon: 'none' })
      return
    }
    const phone = (form.phone || '').trim()
    if (!phone) {
      wx.showToast({ title: '请填写联系电话', icon: 'none' })
      return
    }
    const latText = String(form.latitude || '').trim()
    const lngText = String(form.longitude || '').trim()
    if ((latText && !lngText) || (!latText && lngText)) {
      wx.showToast({ title: '请同时填写经纬度或清空坐标', icon: 'none' })
      return
    }
    let latitude = null
    let longitude = null
    if (latText && lngText) {
      latitude = Number(latText)
      longitude = Number(lngText)
      if (isNaN(latitude) || latitude < -90 || latitude > 90) {
        wx.showToast({ title: '纬度格式不正确', icon: 'none' })
        return
      }
      if (isNaN(longitude) || longitude < -180 || longitude > 180) {
        wx.showToast({ title: '经度格式不正确', icon: 'none' })
        return
      }
    }
    const raw = this.data.raw || {}
    const payload = {
      name: name,
      contactName: (form.contactName || '').trim(),
      phone: phone,
      address: (form.address || '').trim(),
      intro: (form.intro || '').trim(),
      logo: form.logo || '',
      categoryId: form.categoryId || null,
      latitude: latitude,
      longitude: longitude,
      clearLocation: latitude === null && longitude === null,
      marketId: raw.marketId || null,
      market: raw.market || '',
      province: raw.province || '',
      city: raw.city || ''
    }
    const that = this
    this.setData({ submitting: true })
    merchant.merchantRequest('/shop/save', 'POST', payload)
      .then(function () {
        return merchant.refreshSession().catch(function () {})
      })
      .then(function () {
        that.setData({ submitting: false })
        wx.showToast({ title: '已保存', icon: 'success' })
        setTimeout(function () {
          wx.navigateBack()
        }, 700)
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  }
})
