<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
const router=useRouter();
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
const user=ref({
    username:'',
    image:'default.png'
})
import {useUserStore} from '../store/userStore'
import { showNotify } from 'vant';
import { notificationUnreadCountService } from '../apis/notification';
const userStore=useUserStore();
const unread=ref(0);
onMounted(async()=>{
    if(userStore.token){
        const result=await notificationUnreadCountService();
        unread.value=Number(result.data)||0;
    }
})
const logout=()=>{
    userStore.removeInfo();
    showNotify({type:'success', message:'已退出登录'});
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
    <div class="notice-entry">
        <van-icon name="bell" size="22" @click="router.push('/notifications')" />
        <van-badge v-if="unread>0" :content="unread" />
        <span @click="router.push('/notifications')">站内通知</span>
    </div>

    <van-grid :border="false">
    <van-grid-item icon="cart-o" to="/order" text="个人的订单"  />
    <van-grid-item icon="shopping-cart-o" to="/cart" text="购物车" />
    <van-grid-item icon="label-o" to="/favorites" text="收藏的书籍" />
    <van-grid-item icon="comment-o" text="个人的评论" to="/userComment"/> 
    <van-grid-item icon="edit" to="/profile" text="修改个人信息" />
    <van-grid-item icon="location-o" to="/address" text="收货地址" />
    <van-grid-item icon="edit" to="/updatePassword" text="修改登录密码" />
    <van-grid-item v-if="userStore.info.type==2" icon="manager-o" to="/admin" text="管理后台" />
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
.notice-entry{
    display:flex;
    align-items:center;
    gap:6px;
    margin:14px 18px;
    color:#666;
}
</style>
