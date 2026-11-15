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
import { bookCommentListService,addCommentService} from '../apis/comment';
const comments=ref([])
const commentList=async()=>{
    let result=await bookCommentListService({bid})
    comments.value=result.data
}
commentList();
import { bookOrderListService,addOrderService} from '../apis/order';
const orders=ref([])
const orderList=async()=>{
    let result=await bookOrderListService({bid})
    orders.value=result.data
}
orderList();
const content=ref('')
import {useUserStore} from '../store/userStore'
import { showNotify } from 'vant';
const userStore=useUserStore();
const showOrder=ref(false)
const num=ref(1)
const receiver=ref('')
const tel=ref('')
const addr=ref('')
const openOrder=()=>{
    if(!userStore.info.uid){
        showNotify({type:'danger', message:'请先登录'})
        router.push('/login')
        return
    }
    showOrder.value=true
}
const sendOrder=async()=>{
    if(!receiver.value || !tel.value || !addr.value){
        showNotify({type:'danger', message:'请填写收件人、电话和地址'})
        return
    }
    await addOrderService({
        bid:bid,
        uid:userStore.info.uid,
        num:num.value || 1,
        name:receiver.value,
        tel:tel.value,
        addr:addr.value,
        state:'1'
    })
    showNotify({type:'success', message:'下单成功'})
    showOrder.value=false
    orderList()
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
        uid:userStore.info.uid,
        content:content.value
    })
    content.value=''
    showNotify({type:'success', message:'评论成功'})
    commentList()
}
const goSDetail=(book)=>{
    router.push('/sdetail/'+book.sid);
}
const activeNames = ref(['1']);
const user=ref({});
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
        <img :src="picIp+book.image">   
        <div class="price">书籍价格：￥{{ book.price }}</div>
        <div>书籍名称：{{book.name}}</div>
        <div>作者：{{book.author}}</div>
        <div :key="book.id " @click="goSDetail(book)" style="color: blue;">店铺信息<van-icon name="arrow" /></div>
        <van-submit-bar button-text="购买书籍" @submit="openOrder" />
        <van-popup v-model:show="showOrder" position="bottom" round>
            <van-form @submit="sendOrder">
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
    </van-space>   
        <div class="title">用户评价信息</div>
        <div class="comment">
            <van-row v-for="item in comments" :key="item.cid">
                    <van-col span="3" >
                        <van-image
                        round
                        width="2rem"
                        height="2rem"
                        :src="picIp+(item.user ? item.user.image : 'default.png')"
                        />
                    </van-col>
                    <van-col span="21" >
                        <div class="name">{{ item.user ? item.user.username : '' }}</div>
                    <div>
                        {{ item.content }}
                    </div>
                    <div class="time">
                        {{ item.time }}
                    </div>
                    </van-col>
            </van-row>
        </div>
        <van-field v-model="content" rows="2" autosize type="textarea" placeholder="写下你的评价" />
        <div style="margin: 16px;">
            <van-button round block type="primary" @click="sendComment">发表评论</van-button>
        </div>
        <router-view/>
    </div>
</template>


<style scoped>
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