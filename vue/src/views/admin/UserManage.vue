<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { useUserStore } from '../../store/userStore'
import { userListService, setUserStateService } from '../../apis/user';

const userStore=useUserStore();
const users=ref([])
const pageNo=ref(1)
const pageSize=ref(10)
const pages=ref(0)
const tip=ref('加载更多')

const load=async()=>{
    const result=await userListService({pageNo:pageNo.value, pageSize:pageSize.value})
    users.value.push(...(result.data.list || []))
    pages.value=result.data.pages
}
load()
const refresh=async()=>{
    users.value=[]
    pageNo.value=1
    pages.value=0
    tip.value='加载更多'
    await load()
}
const getMore=()=>{
    pageNo.value++
    if(pageNo.value>pages.value){
        tip.value='没有更多了'
        return
    }
    load()
}
const setState=async(item,state)=>{
    const action=state===1?'封禁':'解封'
    try {
        await showConfirmDialog({title:action+'用户', message:`确定${action}用户「${item.username}」吗？`})
    } catch { return }
    await setUserStateService({uid:item.uid, state})
    showNotify({type:'success', message:action+'成功'})
    refresh()
}
</script>

<template>
    <div class="manage">
        <div class="item" v-for="item in users" :key="item.uid">
            <div class="row">
                <span class="name">{{ item.username }}</span>
                <van-tag v-if="item.type==2" type="primary">管理员</van-tag>
                <van-tag v-else type="success">普通用户</van-tag>
                <van-tag v-if="item.state==1" type="danger">已封禁</van-tag>
            </div>
            <div class="meta">UID：{{ item.uid }} ｜ 信誉：{{ item.reputation || '-' }}</div>
            <div class="meta">手机：{{ item.tel || '-' }} ｜ 邮箱：{{ item.email || '-' }}</div>
            <div class="actions" v-if="item.uid!=userStore.info.uid">
                <van-button v-if="item.state!=1" size="mini" type="danger" plain @click="setState(item,1)">封禁</van-button>
                <van-button v-else size="mini" type="success" plain @click="setState(item,0)">解封</van-button>
            </div>
        </div>
        <div class="load" @click="getMore">{{tip}}</div>
    </div>
</template>

<style scoped>
.manage{
    padding: 10px;
}
.item{
    border: 1px solid #ddd;
    margin-bottom: 8px;
    padding: 8px;
}
.row{
    display: flex;
    align-items: center;
    gap: 6px;
}
.name{
    font-weight: bold;
}
.meta{
    color: #666;
    font-size: 13px;
    margin-top: 2px;
}
.actions{
    display: flex;
    justify-content: flex-end;
    margin-top: 6px;
}
.load{
    text-align: center;
    margin: 10px;
}
</style>