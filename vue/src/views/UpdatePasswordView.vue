<script setup>
import { ref } from 'vue';
const user=ref({
    username:'',
    image:'default.png',
    password:''
})
import { showNotify } from 'vant';
import { useRouter } from 'vue-router';
const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
import {updatePassword  } from '../apis/user';
import {useUserStore} from '../store/userStore'
const userStore=useUserStore()
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
const OldPassword=ref('');
const NewPassword=ref('');
const update=async()=>{
    try{
    let result=await updatePassword({
    uid:userStore.info.uid,
    NewPassword:NewPassword.value,
    OldPassword:OldPassword.value
  })
    if(result.code===1){
        showNotify({type:'success',message:'修改成功'});
        router.push('/mine');
    }else{
        showNotify({type:'danger',message:result.msg});
    }
    }catch(error){
        console.error('更新密码错误',error);
        showNotify({type:'danger',message:'修改失败'});
    }
}
</script>

<template>
  <div>
    <van-nav-bar title="修改密码" 
    left-text="返回"
        left-arrow
        @click-left="goBack"/>
        <div class="user">
        <div>
            <van-image
            round
            width="5rem"
            height="5rem"
            :src="userStore.info.image?picIp+userStore.info.image:picIp+user.image"
            />
        </div>
        <div class="name flex-column-center" v-if="userStore.info.username">{{ userStore.info.username }}</div>
        <div class="name flex-column-center" v-else>
            <router-link to="/login">您未登录！点击此处登录</router-link>
        </div>
  </div>
</div>
<div> 
        <div class="password-section">
            <van-field name="OldPassword" label="原密码" v-model="OldPassword" type="password" />
            <van-field name="NewPassword" label="新密码" v-model="NewPassword" type="password" />
        </div>
        <div style="margin: 16px;">
            <van-button round block type="primary" @click="update">
                确认修改
            </van-button>
        </div>
    </div>
</template>


<style scoped>

.user{
    display:flex;
}
.user .name{
    margin-left: 10px;
}

body {
    font-family: Arial, sans-serif;
    background-color: #f4f4f4;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
}

.reset-password-container {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    width: 300px;
    text-align: center;
}

h1 {
    margin-bottom: 20px;
    color: #333;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
    color: #555;
}

input[type="password"] {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

button {
    width: 100%;
    padding: 10px;
    background-color: #007BFF;
    border: none;
    border-radius: 4px;
    color: #fff;
    font-size: 16px;
    cursor: pointer;
}

button:hover {
    background-color: #0056b3;
}

.message {
    margin-top: 15px;
    color: #d9534f;
    font-size: 14px;
}
</style>