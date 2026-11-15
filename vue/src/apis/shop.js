import instance from '../utils/request'
export function shopListService(params){
    return instance.get('/shop/list/'+params);
}
export function shopSDetailService(params){
    return instance.get('/shop/sdetail',{params:params});
}
