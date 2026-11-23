<script setup>
import { ref } from 'vue';
const user=ref({
    username:'',
    password:''
})
import { showNotify } from 'vant';
import { useRouter, useRoute } from 'vue-router';
const router=useRouter();
const route=useRoute();
const goBack=()=>{
    router.go(-1)
}
import { userLoginService } from '../apis/user'
import {useUserStore} from '../store/userStore'
const userStore=useUserStore();
const login=async()=>{
    if(user.value.username==''||user.value.password==''){
      showNotify({type: 'danger', message:'您提交的用户名或密码为空'});
        return
    }
    let result=await userLoginService(user.value)
    userStore.setToken(result.data.token)
    userStore.setInfo(result.data.user)
    showNotify({type: 'success', message:'登录成功'});
    router.push(route.query.redirect || '/mine')
}
</script>

<template>
    <div>
      <van-nav-bar title="登录"  
          left-text="返回"
          left-arrow
          @click-left="goBack"/>
      <van-form @submit="login">
        <van-cell-group inset>
          <van-field label="用户名" placeholder="请输入用户名" v-model.trim="user.username" />
          <van-field label="密码" type="password" placeholder="请输入密码" v-model.trim="user.password"/>
        </van-cell-group>
        <div class="regist-link">
          <router-link to="/forgot-password">忘记密码</router-link>
          <span class="split">|</span>
          <router-link to="/register">没有账号？去注册</router-link>
        </div>
        <van-button block type="warning" native-type="submit">提交</van-button>
      </van-form>
    </div>
  </template>
  
  <style scoped>
  .regist-link{
  text-align: right;
  margin: 10px 16px;
}
.split{
  margin: 0 8px;
  color: #bbb;
}
  </style>
