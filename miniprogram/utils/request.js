/**
 * 小程序接口请求封装
 * 开发阶段用 http://localhost:8080（模拟器需勾选"不校验合法域名"）
 * 真机调试请改成局域网 IP，上线后改成 https 域名
 */
const BASE_URL = 'http://localhost:8080/api/mp'

function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + url,
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
  request: request,
  getOpenid: getOpenid,
  reportView: reportView,
  reportForward: reportForward
}
