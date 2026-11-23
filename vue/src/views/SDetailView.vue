<script setup>
import { useRoute,useRouter} from 'vue-router'
const route = useRoute()
const shop=ref({})
const sid=route.params.sid
import { shopSDetailService} from '../apis/shop'
const shopSDetail=async()=>{
    let result=await shopSDetailService({sid:sid})
    shop.value=result.data;
}
shopSDetail();
const router = useRouter()
const goBack=()=>{
    router.go(-1)
}
import { ref } from 'vue';
const books = ref([])
const loaded = ref(false)
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
import { bookListBySidService } from '../apis/book'
const bookList=async()=>{
    let result=await bookListBySidService({sid});
    books.value=result.data || [];
    loaded.value=true
}
bookList();
const goDetail=(item)=>{
    router.push('/detail/'+item.bid);
}
</script>

<template>
    <div>
        <van-nav-bar
        title=""
        left-text="返回"
        left-arrow
        @click-left="goBack"
        />
        <van-space direction="vertical" fill>
        <img :src="picIp+(shop.picture||'default.png')">
        <div>店铺名称：{{shop.name}}</div>
        <div>店铺简介：{{shop.description}}</div>        
    </van-space>
    <div class="list">
            <van-row type="flex" v-for="item in books" :key="item.bid" @click="goDetail(item)">
            <van-col span="16">
                <div>{{item.name}}</div>
                <div class="author">{{ item.author }}</div>
                <div class="price">￥{{ item.price }}</div>
            </van-col>
           <van-col span="8" class="img"><img :src="picIp+item.image" ></van-col> 
            </van-row>
    </div>
    <van-empty v-if="loaded && books.length===0" description="该店铺暂无在售图书" />
    <van-back-top right="5vw" bottom="5vh"/>
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
.title{
    font-size: 20px;
    font-weight: bold;
    margin: 10px;
}
img{
    width: 100%;
}
.comment .van-row{
    margin: 5px 0;
}
.comment .name{
    font-size: 15px;
    font-weight: bold;
    margin: 5px 0;
}
</style>