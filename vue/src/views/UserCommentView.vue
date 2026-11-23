<script setup>
import { ref } from 'vue';
const user=ref({
    username:'',
    image:'default.png'
})
import { showNotify } from 'vant';
import { useRouter } from 'vue-router';
const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
import {useUserStore} from '../store/userStore'
const userStore=useUserStore();
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
import { userCommentListService,delCommentService } from '../apis/comment';
const comments=ref([])
const loaded=ref(false)
const pageNo=ref(1);
const pageSize=ref(8);
const pages=ref(0)
const commentList=async()=>{
    if(!userStore.info.uid){
        return
    }
    let result=await userCommentListService({
        uid:userStore.info.uid,
        pageNo:pageNo.value,
        pageSize:pageSize.value
    })
    comments.value.push(...(result.data.list || []))
    pages.value=result.data.pages
    loaded.value=true
}
commentList()
const goDetail=(item)=>{
    const bid=item.book && item.book.bid ? item.book.bid : item.bid
    if(bid){
        router.push('/detail/'+bid)
    }
}
const delComment=async(cid)=>{
    await delCommentService({cid})
    showNotify({type: 'success', message:'删除成功'});
    router.go(0)
}
const tip=ref('加载更多')
const getMore=()=>{
    pageNo.value++;
    if(pageNo.value>pages.value){
        tip.value='没有更多了'
        return
    }
    commentList()
}
</script>

<template>
  <div>
    <van-nav-bar title="我的评论"  left-text="返回"
        left-arrow
        @click-left="goBack"/>
        <div class="user">
        <div>
            <van-image
            round
            width="5rem"
            height="5rem"
            :src="userStore.info.image?picIp+userStore.info.image:picIp+user.image"
            />
        </div>
        <div class="name flex-column-center" v-if="userStore.info.username">{{userStore.info.username}}</div>
        <div class="name flex-column-center" v-else>
            <router-link to="/login">您未登录，点击此处进行登录</router-link>
        </div>
    </div>
    <div>
        <div class="list"> 
            <div class="comments" v-for="(item) in comments" :key="item.cid">
                <van-swipe-cell>
            <van-row @click="goDetail(item)" >
                <van-col span="16" class="flex-column">
                    <div>{{ item.book ? item.book.name : '图书已删除' }}</div>
                    <div class="content">{{ item.content }}</div>
                    <div class="time">{{ item.time }}</div>
                </van-col>
                <van-col span="8" class="img"><img :src="picIp+(item.book ? item.book.image : '')"></van-col>
            </van-row>
            <template #right>
    <van-button square text="删除" type="danger" class="delete-button" @click="delComment(item.cid)"/>
  </template>
            </van-swipe-cell>
            </div>
            <van-empty v-if="loaded && comments.length===0" description="还没有发表过评论" />
            <div class="load" v-if="comments.length>0" @click="getMore">
            {{tip}}
        </div>
        <van-back-top right="5vw" bottom="5vh"/>
        </div>
    </div>
  </div>
</template>

<style scoped>
 .delete-button {
    height: 100%;
  }
.user{
    display:flex;
}
.user .name{
    margin-left: 10px;
}
.list .van-col{
    display: flex;
}
.list .van-row{
    margin: 5px 0;
    border: 0.5px solid #eee;
}
.list .time{
    color: #aaa;
    font-size: 13px;
}
.img img{
    width: 100%;
    height: 90px;
    object-fit: contain;
    background: #f2f3f5;
}
.comments{
    border: 1px solid #ddd;
}
.load{
    text-align: center;
    margin: 10px;
}
</style>