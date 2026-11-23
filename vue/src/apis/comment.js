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
export function allCommentListService(params){
    return instance.get('/comment/listAll',{params});
}
const post = (url, data) => {
    const params = new URLSearchParams()
    for (const key in data) params.append(key, data[key])
    return instance.post(url, params)
}
export function likeCommentService(data){
    return post('/comment/like', data)
}
export function unlikeCommentService(data){
    return post('/comment/unlike', data)
}
export function replyCommentService(data){
    return post('/comment/reply', data)
}
