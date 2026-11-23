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
const kindName=(kid)=>{
    const k=kinds.value.find(item=>item.kid===kid)
    return k?k.content:''
}
</script>

<template>
  <div class="shop-page">
        <van-tabs v-model:active="kid" @change="onKindChange">
            <van-tab v-for="item in kinds" :key="item.kid" :title="item.content" :name="item.kid" />
        </van-tabs>
        <div>
  <div class="list">
    <div class="shop-card" v-for="item in shops" :key="item.sid" @click="goSDetail(item)">
      <div class="cover"><img :src="picIp+(item.picture||item.logo||'default.png')"></div>
      <div class="meta">
        <div class="name">{{ item.name }}</div>
        <div class="score-row">
          <van-rate :model-value="Number(item.score)||5" readonly allow-half size="12" color="#ffb400" void-color="#e8e8e8" />
          <span class="score">{{ item.score }}</span>
          <van-tag plain type="primary" size="medium">{{ kindName(item.kid) }}</van-tag>
        </div>
        <div class="desc">{{ item.description }}</div>
      </div>
      <van-icon name="arrow" class="go" />
    </div>
</div>
<van-empty v-if="loaded && shops.length===0" description="该分类下暂无店铺" />
<van-back-top right="5vw" bottom="5vh"/>
    </div>
    <router-view/>
  </div>
</template>

<style scoped>
.shop-page{
    min-height: 100vh;
    background: #f7f8fa;
}
.list{
    padding: 10px 12px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.shop-card{
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 1px 4px rgba(0,0,0,.06);
    transition: transform .15s ease, box-shadow .15s ease;
}
.shop-card:active{
    transform: scale(.98);
    box-shadow: 0 1px 2px rgba(0,0,0,.08);
}
.cover{
    flex: 0 0 112px;
    height: 76px;
    border-radius: 8px;
    overflow: hidden;
    background: #f2f3f5;
}
.cover img{
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}
.meta{
    flex: 1;
    min-width: 0;
}
.name{
    font-size: 16px;
    font-weight: bold;
    color: #323233;
}
.score-row{
    display: flex;
    align-items: center;
    gap: 6px;
    margin: 6px 0;
}
.score{
    color: #ff976a;
    font-size: 12px;
    font-weight: bold;
}
.desc{
    font-size: 12px;
    color: #969799;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
.go{
    color: #c8c9cc;
}
</style>
