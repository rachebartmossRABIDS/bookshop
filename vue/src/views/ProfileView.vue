<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showNotify } from 'vant';
import { useUserStore } from '../store/userStore'
import { usePicStore } from '../store/picStore'
import { updateInfoService, uploadAvatarService } from '../apis/user';

const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
const userStore=useUserStore();
const picIp=usePicStore().picIp

const tel=ref(userStore.info.tel || '')
const email=ref(userStore.info.email || '')
const uploading=ref(false)

const onAvatarRead=async(file)=>{
    uploading.value=true
    try {
        const result=await uploadAvatarService(file.file)
        userStore.setInfo(result.data)
        showNotify({type:'success', message:'头像更新成功'})
    } finally {
        uploading.value=false
    }
}
const saveInfo=async()=>{
    const t=tel.value.trim()
    const e=email.value.trim()
    if(t && !/^1[3-9]\d{9}$/.test(t)){
        showNotify({type:'danger', message:'手机号格式不正确'})
        return
    }
    if(e && !/^[\w.+-]+@[\w-]+(\.[\w-]+)+$/.test(e)){
        showNotify({type:'danger', message:'邮箱格式不正确'})
        return
    }
    const result=await updateInfoService({tel:t, email:e})
    userStore.setInfo(result.data)
    showNotify({type:'success', message:'资料已保存'})
}
</script>

<template>
    <div>
        <van-nav-bar title="修改个人信息" left-text="返回" left-arrow @click-left="goBack"/>
        <div class="avatar-box">
            <van-image
                round
                width="6rem"
                height="6rem"
                :src="picIp+(userStore.info.image || 'default.png')"
            />
            <van-uploader :after-read="onAvatarRead" :disabled="uploading" accept="image/*">
                <van-button size="small" icon="photograph" :loading="uploading">更换头像</van-button>
            </van-uploader>
            <div class="tip">支持 jpg/png/gif/webp，不超过 2MB</div>
        </div>
        <van-cell-group inset>
            <van-field :model-value="userStore.info.username" label="用户名" readonly />
            <van-field v-model="tel" label="手机号" placeholder="请输入 11 位手机号（可留空）" maxlength="11" />
            <van-field v-model="email" label="邮箱" placeholder="请输入邮箱（可留空）" />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" @click="saveInfo">保存资料</van-button>
        </div>
    </div>
</template>

<style scoped>
.avatar-box{
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    padding: 20px 0;
}
.tip{
    color: #999;
    font-size: 12px;
}
</style>