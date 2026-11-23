import instance from '../utils/request'

const post = (url, data) => {
    let params = new URLSearchParams()
    for (let key in data) {
        params.append(key, data[key])
    }
    return instance.post(url, params);
}

export function favoriteSaveService(data){
    return post('/favorite/save', data);
}
export function favoriteRemoveService(data){
    return post('/favorite/remove', data);
}
export function favoriteListService(params){
    return instance.get('/favorite/listByUid', {params});
}
export function favoriteCheckService(params){
    return instance.get('/favorite/check', {params});
}