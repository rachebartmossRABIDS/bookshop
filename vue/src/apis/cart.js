import instance from '../utils/request'

const post = (url, data) => {
    let params = new URLSearchParams()
    for (let key in data) {
        params.append(key, data[key])
    }
    return instance.post(url, params);
}

export function cartAddService(data){
    return post('/cart/add', data);
}
export function cartUpdateNumService(data){
    return post('/cart/updateNum', data);
}
export function cartRemoveService(data){
    return post('/cart/remove', data);
}
export function cartListService(){
    return instance.get('/cart/list');
}
export function cartCheckoutService(data){
    return post('/cart/checkout', data);
}