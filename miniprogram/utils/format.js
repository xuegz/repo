/**
 * 小程序端通用时间/日期格式化工具。
 * 之前 merchant 分包多个页面各自重复实现 pad/todayStr/timeText，统一收敛到这里。
 */

function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

/** 当前日期 yyyy-MM-dd（用于报价有效期过期判断） */
function todayStr() {
  const d = new Date()
  return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
}

/** 解析后端时间字符串（2026-01-02T03:04:05 或带毫秒），失败返回 null */
function parseTime(value) {
  if (!value) {
    return null
  }
  const date = new Date(String(value).replace(/-/g, '/').replace('T', ' ').split('.')[0])
  return isNaN(date.getTime()) ? null : date
}

/** yyyy-MM-dd HH:mm（列表展示创建/报价时间） */
function formatDateTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 16) : ''
}

/** 聊天气泡时间：只显示 时:分 */
function chatTime(value) {
  const date = parseTime(value)
  if (!date) {
    return ''
  }
  return pad(date.getHours()) + ':' + pad(date.getMinutes())
}

/** 会话列表时间：今天显示 时:分，昨天显示「昨天」，更早显示 M-D */
function convTime(value) {
  const date = parseTime(value)
  if (!date) {
    return ''
  }
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return pad(date.getHours()) + ':' + pad(date.getMinutes())
  }
  const yesterday = new Date(now.getTime() - 86400000)
  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  }
  return (date.getMonth() + 1) + '-' + date.getDate()
}

module.exports = {
  pad: pad,
  todayStr: todayStr,
  parseTime: parseTime,
  formatDateTime: formatDateTime,
  chatTime: chatTime,
  convTime: convTime
}
