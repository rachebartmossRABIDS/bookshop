<template>
    <router-view></router-view>
    <van-tabbar route replace v-model="active" fixed active-color="red" inactive-color="black" :placeholder="true">
    <van-tabbar-item replace icon="home-o" to = '/home'>书籍列表</van-tabbar-item>
    <van-tabbar-item replace icon="shop" to="/shop">店铺</van-tabbar-item>
    <van-tabbar-item replace icon="shopping-cart-o" to="/cart">购物车</van-tabbar-item>
    <van-tabbar-item replace icon="user-o" to='/mine'>个人中心</van-tabbar-item>
    </van-tabbar>
</template>
    <script setup>
    import { ref } from 'vue'
    const active = ref(0)
    </script>
