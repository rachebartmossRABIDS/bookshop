<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const router=useRouter();
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
const user=ref({
    username:'',
    image:'default.png'
})
import {useUserStore} from '../store/userStore'
const userStore=useUserStore();
const logout=()=>{
    userStore.removeInfo();
}
</script>

<template>
    <div class="user">
       <div>
        <van-image
        round
        width="5rem"
        height="5rem"
        :src="userStore.info.image?picIp+userStore.info.image:picIp+user.image"
        />
       </div>
       <div class="name flex-column-center" v-if="userStore.info.username">{{userStore.info.username}}</div>
        <div class="name flex-column-center" v-else>
            <router-link to="/login">您未登录，点击此处进行登录</router-link>
        </div>
    </div>

    <van-grid :border="false">
    <van-grid-item icon="cart-o" to="/order" text="个人的订单"  />
    <van-grid-item icon="label-o" text="收藏的书籍" /> 
    <van-grid-item icon="comment-o" text="个人的评论" to="/userComment"/> 
    <van-grid-item icon="edit"  text="修改个人信息" /> 
    <van-grid-item icon="edit" to="/updatePassword" text="修改登录密码" />
    <van-grid-item icon="delete-o" @click="logout" text="退出登录" />
    </van-grid>
</template>

<style scoped>
.user{
    display: flex;
}
.user .name{
    margin-left: 20px;
}
</style>