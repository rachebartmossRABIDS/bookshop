<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { usePicStore } from '../../store/picStore'
import { allOrderListService, shipOrderService, cancelOrderService, delOrderService } from '../../apis/order';

const picIp=usePicStore().picIp
const orders=ref([])
const pageNo=ref(1)
const pageSize=ref(8)
const pages=ref(0)
const tip=ref('加载更多')
const shipping=ref(null)
const logisticsCompany=ref('')
const logisticsNo=ref('')

const load=async()=>{
    const result=await allOrderListService({pageNo:pageNo.value, pageSize:pageSize.value})
    orders.value.push(...(result.data.list || []))
    pages.value=result.data.pages
}
load()
const refresh=async()=>{
    orders.value=[]
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
const stateText=(state)=>{
    const map={'0':'待付款','1':'已付款','2':'已发货','3':'已收货','4':'交易成功','5':'已取消'}
    return map[state] || state || '待处理'
}
const isTerminal=(state)=>['3','4','5'].includes(state)
const openShip=(item)=>{
    shipping.value=item
    logisticsCompany.value=item.logisticsCompany || ''
    logisticsNo.value=item.logisticsNo || ''
}
const ship=async()=>{
    await shipOrderService({oid:shipping.value.oid, logisticsCompany:logisticsCompany.value, logisticsNo:logisticsNo.value})
    showNotify({type:'success', message:'发货成功'})
    shipping.value=null
    refresh()
}
const cancel=async(item)=>{
    try {
        await showConfirmDialog({title:'取消订单', message:'确定取消该订单吗？库存将退回。'})
    } catch { return }
    await cancelOrderService({oid:item.oid})
    showNotify({type:'success', message:'订单已取消'})
    refresh()
}
const remove=async(item)=>{
    try {
        await showConfirmDialog({title:'删除订单', message:'确定删除该订单记录吗？'})
    } catch { return }
    await delOrderService({oid:item.oid})
    showNotify({type:'success', message:'删除成功'})
    refresh()
}
</script>

<template>
    <div class="manage">
        <div class="item" v-for="item in orders" :key="item.oid">
            <van-row>
                <van-col span="7" class="img">
                    <img :src="picIp+(item.book && item.book.image ? item.book.image : 'default.png')">
                </van-col>
                <van-col span="17" class="info">
                    <div class="name">#{{ item.oid }} {{ item.book ? item.book.name : '' }}</div>
                    <div>买家：{{ item.user ? item.user.username : item.uid }} ｜ 数量：{{ item.num }}</div>
                    <div class="amount">金额：￥{{ item.amount }} ｜ {{ stateText(item.state) }}</div>
                    <div class="meta">收件：{{ item.name }} {{ item.tel }}</div>
                    <div class="meta">{{ item.addr }}</div>
                    <div class="time">{{ item.time }}</div>
                </van-col>
            </van-row>
            <div class="actions">
                <van-button v-if="item.state=='1'" size="mini" type="primary" @click="openShip(item)">发货</van-button>
                <van-button v-if="item.state=='0'||item.state=='1'" size="mini" @click="cancel(item)">取消订单</van-button>
                <van-button v-if="isTerminal(item.state)" size="mini" type="danger" plain @click="remove(item)">删除</van-button>
            </div>
        </div>
        <div class="load" @click="getMore">{{tip}}</div>
        <van-back-top right="5vw" bottom="5vh"/>
        <van-popup :show="!!shipping" position="bottom" round @update:show="value => { if(!value) shipping=null }">
            <div class="ship-title">填写物流信息（可选）</div>
            <van-field v-model="logisticsCompany" label="物流公司" maxlength="100" placeholder="如顺丰速运" />
            <van-field v-model="logisticsNo" label="物流单号" maxlength="100" placeholder="请输入物流单号" />
            <div class="ship-actions">
                <van-button block round type="primary" @click="ship">确认发货</van-button>
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
}
.img img{
    width: 100%;
    height: 90px;
    object-fit: contain;
    background: #f2f3f5;
}
.info{
    padding-left: 8px;
    font-size: 13px;
}
.name{
    font-weight: bold;
}
.amount{
    color: #d10000;
}
.meta{
    color: #666;
}
.time{
    color: #aaa;
    font-size: 12px;
}
.actions{
    display: flex;
    justify-content: flex-end;
    gap: 6px;
    padding: 6px 8px;
    border-top: 1px dashed #eee;
}
.load{
    text-align: center;
    margin: 10px;
}
.ship-title{
    text-align:center;
    font-weight:bold;
    padding:15px;
}
.ship-actions{
    padding:18px;
}
</style>
