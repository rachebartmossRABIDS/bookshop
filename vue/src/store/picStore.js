import {defineStore} from 'pinia'
export const usePicStore=defineStore('picStore',()=>{
    // 通过 vite 代理访问后端 /pic 静态图片服务，避免硬编码主机端口
    const picIp='/pic/';
    return {picIp};
})
