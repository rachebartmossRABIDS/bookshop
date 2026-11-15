import instance from '../utils/request'
export function bookOrderListService(params){
    return instance.get('/order/listByBid',{params});
}
export function userOrderListService(params){
    return instance.get('/order/listByUid',{params});
}
export function addOrderService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/order/save',params);
}
export function delOrderService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/order/remove',params);
}