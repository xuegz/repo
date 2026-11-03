/**
 * 小程序接口请求封装
 * 当前指向 ECS 生产环境（仅体验版/开发版可用，需勾选"不校验合法域名"）
 * 本地调试改回 http://localhost:8080；域名备案完成后改为 https 域名
 */
const HOST = 'http://47.111.99.105'
const BASE_URL = HOST + '/api/mp'

function request(url, method = 'GET', data = {}) {
  // url 以 http 开头时视为完整地址，便于调用非 /api/mp 前缀的接口
  const target = url.indexOf('http') === 0 ? url : BASE_URL + url
  return new Promise((resolve, reject) => {
    wx.request({
      url: target,
      method: method,
      data: data,
      header: { 'content-type': 'application/json' },
      success: function (res) {
        const body = res.data || {}
        if (body.code === 200) {
          resolve(body.data)
        } else {
          wx.showToast({ title: body.msg || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail: function (err) {
        wx.showToast({ title: '网络异常，请检查后端服务', icon: 'none' })
        reject(err)
      }
    })
  })
}

/** 上报浏览 */
function reportView(targetType, targetId) {
  const openid = getOpenid()
  return request('/view', 'POST', { targetType, targetId, openid }).catch(() => {})
}

/** 上报转发 */
function reportForward(targetType, targetId) {
  const openid = getOpenid()
  return request('/forward', 'POST', { targetType, targetId, openid }).catch(() => {})
}

/**
 * 上报电话联系
 * targetType: 1=从产品页拨给商户 2=从商户主页拨号
 * merchantId: 被联系商户，不传时后端自动推导
 */
function reportCall(targetType, targetId, merchantId) {
  const openid = getOpenid()
  const data = { targetType, targetId, openid }
  if (merchantId) {
    data.merchantId = merchantId
  }
  return request('/call', 'POST', data).catch(() => {})
}

/**
 * 收藏 / 取消收藏
 * targetType: 1=产品 2=商户；返回 Promise<{ favorited }>
 */
function toggleFavorite(targetType, targetId) {
  return request('/favorite/toggle', 'POST', {
    openid: getOpenid(),
    targetType: targetType,
    targetId: targetId
  })
}

/** 查询是否已收藏 */
function checkFavorite(targetType, targetId) {
  return request('/favorite/check', 'GET', {
    openid: getOpenid(),
    targetType: targetType,
    targetId: targetId
  })
}

/**
 * 获取本地访客标识（未接入微信登录，用于区分浏览记录）
 * 如需真实 openid，可在后端接入 code2session 后替换
 */
function getOpenid() {
  let openid = wx.getStorageSync('openid')
  if (!openid) {
    openid = 'mp_' + Date.now() + Math.random().toString(36).slice(2, 8)
    wx.setStorageSync('openid', openid)
  }
  return openid
}

module.exports = {
  HOST: HOST,
  request: request,
  getOpenid: getOpenid,
  reportView: reportView,
  reportForward: reportForward,
  reportCall: reportCall,
  toggleFavorite: toggleFavorite,
  checkFavorite: checkFavorite
}
