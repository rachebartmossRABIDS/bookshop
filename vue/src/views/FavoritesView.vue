<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showNotify, showConfirmDialog } from 'vant';
import { useUserStore } from '../store/userStore'
import { usePicStore } from '../store/picStore'
import { favoriteListService, favoriteRemoveService } from '../apis/favorite';

const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
const userStore=useUserStore();
const picIp=usePicStore().picIp

const favorites=ref([])
const pageNo=ref(1)
const pageSize=ref(8)
const pages=ref(0)
const tip=ref('加载更多')
const loaded=ref(false)

const favoriteList=async()=>{
    if(!userStore.info.uid){
        return
    }
    const result=await favoriteListService({
        uid:userStore.info.uid,
        pageNo:pageNo.value,
        pageSize:pageSize.value
    })
    favorites.value.push(...(result.data.list || []))
    pages.value=result.data.pages
    loaded.value=true
}
favoriteList()

const refresh=async()=>{
    favorites.value=[]
    pageNo.value=1
    pages.value=0
    tip.value='加载更多'
    await favoriteList()
}
const getMore=()=>{
    pageNo.value++
    if(pageNo.value>pages.value){
        tip.value='没有更多了'
        return
    }
    favoriteList()
}
const goDetail=(item)=>{
    const bid=item.book && item.book.bid ? item.book.bid : item.bid
    if(bid){
        router.push('/detail/'+bid)
    }
}
const removeFavorite=async(item)=>{
    try {
        await showConfirmDialog({title:'取消收藏', message:'确定取消收藏该图书吗？'})
    } catch { return }
    await favoriteRemoveService({bid:item.bid})
    showNotify({type:'success', message:'已取消收藏'})
    refresh()
}
</script>

<template>
    <div>
        <van-nav-bar title="收藏的书籍" left-text="返回" left-arrow @click-left="goBack"/>
        <van-empty v-if="loaded && favorites.length===0" description="还没有收藏任何图书" />
        <div class="list">
            <div class="item" v-for="item in favorites" :key="item.fid">
                <van-row @click="goDetail(item)">
                    <van-col span="7" class="img">
                        <img :src="picIp+(item.book && item.book.image ? item.book.image : 'default.png')">
                    </van-col>
                    <van-col span="17" class="info">
                        <div class="name">{{ item.book ? item.book.name : '图书已删除' }}</div>
                        <div class="author">{{ item.book ? item.book.author : '' }}</div>
                        <div class="price" v-if="item.book">￥{{ item.book.price }}</div>
                        <div class="time">收藏于 {{ item.time }}</div>
                    </van-col>
                </van-row>
                <div class="actions">
                    <van-button size="small" type="danger" plain @click="removeFavorite(item)">取消收藏</van-button>
                </div>
            </div>
            <div class="load" v-if="favorites.length>0" @click="getMore">{{tip}}</div>
        </div>
        <van-back-top right="5vw" bottom="5vh"/>
    </div>
</template>

<style scoped>
.list{
    padding: 8px;
}
.item{
    border: 1px solid #ddd;
    margin-bottom: 8px;
}
.img img{
    width: 100%;
    height: 90px;
    object-fit: cover;
}
.info{
    padding-left: 10px;
}
.name{
    font-weight: bold;
}
.author{
    color: #666;
    font-size: 13px;
}
.price{
    color: #d10000;
}
.time{
    color: #aaa;
    font-size: 12px;
}
.actions{
    display: flex;
    justify-content: flex-end;
    padding: 6px 8px;
    border-top: 1px dashed #eee;
}
.load{
    text-align: center;
    margin: 10px;
}
</style>