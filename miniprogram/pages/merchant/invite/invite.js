const merchant = require('../../../utils/merchant')
const { formatDateTime } = require('../../../utils/format')

const STATUS_META = { 0: '待审核', 1: '已通过', 2: '已拒绝' }

Page({
  data: {
    loading: true,
    locked: false,
    code: '',
    inviteCount: 0,
    invited: [],
    shopName: ''
  },

  onShow: function () {
    this.loadInvite()
  },

  onPullDownRefresh: function () {
    this.loadInvite().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  loadInvite: function () {
    const that = this
    const shop = merchant.cachedShop() || {}
    this.setData({ loading: true, shopName: shop.shopName || '' })
    return merchant.merchantRequest('/invite')
      .then(function (data) {
        const rows = ((data && data.invited) || []).map(function (item) {
          item.statusText = STATUS_META[item.status] || '未知'
          item.timeText = formatDateTime(item.inviteTime || item.createTime)
          return item
        })
        that.setData({
          loading: false,
          locked: false,
          code: (data && data.code) || '',
          inviteCount: Number((data && data.inviteCount) || 0),
          invited: rows
        })
      })
      .catch(function (err) {
        that.setData({ loading: false, locked: (err && err.code) === 403 })
      })
  },

  copyCode: function () {
    if (!this.data.code) {
      return
    }
    wx.setClipboardData({ data: this.data.code })
  },

  goBack: function () {
    wx.navigateBack()
  },

  onShareAppMessage: function () {
    const code = this.data.code
    return {
      title: this.data.shopName
        ? '「' + this.data.shopName + '」邀请你免费入驻材路通'
        : '邀请你免费入驻材路通',
      path: '/pages/apply/apply' + (code ? '?invite=' + code : '')
    }
  }
})
