<script setup>
import { onMounted, ref } from 'vue';
const content=ref('')
import { showNotify } from 'vant';
import { useRoute,useRouter } from 'vue-router';
import { hotWordsService } from '../apis/book';
const historyKey='bookshop_search_history'
const readHistory=()=>{
    try {
        const value=JSON.parse(localStorage.getItem(historyKey)||'[]')
        return Array.isArray(value)?value.slice(0,10):[]
    } catch {
        return []
    }
}
const history=ref(readHistory())
const hotWords=ref([])
const remember=(keyword)=>{
    const word=String(keyword||'').trim()
    if(!word) return
    history.value=[word,...history.value.filter(item=>item!==word)].slice(0,10)
    localStorage.setItem(historyKey,JSON.stringify(history.value))
}
const onSearch = (val) => {
    if(val==''){
        showNotify({type: 'danger', message:'您输入的搜索内容为空'});
        return
    }
    remember(val)
    router.push('/searchResult/'+encodeURIComponent(val))
}
const onClickButton = () =>{
    onSearch(content.value)
} 
const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
const searchWord=(word)=>{
    content.value=word
    onSearch(word)
}
const clearHistory=()=>{
    history.value=[]
    localStorage.removeItem(historyKey)
}
onMounted(async()=>{
    try {
        const result=await hotWordsService()
        hotWords.value=result.data || []
    } catch {
        hotWords.value=[]
    }
})
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
      <div class="search-panels">
        <div class="panel-head">
            <strong>搜索历史</strong>
            <span v-if="history.length" @click="clearHistory">清空</span>
        </div>
        <div class="tags" v-if="history.length">
            <van-button v-for="word in history" :key="word" size="mini" round plain @click="searchWord(word)">{{ word }}</van-button>
        </div>
        <div v-else class="empty-tip">暂无搜索历史</div>
        <div class="panel-head hot-title"><strong>热门搜索</strong></div>
        <div class="tags" v-if="hotWords.length">
            <van-button v-for="word in hotWords" :key="word.keyword" size="mini" round type="danger" plain
                @click="searchWord(word.keyword)">{{ word.keyword }}</van-button>
        </div>
        <div v-else class="empty-tip">暂无热词</div>
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
.search-panels{
    padding:12px;
}
.panel-head{
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin-bottom:10px;
}
.panel-head span{
    color:#1989fa;
    font-size:13px;
}
.hot-title{
    margin-top:24px;
}
.tags{
    display:flex;
    flex-wrap:wrap;
    gap:8px;
}
.empty-tip{
    color:#999;
    padding:12px 0;
}
</style>
