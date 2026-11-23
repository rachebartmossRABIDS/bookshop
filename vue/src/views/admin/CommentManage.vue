<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { allCommentListService, delCommentService, replyCommentService } from '../../apis/comment';

const comments=ref([])
const pageNo=ref(1)
const pageSize=ref(10)
const pages=ref(0)
const tip=ref('加载更多')
const replyTarget=ref(null)
const replyText=ref('')

const load=async()=>{
    const result=await allCommentListService({pageNo:pageNo.value, pageSize:pageSize.value})
    comments.value.push(...(result.data.list || []))
    pages.value=result.data.pages
}
load()
const refresh=async()=>{
    comments.value=[]
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
const remove=async(item)=>{
    try {
        await showConfirmDialog({title:'删除评论', message:'确定删除该评论吗？'})
    } catch { return }
    await delCommentService({cid:item.cid})
    showNotify({type:'success', message:'删除成功'})
    refresh()
}
const openReply=(item)=>{
    replyTarget.value=item
    replyText.value=item.reply || ''
}
const submitReply=async()=>{
    if(!replyText.value.trim()){
        showNotify({type:'danger', message:'回复内容不能为空'})
        return
    }
    await replyCommentService({cid:replyTarget.value.cid, reply:replyText.value})
    showNotify({type:'success', message:'回复成功'})
    replyTarget.value=null
    refresh()
}
</script>

<template>
    <div class="manage">
        <div class="item" v-for="item in comments" :key="item.cid">
            <div class="row">
                <span class="name">{{ item.user ? item.user.username : item.uid }}</span>
                <span class="meta">评论《{{ item.book ? item.book.name : item.bid }}》</span>
            </div>
            <div class="content">{{ item.content }}</div>
            <div v-if="item.reply" class="reply">商家回复：{{ item.reply }}</div>
            <div class="row">
                <span class="time">{{ item.time }}</span>
                <van-button size="mini" type="primary" plain @click="openReply(item)">{{ item.reply ? '修改回复' : '回复' }}</van-button>
                <van-button size="mini" type="danger" plain @click="remove(item)">删除</van-button>
            </div>
        </div>
        <van-empty v-if="comments.length===0" description="暂无评论" />
        <div class="load" v-if="comments.length>0" @click="getMore">{{tip}}</div>
        <van-popup :show="!!replyTarget" position="bottom" round @update:show="value => { if(!value) replyTarget=null }">
            <div class="reply-title">回复评论</div>
            <van-field v-model="replyText" type="textarea" rows="3" maxlength="1000" show-word-limit />
            <div class="reply-actions">
                <van-button block round type="primary" @click="submitReply">提交回复</van-button>
            </div>
        </van-popup>
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
    justify-content: space-between;
    gap: 6px;
}
.name{
    font-weight: bold;
}
.meta{
    color: #666;
    font-size: 13px;
}
.content{
    margin: 6px 0;
}
.time{
    color: #aaa;
    font-size: 12px;
}
.reply{
    padding:7px;
    margin:6px 0;
    border-radius:6px;
    background:#f7f8fa;
    color:#555;
}
.reply-title{
    text-align:center;
    font-weight:bold;
    padding:15px;
}
.reply-actions{
    padding:18px;
}
.load{
    text-align: center;
    margin: 10px;
}
</style>
