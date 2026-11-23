import axios from 'axios'
import { showNotify } from 'vant';
import { useUserStore } from '../store/userStore'

const baseURL = '/api';
const instance = axios.create({
        baseURL
})

// 请求拦截器：自动携带登录 token
instance.interceptors.request.use(config=>{
    const userStore=useUserStore();
    if(userStore.token){
        config.headers.Authorization='Bearer '+userStore.token;
    }
    return config;
})

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
        if(err.response && err.response.status===401){
            // 未登录或 token 过期：清空登录状态并跳转登录页
            const userStore=useUserStore();
            userStore.removeInfo();
            showNotify({type: 'danger', message: '请先登录'});
            window.location.hash='#/login';
            return Promise.reject(err)
        }
        showNotify({type: 'danger', message: '服务异常'});
        return Promise.reject(err)
    }
)
export default instance
