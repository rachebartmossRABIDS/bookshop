import instance from '../utils/request'
export function kindListService(){
   return instance.get('/kind/list');
}
export function saveKindService(data){
    let params=new URLSearchParams()
    for(let key in data){
        if(data[key]!==undefined&&data[key]!==null){
            params.append(key,data[key])
        }
    }
    return instance.post('/kind/save',params);
}
export function removeKindService(data){
    let params=new URLSearchParams()
    for(let key in data){
        params.append(key,data[key])
    }
    return instance.post('/kind/remove',params);
}