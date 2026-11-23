import instance from '../utils/request'
export function shopListService(params){
    return instance.get('/shop/list/'+params);
}
export function shopSDetailService(params){
    return instance.get('/shop/sdetail',{params:params});
}
export function shopListAllService(){
    return instance.get('/shop/listAll');
}
export function saveShopService(data){
    let params=new URLSearchParams()
    for(let key in data){
        if(data[key]!==undefined&&data[key]!==null){
            params.append(key,data[key])
        }
    }
    return instance.post('/shop/save',params);
}
export function removeShopService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/shop/remove',params);
}
