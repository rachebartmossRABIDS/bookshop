import {defineStore} from 'pinia'
import { ref } from 'vue';
export const useUserStore=defineStore('userStore',()=>{
    const info=ref({});
    const token=ref('');
    const setInfo=(newInfo)=>{
        info.value=newInfo
    }
    const setToken=(newToken)=>{
        token.value=newToken
    }
    const removeInfo=()=>{
        info.value={}
        token.value=''
    }
    return {info,token,setInfo,setToken,removeInfo};
},{persist:true})
