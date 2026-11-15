import instance from '../utils/request'
export function bookCommentListService(params){
    return instance.get('/comment/listByBid',{params});
}
export function userCommentListService(params){
    return instance.get('/comment/listByUid',{params});
}
export function addCommentService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/comment/save',params);
}
export function delCommentService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/comment/remove',params);
}