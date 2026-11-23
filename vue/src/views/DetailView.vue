<script setup>
import { useRoute,useRouter} from 'vue-router'
const route = useRoute()
const book=ref({})
const bid=route.params.bid
import { bookDetailService} from '../apis/book'
const bookDetail=async()=>{
    let result=await bookDetailService({bid:bid})
    book.value=result.data;
}
bookDetail();
const router = useRouter()
const goBack=()=>{
    router.go(-1)
}
import { ref } from 'vue';
import { usePicStore } from '../store/picStore'
const picIp=usePicStore().picIp
import { bookCommentListService,addCommentService,likeCommentService,unlikeCommentService} from '../apis/comment';
import { addressListService } from '../apis/address';
const comments=ref([])
const commentList=async()=>{
    let result=await bookCommentListService({bid})
    comments.value=result.data
}
commentList();
import { addOrderService} from '../apis/order';
import { cartAddService } from '../apis/cart';
const content=ref('')
const score=ref(5)
import {useUserStore} from '../store/userStore'
import { showNotify } from 'vant';
const userStore=useUserStore();
import { favoriteCheckService, favoriteSaveService, favoriteRemoveService } from '../apis/favorite';
const isFav=ref(false)
const checkFavorite=async()=>{
    if(!userStore.info.uid){
        return
    }
    const result=await favoriteCheckService({bid})
    isFav.value=!!result.data
}
checkFavorite()
const toggleFavorite=async()=>{
    if(!userStore.info.uid){
        showNotify({type:'danger', message:'请先登录'})
        router.push('/login')
        return
    }
    if(isFav.value){
        await favoriteRemoveService({bid})
        isFav.value=false
        showNotify({type:'success', message:'已取消收藏'})
    }else{
        await favoriteSaveService({bid})
        isFav.value=true
        showNotify({type:'success', message:'收藏成功'})
    }
}
const showOrder=ref(false)
const num=ref(1)
const receiver=ref('')
const tel=ref('')
const addr=ref('')
const addresses=ref([])
const requestId=ref('')
const newRequestId=()=>globalThis.crypto?.randomUUID?.() || (Date.now()+'-'+Math.random().toString(16).slice(2))
const applyAddress=(item)=>{
    receiver.value=item.name
    tel.value=item.tel
    addr.value=item.addr
}
const openOrder=async()=>{
    if(!userStore.info.uid){
        showNotify({type:'danger', message:'请先登录'})
        router.push('/login')
        return
    }
    if(Number(book.value.inventory)<=0){
        showNotify({type:'danger', message:'该图书库存不足，暂时无法购买'})
        return
    }
    requestId.value=newRequestId()
    const result=await addressListService()
    addresses.value=result.data || []
    const selected=addresses.value.find(item=>item.isDefault===1) || addresses.value[0]
    if(selected) applyAddress(selected)
    showOrder.value=true
}
const sendOrder=async()=>{
    if(!receiver.value || !tel.value || !addr.value){
        showNotify({type:'danger', message:'请填写收件人、电话和地址'})
        return
    }
    const count=Number(num.value)||1
    if(count<1){
        showNotify({type:'danger', message:'购买数量至少为 1'})
        return
    }
    if(count>Number(book.value.inventory)){
        showNotify({type:'danger', message:'购买数量超出剩余库存'})
        return
    }
    await addOrderService({
        bid:bid,
        num:count,
        name:receiver.value,
        tel:tel.value,
        addr:addr.value,
        requestId:requestId.value
    })
    showNotify({type:'success', message:'下单成功，请完成付款'})
    showOrder.value=false
    bookDetail()
    router.push('/order')
}
const sendComment=async()=>{
    if(!userStore.info.uid){
        showNotify({type:'danger', message:'请先登录'})
        router.push('/login')
        return
    }
    if(!content.value.trim()){
        showNotify({type:'danger', message:'评论内容不能为空'})
        return
    }
    await addCommentService({
        bid:bid,
        content:content.value,
        score:score.value
    })
    content.value=''
    showNotify({type:'success', message:'评论成功'})
    commentList()
}
const toggleLike=async(item)=>{
    if(!userStore.info.uid){
        showNotify({type:'danger', message:'请先登录'})
        router.push('/login')
        return
    }
    if(item.liked){
        await unlikeCommentService({cid:item.cid})
        item.liked=false
        item.likeCount=Math.max(0,Number(item.likeCount||0)-1)
    }else{
        await likeCommentService({cid:item.cid})
        item.liked=true
        item.likeCount=Number(item.likeCount||0)+1
    }
}
const goSDetail=(book)=>{
    router.push('/sdetail/'+book.sid);
}
const addToCart=async()=>{
    if(!userStore.info.uid){
        showNotify({type:'danger', message:'请先登录'})
        router.push('/login')
        return
    }
    if(Number(book.value.inventory)<=0){
        showNotify({type:'danger', message:'该图书库存不足，暂时无法购买'})
        return
    }
    await cartAddService({bid, num:1})
    showNotify({type:'success', message:'已加入购物车'})
}
const activeNames = ref(['1']);
const user=ref({});
</script>


<template>
    <div class="page">
        <van-nav-bar
        title="书籍详情"
        left-text="返回"
        left-arrow
        @click-left="goBack"
        />
        <div class="cover-card">
            <img :src="picIp+(book.image||'default.png')">
        </div>
        <div class="info-card">
            <div class="book-name">{{ book.name }}</div>
            <div class="book-author">作者：{{ book.author }}</div>
            <div class="price-row">
                <div class="price">￥{{ book.price }}</div>
                <div class="stock">剩余库存：{{ book.inventory }}</div>
            </div>
            <div class="shop-row">
                <van-button size="small" icon="shop-o" plain type="primary" @click="goSDetail(book)">进入店铺</van-button>
                <van-button size="small" :icon="isFav?'star':'star-o'" :type="isFav?'warning':'default'" plain @click="toggleFavorite">{{ isFav?'已收藏':'收藏' }}</van-button>
            </div>
            <div class="buy-row">
                <van-button class="buy-btn" type="warning" @click="addToCart">加入购物车</van-button>
                <van-button class="buy-btn" type="danger" @click="openOrder">立即购买</van-button>
            </div>
        </div>
        <van-popup v-model:show="showOrder" position="bottom" round>
            <van-form @submit="sendOrder">
                <div v-if="addresses.length" class="address-picker">
                    <div class="picker-title">选择收货地址</div>
                    <div v-for="item in addresses" :key="item.aid" class="address-option"
                        @click="applyAddress(item)">
                        <strong>{{ item.name }}</strong>
                        <span>{{ item.tel }}</span>
                        <div>{{ item.addr }}</div>
                    </div>
                </div>
                <van-field v-model.number="num" type="digit" label="数量" />
                <van-field v-model="receiver" label="收件人" placeholder="请输入收件人" />
                <van-field v-model="tel" label="电话" placeholder="请输入电话" />
                <van-field v-model="addr" label="地址" placeholder="请输入收件地址" />
                <div style="margin: 16px;">
                    <van-button round block type="primary" native-type="submit">提交订单</van-button>
                </div>
            </van-form>
        </van-popup>
        <van-collapse class="collapse" v-model="activeNames" is-link="false">
        <van-collapse-item title="书籍详细信息" >
        <div class="list">
        <div>书籍内容简介：{{ book.description }}</div>
        <div>出版社：{{ book.press }}</div>
        <div>出版时间：{{ book.time }}</div>
        <div>ISBN号:{{ book.isbn}}</div>
        <div>书籍目录：{{ book.backlist}}</div>
        <div>库存：{{ book.inventory}}</div>
        <div>销量：{{ book.volume}}</div>
       </div>
    </van-collapse-item>
  </van-collapse>
        <div class="title">用户评价信息</div>
        <div class="comment">
            <van-row v-for="item in comments" :key="item.cid">
                    <van-col span="3" >
                        <van-image
                        round
                        width="2rem"
                        height="2rem"
                        :src="picIp+(item.user && item.user.image ? item.user.image : 'default.png')"
                        />
                    </van-col>
                    <van-col span="21" >
                        <div class="name">{{ item.user ? item.user.username : '' }}</div>
                        <div class="comment-meta">
                            <van-rate :model-value="Number(item.score)||5" readonly size="12" />
                            <van-tag v-if="item.oid" type="success">已购</van-tag>
                        </div>
                    <div>
                        {{ item.content }}
                    </div>
                    <div v-if="item.reply" class="reply">
                        <strong>商家回复：</strong>{{ item.reply }}
                    </div>
                    <van-button size="mini" plain :icon="item.liked ? 'good-job' : 'good-job-o'"
                        @click.stop="toggleLike(item)">
                        有用 {{ item.likeCount || 0 }}
                    </van-button>
                    <div class="time">
                        {{ item.time }}
                    </div>
                    </van-col>
            </van-row>
        </div>
        <div class="rating-row">
            <span>评分</span>
            <van-rate v-model="score" />
        </div>
        <van-field v-model="content" rows="2" autosize type="textarea" placeholder="写下你的评价" />
        <div style="margin: 16px;">
            <van-button round block type="primary" @click="sendComment">发表评论</van-button>
        </div>
        <router-view/>
    </div>
</template>


<style scoped>
.page{
    padding-bottom: 16px;
}
.cover-card{
    margin: 10px 12px;
    border-radius: 12px;
    overflow: hidden;
    background: #f2f3f5;
}
.cover-card img{
    width: 100%;
    height: 240px;
    object-fit: contain;
    display: block;
}
.info-card{
    margin: 0 12px 12px;
    padding: 14px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.book-name{
    font-size: 20px;
    font-weight: bold;
    color: #323233;
}
.book-author{
    margin-top: 6px;
    color: #666;
    font-size: 14px;
}
.list{
    color: black;
}
.price{
    height: 25px;
    font-size: 25px;
    color: #d10000;
    line-height: 25px;
    font-weight: 400;
}
.price-row{
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 10px;
}
.stock{
    color: #666;
    font-size: 13px;
}
.shop-row{
    display: flex;
    gap: 10px;
    margin-top: 12px;
}
.buy-row{
    display: flex;
    gap: 10px;
    margin-top: 14px;
}
.buy-row .buy-btn{
    flex: 1;
    border-radius: 20px;
}
.title{
    font-size: 20px;
    font-weight: bold;
    margin: 10px;
}
.comment .van-row{
    margin: 5px 0;
}
.comment .name{
    font-size: 15px;
    font-weight: bold;
    margin: 5px 0;
}
.comment-meta{
    display:flex;
    align-items:center;
    gap:8px;
    margin:4px 0;
}
.reply{
    margin:8px 0;
    padding:8px;
    border-radius:6px;
    background:#f7f8fa;
    color:#555;
}
.rating-row{
    display:flex;
    align-items:center;
    gap:12px;
    margin:0 16px;
}
.address-picker{
    max-height:180px;
    overflow:auto;
    padding:8px 16px;
}
.picker-title{
    font-weight:bold;
    margin-bottom:6px;
}
.address-option{
    border:1px solid #eee;
    border-radius:8px;
    padding:8px;
    margin-bottom:6px;
    font-size:13px;
}
.address-option span{
    margin-left:8px;
    color:#777;
}
</style>
