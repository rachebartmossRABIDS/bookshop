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
export function saveBookService(data){
    let params=new URLSearchParams()
    for(let key in data){
        if(data[key]!==undefined&&data[key]!==null){
            params.append(key,data[key])
        }
    }
    return instance.post('/book/save',params);
}
export function removeBookService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/book/remove',params);
}
export function uploadBookImageService(file){
    let formData=new FormData()
    formData.append('file',file)
    return instance.post('/book/uploadImage',formData);
}
export function hotWordsService(){
    return instance.get('/book/hotWords');
}
