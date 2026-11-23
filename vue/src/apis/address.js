import instance from '../utils/request'

const post = (url, data = {}) => {
  const params = new URLSearchParams()
  for (const key in data) {
    // 新增地址时 aid 为 null，拼成字符串 "null" 会让后端 Integer 绑定报错
    if (data[key] === undefined || data[key] === null) continue
    params.append(key, data[key])
  }
  return instance.post(url, params)
}

export const addressListService = () => instance.get('/address/list')
export const addressSaveService = data => post('/address/save', data)
export const addressRemoveService = data => post('/address/remove', data)
export const addressSetDefaultService = data => post('/address/setDefault', data)
