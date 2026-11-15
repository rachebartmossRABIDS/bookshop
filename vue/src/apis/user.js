import instance from '../utils/request'
export function userLoginService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/user/login',params);
}
export function updatePassword(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/user/updatePassword',params);
}
export function userInfoService(params){
    return instance.get('/user/'+params);
}
