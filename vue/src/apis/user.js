import instance from '../utils/request'
export function userLoginService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/user/login',params);
}
export function userRegisterService(data){
    let params=new URLSearchParams()
    for(let key in data){
        if(data[key]!==''&&data[key]!==undefined&&data[key]!==null){
            params.append(key,data[key])
        }
    }
    return instance.post('/user/register',params);
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
export function updateInfoService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key]==null?'':data[key])
    }
    return instance.post('/user/updateInfo',params);
}
export function uploadAvatarService(file){
    let formData=new FormData()
    formData.append('file',file)
    return instance.post('/user/uploadAvatar',formData);
}
export function userListService(params){
    return instance.get('/user/list',{params});
}
export function setUserStateService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/user/setState',params);
}
export function sendResetCodeService(email){
    return instance.post('/user/sendResetCode',new URLSearchParams({email}));
}
export function resetPasswordService(data){
    return instance.post('/user/resetPassword',new URLSearchParams(data));
}
