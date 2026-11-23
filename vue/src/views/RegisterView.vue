<script setup>
import { ref } from 'vue';
import { showNotify } from 'vant';
import { useRouter } from 'vue-router';
import { userRegisterService } from '../apis/user'

const router = useRouter();
const goBack = () => {
    router.go(-1)
}
const form = ref({
    username: '',
    password: '',
    confirm: '',
    tel: '',
    email: ''
})
const register = async () => {
    if (form.value.username == '' || form.value.password == '') {
        showNotify({ type: 'danger', message: '用户名和密码不能为空' });
        return
    }
    if (form.value.username.length < 3 || form.value.username.length > 20) {
        showNotify({ type: 'danger', message: '用户名长度需在 3-20 个字符之间' });
        return
    }
    if (form.value.password.length < 6) {
        showNotify({ type: 'danger', message: '密码长度不能少于 6 位' });
        return
    }
    if (form.value.password != form.value.confirm) {
        showNotify({ type: 'danger', message: '两次输入的密码不一致' });
        return
    }
    await userRegisterService({
        username: form.value.username,
        password: form.value.password,
        tel: form.value.tel,
        email: form.value.email
    })
    showNotify({ type: 'success', message: '注册成功，请登录' });
    router.push('/login')
}
</script>

<template>
    <div>
      <van-nav-bar title="注册"
          left-text="返回"
          left-arrow
          @click-left="goBack"/>
      <van-form @submit="register">
        <van-cell-group inset>
          <van-field label="用户名" placeholder="3-20 个字符" v-model.trim="form.username" />
          <van-field label="密码" type="password" placeholder="至少 6 位" v-model.trim="form.password" />
          <van-field label="确认密码" type="password" placeholder="再次输入密码" v-model.trim="form.confirm" />
          <van-field label="手机号" placeholder="选填" v-model.trim="form.tel" />
          <van-field label="邮箱" placeholder="选填" v-model.trim="form.email" />
        </van-cell-group>
        <div style="margin: 16px;">
          <van-button block type="warning" native-type="submit">注册</van-button>
        </div>
      </van-form>
    </div>
  </template>
