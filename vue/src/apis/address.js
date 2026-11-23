import instance from '../utils/request'

const post = (url, data = {}) => {
  const params = new URLSearchParams()
  for (const key in data) params.append(key, data[key])
  return instance.post(url, params)
}

export const addressListService = () => instance.get('/address/list')
export const addressSaveService = data => post('/address/save', data)
export const addressRemoveService = data => post('/address/remove', data)
export const addressSetDefaultService = data => post('/address/setDefault', data)
