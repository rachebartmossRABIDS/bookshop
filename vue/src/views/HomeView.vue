<script setup>
import { ref} from 'vue'
const books = ref([])
import {useRouter} from 'vue-router'
const router = useRouter();
import { bookListService } from '../apis/book'
const kid=ref(0)
const bookList=async()=>{
    if(!kid.value){
        return
    }
    let result=await bookListService(kid.value);
    books.value=result.data;
}
import { kindListService } from '../apis/kind'
const kinds=ref([])
const kindList=async()=>{ 
    let result=await kindListService();
    kinds.value=result.data || [];
    if(kinds.value.length){
        kid.value=kinds.value[0].kid
        bookList()
    }
}
kindList();
const onKindChange=(name)=>{
    kid.value=name
    bookList()
}
const goDetail=(item)=>{
    router.push('/detail/'+item.bid);
}
const active = ref(0) 
const value = ref('')
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
const goSearch=()=>{
    router.push('/search')
}
</script>

<template>
    <div>
        <van-sticky>
       <div>
         <van-search
            v-model="value"
            shape="round"
            background="blue"
            placeholder="搜索关键词"
            @click="goSearch"
            />
       </div> 
        </van-sticky>
        <van-tabs v-model:active="kid" @change="onKindChange">
            <van-tab v-for="item in kinds" :key="item.kid" :title="item.content" :name="item.kid" />
        </van-tabs>
        <div> 
            <div class="list"> 
            <van-row v-for="(item) in books" :key="item.bid" @click="goDetail(item)">
                <van-col span="8" class="img"><img :src="picIp+item.image"></van-col>
                <van-col span="16" class="flex-column">
                 <div class="name">{{item.name}}</div>
                <div class="author">{{ item.author }}</div>
                <div class="price">￥{{ item.price }}</div>  
                </van-col>
            </van-row>
        </div>
    <van-back-top right="5vw" bottom="5vh"/>
    </div>
    <router-view/>
    </div>
</template>

<style scoped>
.author{
    color: gray;
}
.price{
    height: 25px;
    font-size: 25px;
    color: #d10000;
    line-height: 25px;
    font-weight: 400;
}
.img img{
    width: 100%;
    height: 150px;
}
.list .van-row{
    margin: 0px 0;
}
.list.van-col{
    display: flex;
}
</style>