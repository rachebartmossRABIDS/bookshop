<script setup>
import { ref } from 'vue';
import { showNotify } from 'vant';
import { useRoute,useRouter } from 'vue-router';
const content=useRoute().params.content
const router=useRouter();
const onClickButton = () =>{
    goSearch()
} 
const goSearch=()=>{
    router.push('/search')
}
const goBack=()=>{
    router.go(-1)
}
const books=ref([])
const pageNo=ref(1);
const pageSize=ref(8);
const pages=ref(0)
import {searchService} from '../apis/book'
const bookList=async()=>{
    let result=await searchService({
    name:content,
    pageNo:pageNo.value,
    pageSize:pageSize.value
})
    books.value.push(...result.data.list)
    pages.value=result.data.pages
}
bookList()
const tip=ref('加载更多')
const getMore=()=>{
    pageNo.value++;
    if(pageNo.value>pages.value){
        tip.value='没有更多了'
        return
    }
    bookList()
}
const goDetail=(item)=>{
    router.push('/detail/'+item.bid);//跳转路由
}
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
</script>

<template>
    <div>
        <van-sticky>
            <van-row>
                <van-col class="back" span="1" @click="goBack">
                    <van-icon name="arrow-left" />
                </van-col>
                <van-col span="23">
            <van-search
            v-model="content"
            show-action
            label=""
            placeholder="请输入搜索关键词"
            @click="goSearch"
            >
            <template #action>
            <div @click="onClickButton">搜索</div>
            </template>
            </van-search>
            </van-col>
            </van-row>  
        </van-sticky>    
      <div>
        <div class="list"> 
            <van-row v-for="(item) in books" :key="item.bid" @click="goDetail(item)">
                <van-col span="16" class="flex-column">
                    <div>{{ item.name }}</div>
                    <div class="time">{{ item.author }}</div>
                    <div class="price">￥{{ item.price }}</div>
                </van-col>
                <van-col span="8" class="img"><img :src="picIp+item.image"></van-col>
            </van-row>
        </div>
        <div class="load" @click="getMore">
            {{ tip }}
        </div>
        <van-back-top right="5vw" bottom="5vh"/>
      </div>
      <router-view/>      
    </div>
</template>

<style scoped>
.load{
    text-align: center;
    margin: 10px;
}
.back{
    height: 50px;
    line-height: 50px;
}
.list .van-col{
    display: flex;
}
.list .van-row{
    margin: 5px 0;
}
.list .time{
    color: #aaa;
    font-size: 13px;
}
.price{
    color: #d10000;
}
.img img{
    width: 100%;
    height: 80px;
}
.van-swipe-item img{
    width: 100%;
    height: 180px; 
}
</style>
