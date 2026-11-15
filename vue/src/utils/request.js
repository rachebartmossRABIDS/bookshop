import axios from 'axios'
const baseURL = '/api';
const instance = axios.create({
        baseURL
})
import { showNotify } from 'vant';
instance.interceptors.response.use(
    result=>{
        if(result.data.code==1){
            return result.data;
        }
        if(result.data.code==undefined){
            
            return result.data;
        }
        showNotify({type: 'danger', message: result.data.msg?result.data.msg:'服务异常'});
        return Promise.reject(result.data)
    },
    err=>{
        showNotify({type: 'danger', message: '服务异常'});
        return Promise.reject(err)
    }
)
export default instance