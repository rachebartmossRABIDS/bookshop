<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import BookManage from './admin/BookManage.vue';
import KindManage from './admin/KindManage.vue';
import ShopManage from './admin/ShopManage.vue';
import UserManage from './admin/UserManage.vue';
import OrderManage from './admin/OrderManage.vue';
import CommentManage from './admin/CommentManage.vue';
import Overview from './admin/Overview.vue';
import OperationLogs from './admin/OperationLogs.vue';

const router=useRouter();
const goBack=()=>{
    router.push('/mine')
}
const active=ref(0)
</script>

<template>
    <div class="admin">
        <van-nav-bar title="管理后台" left-text="返回" left-arrow @click-left="goBack"/>
        <van-tabs v-model:active="active" sticky>
            <van-tab title="概览"><Overview/></van-tab>
            <van-tab title="图书"><BookManage/></van-tab>
            <van-tab title="分类"><KindManage/></van-tab>
            <van-tab title="店铺"><ShopManage/></van-tab>
            <van-tab title="用户"><UserManage/></van-tab>
            <van-tab title="订单"><OrderManage/></van-tab>
            <van-tab title="评论"><CommentManage/></van-tab>
            <van-tab title="日志"><OperationLogs/></van-tab>
        </van-tabs>
    </div>
</template>

<style scoped>
.admin{
    padding-bottom: 20px;
}
</style>
