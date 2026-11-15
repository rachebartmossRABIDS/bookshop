<script setup>
import { ref } from 'vue';
const content=ref('')
import { showNotify } from 'vant';
import { useRoute,useRouter } from 'vue-router';
const onSearch = (val) => {
    if(val==''){
        showNotify({type: 'danger', message:'您输入的搜索内容为空'});
        return
    }
    router.push('/searchResult/'+val)
}
const onClickButton = () =>{
    onSearch(content.value)
} 
const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
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
            v-model.trim="content"
            show-action
            label=""
            placeholder="请输入搜索关键词"
            @search="onSearch"
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
           
        </div>
        <van-back-top right="5vw" bottom="5vh"/>
      </div>
      <router-view/>      
    </div>
</template>

<style scoped>
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
.img img{
    width: 100%;
    height: 80px;
}
.van-swipe-item img{
    width: 100%;
    height: 180px; 
}
</style>
