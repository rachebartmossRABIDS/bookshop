import instance from '../utils/request'
export function bookListService(params){
    return instance.get('/book/list/'+params);
}
export function bookListBySidService(params){
    return instance.get('/book/listBySid',{params:params});
}
export function bookDetailService(params){
    return instance.get('/book/detail',{params:params});
}
export function searchService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/book/search',params);
}
