import instance from '../utils/request'

const post = (url, data = {}) => {
  const params = new URLSearchParams()
  for (const key in data) params.append(key, data[key])
  return instance.post(url, params)
}

export const notificationListService = params => instance.get('/notification/list', { params })
export const notificationReadService = data => post('/notification/read', data)
export const notificationUnreadCountService = () => instance.get('/notification/unreadCount')
