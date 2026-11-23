<script setup>
import { ref} from 'vue'
const shops = ref([])
const loaded = ref(false)
import {useRouter} from 'vue-router'
const router = useRouter()
import { shopListService } from '../apis/shop'
const kid=ref(0)
const shopList=async()=>{
    if(!kid.value){
        return
    }
    loaded.value=false
    let result=await shopListService(kid.value);
    shops.value=result.data || [];
    loaded.value=true
}
import { kindListService } from '../apis/kind'
const kinds=ref([])
const kindList=async()=>{ 
    let result=await kindListService();
    kinds.value=result.data || [];
    if(kinds.value.length){
        kid.value=kinds.value[0].kid
        shopList()
    }
}
kindList();
const onKindChange=(name)=>{
    kid.value=name
    shopList()
}
const active = ref(0) 
const value = ref('')
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
const goSDetail=(item)=>{
    router.push('/sdetail/'+item.sid);
}
</script>

<template>
  <div>  
        <van-tabs v-model:active="kid" @change="onKindChange">
            <van-tab v-for="item in kinds" :key="item.kid" :title="item.content" :name="item.kid" />
        </van-tabs>
        <div> 
  <div class="list">
    <van-row gutter="10">
    <van-col span="12" v-for="(item) in shops" :key="item.sid" @click="goSDetail(item)"> 
         <div class="img"><img :src="picIp+item.logo" ></div>
            <div>{{item.name}}</div>
            <div>{{ item.score }}</div>
         </van-col>
</van-row>
</div>
<van-empty v-if="loaded && shops.length===0" description="该分类下暂无店铺" />
<van-back-top right="5vw" bottom="5vh"/>
    </div>
    <router-view/>
  </div>
</template>

<style scoped>
.van-swipe img{
    width: 100%;
    height: 280px;
}
.img img{
    width: 100%;
    height: 150px;
}

.list .van-row{
    margin: 5px 0;
}
.list.van-col{
    display: flex;
}
.list .time{
    margin-top: 30px;
    color: grey;
    font-size: 13px;
}
</style>
