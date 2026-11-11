const { HOST, ensureLogin, clearMemberAccess } = require('./request')

const TOKEN_KEY = 'merchantToken'
const SHOP_KEY = 'merchantShop'
const LOGIN_PAGE = '/pages/merchant/login/login'

function token() {
  return wx.getStorageSync(TOKEN_KEY) || ''
}

function cachedShop() {
  return wx.getStorageSync(SHOP_KEY) || {}
}

function saveSession(data) {
  if (!data) return
  if (data.token) {
    wx.setStorageSync(TOKEN_KEY, data.token)
  }
  const old = cachedShop()
  wx.setStorageSync(SHOP_KEY, {
    merchantId: data.merchantId || old.merchantId || 0,
    shopName: data.shopName || old.shopName || '',
    shopLogo: data.shopLogo || old.shopLogo || '',
    realName: data.realName || old.realName || '',
    mustChangePassword: data.mustChangePassword === undefined
      ? !!old.mustChangePassword
      : !!data.mustChangePassword
  })
}

function markPasswordChanged() {
  const shop = cachedShop()
  shop.mustChangePassword = false
  wx.setStorageSync(SHOP_KEY, shop)
}

function clearSession() {
  wx.removeStorageSync(TOKEN_KEY)
  wx.removeStorageSync(SHOP_KEY)
}

function toLogin() {
  clearSession()
  wx.redirectTo({ url: LOGIN_PAGE })
}

/** 带 JWT 的请求：path 以 /api/ 开头 */
function authRequest(path, method = 'GET', data = {}) {
  return new Promise(function (resolve, reject) {
    const current = token()
    if (!current) {
      toLogin()
      reject({ code: 401 })
      return
    }
    wx.request({
      url: HOST + path,
      method: method,
      data: data,
      header: {
        'content-type': 'application/json',
        Authorization: 'Bearer ' + current
      },
      success: function (res) {
        const body = res.data || {}
        if (body.code === 200) {
          resolve(body.data)
          return
        }
        if (body.code === 401) {
          toLogin()
        } else {
          wx.showToast({ title: body.msg || '请求失败', icon: 'none' })
        }
        reject(body)
      },
      fail: function (err) {
        wx.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
        reject(err)
      }
    })
  })
}

/** 商户端接口 */
function merchantRequest(path, method, data) {
  return authRequest('/api/merchant' + path, method, data)
}

/** 账号接口（改密等） */
function accountRequest(path, method, data) {
  return authRequest('/api/account' + path, method, data)
}

/** 微信 openid 与商户账号绑定 */
function bindAccount(openid, username, password) {
  return new Promise(function (resolve, reject) {
    wx.request({
      url: HOST + '/api/mp/merchant/bind',
      method: 'POST',
      data: { openid: openid, username: username, password: password },
      header: { 'content-type': 'application/json' },
      success: function (res) {
        const body = res.data || {}
        if (body.code === 200 && body.data) {
          clearMemberAccess()
          resolve(body.data)
          return
        }
        wx.showToast({ title: body.msg || '绑定失败', icon: 'none' })
        reject(body)
      },
      fail: function (err) {
        wx.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
        reject(err)
      }
    })
  })
}

/** 查询绑定状态并刷新 token */
function refreshSession() {
  return ensureLogin().then(function (openid) {
    return new Promise(function (resolve, reject) {
      wx.request({
        url: HOST + '/api/mp/merchant/session',
        data: { openid: openid },
        success: function (res) {
          const body = res.data || {}
          if (body.code === 200 && body.data) {
            if (body.data.bound) {
              saveSession(body.data)
            } else {
              clearSession()
            }
            resolve(body.data)
            return
          }
          reject(body)
        },
        fail: reject
      })
    })
  })
}

function unbind() {
  return ensureLogin().then(function (openid) {
    return authRequest('/api/mp/merchant/unbind', 'POST', { openid: openid }).then(function (data) {
      clearMemberAccess()
      return data
    })
  })
}

module.exports = {
  HOST: HOST,
  LOGIN_PAGE: LOGIN_PAGE,
  token: token,
  cachedShop: cachedShop,
  saveSession: saveSession,
  markPasswordChanged: markPasswordChanged,
  clearSession: clearSession,
  bindAccount: bindAccount,
  refreshSession: refreshSession,
  merchantRequest: merchantRequest,
  accountRequest: accountRequest,
  unbind: unbind
}
