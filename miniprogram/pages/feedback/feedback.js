const { openidRequest } = require('../../utils/request')

Page({
  data: {
    categories: ['功能建议', '使用问题', '信息有误', '其他'],
    category: '功能建议',
    content: '',
    contact: '',
    submitting: false,
    records: []
  },

  onShow: function () {
    this.loadRecords()
  },

  onCategory: function (e) {
    this.setData({ category: e.currentTarget.dataset.name })
  },

  onContent: function (e) {
    this.setData({ content: e.detail.value || '' })
  },

  onContact: function (e) {
    this.setData({ contact: e.detail.value || '' })
  },

  submit: function () {
    const content = (this.data.content || '').trim()
    if (content.length < 5) {
      wx.showToast({ title: '请至少填写 5 个字的反馈内容', icon: 'none' })
      return
    }
    const that = this
    this.setData({ submitting: true })
    openidRequest('/feedback', 'POST', {
      category: this.data.category,
      content: content,
      contact: (this.data.contact || '').trim()
    })
      .then(function () {
        that.setData({ submitting: false, content: '', contact: '' })
        wx.showToast({ title: '已提交，感谢反馈', icon: 'success' })
        that.loadRecords()
      })
      .catch(function () {
        that.setData({ submitting: false })
      })
  },

  loadRecords: function () {
    const that = this
    openidRequest('/feedback/list', 'GET')
      .then(function (data) {
        that.setData({ records: data || [] })
      })
      .catch(function () {})
  }
})
