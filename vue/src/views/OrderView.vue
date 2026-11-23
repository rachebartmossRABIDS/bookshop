<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { useRouter } from 'vue-router';
import {useUserStore} from '../store/userStore'
import { usePicStore } from '../store/picStore'
import { userOrderListService, payOrderService, cancelOrderService, confirmOrderService, delOrderService, refundOrderService } from '../apis/order';

const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
const userStore=useUserStore();
const picIp=usePicStore().picIp
const user=ref({
    username:'',
    image:'default.png'
})
const orders=ref([])
const pageNo=ref(1);
const pageSize=ref(8);
const pages=ref(0)
const tip=ref('加载更多')
const orderList=async()=>{
    if(!userStore.info.uid){
        return
    }
    let result=await userOrderListService({
        uid:userStore.info.uid,
        pageNo:pageNo.value,
        pageSize:pageSize.value
    })
    orders.value.push(...(result.data.list || []))
    pages.value=result.data.pages
}
orderList()
const refresh=async()=>{
    orders.value=[]
    pageNo.value=1
    pages.value=0
    tip.value='加载更多'
    await orderList()
}
const goDetail=(item)=>{
    router.push('/order/detail/'+item.oid)
}
const stateText=(state)=>{
    const map={'0':'待付款','1':'已付款','2':'已发货','3':'已收货','4':'交易成功','5':'已取消','6':'已退款'}
    return map[state] || state || '待处理'
}
const isTerminal=(state)=>['3','4','5','6'].includes(state)
const payOrder=async(oid)=>{
    await payOrderService({oid})
    showNotify({type:'success', message:'付款成功'})
    refresh()
}
const cancelOrder=async(oid)=>{
    try {
        await showConfirmDialog({title:'取消订单', message:'确定取消该订单吗？库存将退回，已付款金额原路退回（模拟）。'})
    } catch { return }
    await cancelOrderService({oid})
    showNotify({type:'success', message:'订单已取消'})
    refresh()
}
const confirmOrder=async(oid)=>{
    await confirmOrderService({oid})
    showNotify({type:'success', message:'已确认收货'})
    refresh()
}
const refundOrder=async(oid)=>{
    try {
        await showConfirmDialog({title:'申请退款', message:'仅已付款且未发货的订单可退款，确定继续吗？'})
    } catch { return }
    await refundOrderService({oid})
    showNotify({type:'success', message:'退款成功，库存与销量已恢复'})
    refresh()
}
const delOrder=async(oid)=>{
    try {
        await showConfirmDialog({title:'删除订单', message:'确定删除该订单记录吗？'})
    } catch { return }
    await delOrderService({oid})
    showNotify({type:'success', message:'删除成功'})
    refresh()
}
const getMore=()=>{
    pageNo.value++;
    if(pageNo.value>pages.value){
        tip.value='没有更多了'
        return
    }
    orderList()
}
</script>

<template>
  <div>
    <van-nav-bar title="我的订单"  left-text="返回"
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
            <div class="orders" v-for="(item) in orders" :key="item.oid">
                <van-row @click="goDetail(item)" >
                    <van-col span="16" class="flex-column">
                        <div>订单数量：{{ item.num }}</div>
                        <div class="amount">订单金额：￥{{ item.amount }}</div>
                        <div>收件地址：{{ item.addr }}</div>
                        <div>订单状态：{{ stateText(item.state) }}</div>
                        <div class="time">{{ item.time }}</div>
                    </van-col>
                    <van-col span="8" class="img"><img :src="picIp+(item.book ? item.book.image : '')"></van-col>
                </van-row>
                <div class="actions">
                    <van-button v-if="item.state=='0'" size="small" type="primary" @click.stop="payOrder(item.oid)">付款</van-button>
                    <van-button v-if="item.state=='0'||item.state=='1'" size="small" @click.stop="cancelOrder(item.oid)">取消订单</van-button>
                    <van-button v-if="item.state=='1'" size="small" type="warning" @click.stop="refundOrder(item.oid)">申请退款</van-button>
                    <van-button v-if="item.state=='2'" size="small" type="primary" @click.stop="confirmOrder(item.oid)">确认收货</van-button>
                    <van-button v-if="isTerminal(item.state)" size="small" type="danger" plain @click.stop="delOrder(item.oid)">删除</van-button>
                </div>
            </div>

            <div class="load" @click="getMore">
            {{tip}}
        </div>
        <van-back-top right="5vw" bottom="5vh"/>
        </div>
    </div>
  </div>
</template>

<style scoped>
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
.amount{
    color: #d10000;
}
.img img{
    width: 100%;
    height: 80px;
}
.orders{
    border: 1px solid #ddd;
    margin-bottom: 8px;
}
.actions{
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding: 6px 8px;
    border-top: 1px dashed #eee;
}
.load{
    text-align: center;
    margin: 10px;
}
</style>
