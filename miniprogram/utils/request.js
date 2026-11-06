/**
 * 小程序接口请求封装
 * 当前指向 ECS 生产环境（仅体验版/开发版可用，需勾选"不校验合法域名"）
 * 本地调试改回 http://localhost:8080；域名备案完成后改为 https 域名
 */
const HOST = 'http://47.111.99.105'
const BASE_URL = HOST + '/api/mp'
const OPENID_FLAG = 'openidReal'

let openidCache = ''
let loginPromise = null
let lastLoginToastAt = 0

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

/** 读取本地 openid；没有真实登录标记的旧数据（mp_ 假标识）直接丢弃 */
function cachedOpenid() {
  if (openidCache) {
    return openidCache
  }
  const stored = wx.getStorageSync('openid') || ''
  if (stored && wx.getStorageSync(OPENID_FLAG) === '1') {
    openidCache = stored
    return openidCache
  }
  if (stored) {
    wx.removeStorageSync('openid')
  }
  return ''
}

function wxLogin() {
  return new Promise(function (resolve, reject) {
    wx.login({
      success: function (res) {
        if (!res.code) {
          reject(new Error('微信登录失败，请稍后重试'))
          return
        }
        wx.request({
          url: BASE_URL + '/login',
          method: 'POST',
          data: { code: res.code },
          header: { 'content-type': 'application/json' },
          success: function (r) {
            const body = r.data || {}
            if (body.code === 200 && body.data && body.data.openid) {
              resolve(body.data.openid)
            } else {
              reject(new Error(body.msg || '微信登录失败，请稍后重试'))
            }
          },
          fail: function () {
            reject(new Error('网络异常，请检查后端服务'))
          }
        })
      },
      fail: function () {
        reject(new Error('微信登录失败，请稍后重试'))
      }
    })
  })
}

/** 确保已拿到 openid（同一时间只发起一次登录） */
function ensureLogin() {
  const cached = cachedOpenid()
  if (cached) {
    return Promise.resolve(cached)
  }
  if (loginPromise) {
    return loginPromise
  }
  loginPromise = wxLogin()
    .then(function (openid) {
      openidCache = openid
      wx.setStorageSync('openid', openid)
      wx.setStorageSync(OPENID_FLAG, '1')
      return openid
    })
    .catch(function (err) {
      loginPromise = null
      const now = Date.now()
      if (now - lastLoginToastAt > 10000) {
        lastLoginToastAt = now
        wx.showToast({ title: (err && err.message) || '微信登录失败', icon: 'none' })
      }
      throw err
    })
  return loginPromise
}

/** 需要 openid 的请求：先登录，再自动带上 openid */
function openidRequest(url, method, data) {
  return ensureLogin().then(function (openid) {
    return request(url, method, Object.assign({}, data || {}, { openid: openid }))
  })
}

/** 上报浏览 */
function reportView(targetType, targetId) {
  return openidRequest('/view', 'POST', { targetType: targetType, targetId: targetId }).catch(function () {})
}

/** 上报转发 */
function reportForward(targetType, targetId) {
  return openidRequest('/forward', 'POST', { targetType: targetType, targetId: targetId }).catch(function () {})
}

/**
 * 上报电话联系
 * targetType: 1=从产品页拨给商户 2=从商户主页拨号
 * merchantId: 被联系商户，不传时后端自动推导
 */
function reportCall(targetType, targetId, merchantId) {
  const data = { targetType: targetType, targetId: targetId }
  if (merchantId) {
    data.merchantId = merchantId
  }
  return openidRequest('/call', 'POST', data).catch(function () {})
}

/**
 * 收藏 / 取消收藏
 * targetType: 1=产品 2=商户；返回 Promise<{ favorited }>
 */
function toggleFavorite(targetType, targetId) {
  return openidRequest('/favorite/toggle', 'POST', {
    targetType: targetType,
    targetId: targetId
  })
}

/** 查询是否已收藏 */
function checkFavorite(targetType, targetId) {
  return openidRequest('/favorite/check', 'GET', {
    targetType: targetType,
    targetId: targetId
  })
}

/** 上传图片（头像等），返回可访问地址 */
function uploadImage(filePath) {
  return new Promise(function (resolve, reject) {
    wx.uploadFile({
      url: BASE_URL + '/upload',
      filePath: filePath,
      name: 'file',
      success: function (res) {
        let body = {}
        try {
          body = JSON.parse(res.data)
        } catch (e) {}
        if (body.code === 200 && body.data && body.data.url) {
          resolve(body.data.url)
        } else {
          reject(new Error(body.msg || '图片上传失败'))
        }
      },
      fail: function () {
        reject(new Error('图片上传失败，请检查网络'))
      }
    })
  })
}

/**
 * 检查是否有新的商户报价，未读时在 tabBar「我的」显示红点
 * 静默请求，不打扰用户；返回最新报价时间戳
 */
function checkQuoteNotice() {
  return ensureLogin()
    .then(function (openid) {
      return new Promise(function (resolve) {
        wx.request({
          url: BASE_URL + '/inquiries',
          method: 'GET',
          data: { openid: openid },
          success: function (res) {
            const body = res.data || {}
            if (body.code !== 200 || !body.data) {
              resolve(0)
              return
            }
            let latest = 0
            body.data.forEach(function (item) {
              if (!item.quoteTime) return
              const t = new Date(item.quoteTime).getTime()
              if (t && t > latest) latest = t
            })
            const seen = Number(wx.getStorageSync('quoteSeenTime') || 0)
            if (latest && latest > seen) {
              wx.showTabBarRedDot({ index: 2, fail: function () {} })
              resolve(latest)
            } else {
              wx.hideTabBarRedDot({ index: 2, fail: function () {} })
              resolve(0)
            }
          },
          fail: function () {
            resolve(0)
          }
        })
      })
    })
    .catch(function () {
      return 0
    })
}

module.exports = {
  HOST: HOST,
  request: request,
  ensureLogin: ensureLogin,
  openidRequest: openidRequest,
  uploadImage: uploadImage,
  reportView: reportView,
  reportForward: reportForward,
  reportCall: reportCall,
  toggleFavorite: toggleFavorite,
  checkFavorite: checkFavorite,
  checkQuoteNotice: checkQuoteNotice
}
