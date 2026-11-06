const merchant = require('../../../utils/merchant')
const { uploadImage } = require('../../../utils/request')

const MAX_IMAGES = 9

Page({
  data: {
    id: 0,
    isEdit: false,
    loading: true,
    submitting: false,
    status: 0,
    form: {
      name: '',
      categoryId: null,
      brand: '',
      typeName: '',
      spec: '',
      price: '',
      unit: '件',
      images: [],
      description: ''
    },
    categoryLabels: [],
    categoryIndex: -1,
    selectedCategory: ''
  },

  onLoad: function (options) {
    const id = options && options.id ? Number(options.id) : 0
    this.setData({ id: id, isEdit: !!id })
    wx.setNavigationBarTitle({ title: id ? '编辑产品' : '发布产品' })
    const that = this
    this.loadCategories().then(function () {
      if (id) {
        that.loadDetail(id)
      } else {
        that.setData({ loading: false })
      }
    })
  },

  loadCategories: function () {
    const that = this
    return merchant.merchantRequest('/categories')
      .then(function (data) {
        const cats = data || []
        const nameById = {}
        cats.forEach(function (cat) {
          nameById[cat.id] = cat.name
        })
        const labels = cats.map(function (cat) {
          const parent = cat.parentId ? nameById[cat.parentId] : ''
          return (parent ? parent + ' / ' : '') + cat.name
        })
        that.setData({ categories: cats, categoryLabels: labels })
        return cats
      })
      .catch(function () {
        return []
      })
  },

  loadDetail: function (id) {
    const that = this
    merchant.merchantRequest('/product/detail/' + id)
      .then(function (product) {
        const p = product || {}
        let images = p.imageList || []
        if (!images.length && p.images) {
          images = String(p.images).split(',').filter(function (path) {
            return !!path
          })
        }
        const cats = that.data.categories || []
        let categoryIndex = -1
        for (let i = 0; i < cats.length; i += 1) {
          if (Number(cats[i].id) === Number(p.categoryId)) {
            categoryIndex = i
            break
          }
        }
        that.setData({
          form: {
            name: p.name || '',
            categoryId: p.categoryId || null,
            brand: p.brand || '',
            typeName: p.typeName || '',
            spec: p.spec || '',
            price: p.price === null || p.price === undefined ? '' : String(p.price),
            unit: p.unit || '',
            images: images,
            description: p.description || ''
          },
          status: p.status || 0,
          categoryIndex: categoryIndex,
          selectedCategory: categoryIndex >= 0 ? that.data.categoryLabels[categoryIndex] : '',
          loading: false
        })
      })
      .catch(function () {
        wx.showToast({ title: '产品加载失败', icon: 'none' })
        setTimeout(function () {
          wx.navigateBack()
        }, 800)
      })
  },

  onInput: function (e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  onCategoryChange: function (e) {
    const index = Number(e.detail.value)
    this.setData({
      categoryIndex: index,
      'form.categoryId': this.data.categories[index] ? this.data.categories[index].id : null,
      selectedCategory: this.data.categoryLabels[index]
    })
  },

  chooseImages: function () {
    const that = this
    const remain = MAX_IMAGES - this.data.form.images.length
    if (remain <= 0) {
      wx.showToast({ title: '最多上传 ' + MAX_IMAGES + ' 张图片', icon: 'none' })
      return
    }
    wx.chooseMedia({
      count: remain,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: function (res) {
        const files = (res.tempFiles || []).map(function (item) {
          return item.tempFilePath
        })
        if (files.length) {
          that.uploadFiles(files)
        }
      }
    })
  },

  uploadFiles: function (files) {
    const that = this
    const uploaded = []
    let index = 0

    function finish() {
      wx.hideLoading()
      if (uploaded.length) {
        that.setData({ 'form.images': that.data.form.images.concat(uploaded) })
      }
    }

    function next() {
      if (index >= files.length) {
        finish()
        return
      }
      const file = files[index]
      index += 1
      wx.showLoading({ title: '上传 ' + index + '/' + files.length, mask: true })
      uploadImage(file)
        .then(function (url) {
          uploaded.push(url)
          next()
        })
        .catch(function (err) {
          finish()
          wx.showToast({ title: (err && err.message) || '图片上传失败', icon: 'none' })
        })
    }

    next()
  },

  removeImage: function (e) {
    const index = Number(e.currentTarget.dataset.index)
    const images = this.data.form.images.slice()
    images.splice(index, 1)
    this.setData({ 'form.images': images })
  },

  submit: function () {
    if (this.data.submitting) return
    const form = this.data.form
    const name = (form.name || '').trim()
    if (!name) {
      wx.showToast({ title: '请填写产品名称', icon: 'none' })
      return
    }
    if (!form.categoryId) {
      wx.showToast({ title: '请选择所属品类', icon: 'none' })
      return
    }
    let price = 0
    if (form.price !== '' && form.price !== null && form.price !== undefined) {
      price = Number(form.price)
      if (isNaN(price) || price < 0) {
        wx.showToast({ title: '价格格式不正确', icon: 'none' })
        return
      }
    }
    const payload = {
      name: name,
      categoryId: form.categoryId,
      brand: (form.brand || '').trim(),
      typeName: (form.typeName || '').trim(),
      spec: (form.spec || '').trim(),
      price: price,
      unit: (form.unit || '').trim(),
      images: form.images.join(','),
      description: (form.description || '').trim()
    }
    if (this.data.id) {
      payload.id = this.data.id
    }
    const that = this
    this.setData({ submitting: true })
    merchant.merchantRequest('/product/save', 'POST', payload)
      .then(function () {
        that.setData({ submitting: false })
        wx.showToast({ title: that.data.id ? '已保存' : '已提交审核', icon: 'success' })
        setTimeout(function () {
          wx.navigateBack()
        }, 700)
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  }
})
