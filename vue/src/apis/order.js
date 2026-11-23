import instance from '../utils/request'
export function userOrderListService(params){
    return instance.get('/order/listByUid',{params});
}
const post = (url, data) => {
    let params = new URLSearchParams()
    for (let key in data) {
        params.append(key, data[key])
    }
    return instance.post(url, params);
}
export function addOrderService(data){
    return post('/order/save', data);
}
export function payOrderService(data){
    return post('/order/pay', data);
}
export function cancelOrderService(data){
    return post('/order/cancel', data);
}
export function confirmOrderService(data){
    return post('/order/confirm', data);
}
export function delOrderService(data){
    return post('/order/remove', data);
}
export function shipOrderService(data){
    return post('/order/ship', data);
}
export function allOrderListService(params){
    return instance.get('/order/listAll',{params});
}
export function orderDetailService(params){
    return instance.get('/order/detail',{params});
}
export function refundOrderService(data){
    return post('/order/refund', data);
}
