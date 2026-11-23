<script setup>
import { useRouter } from 'vue-router';
const router=useRouter();
const goHome=()=>{
    router.replace('/home')
}
</script>

<template>
    <div class="notfound">
        <van-empty image="error" description="页面不存在或已被移除" />
        <div class="ops">
            <van-button round type="primary" size="small" @click="goHome">返回首页</van-button>
            <van-button round size="small" @click="router.go(-1)">返回上一页</van-button>
        </div>
    </div>
</template>

<style scoped>
.notfound{
    padding-top: 40px;
}
.ops{
    display: flex;
    justify-content: center;
    gap: 12px;
}
</style>