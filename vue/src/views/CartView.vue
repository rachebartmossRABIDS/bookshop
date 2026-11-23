<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { showNotify, showConfirmDialog } from 'vant';
import { useUserStore } from '../store/userStore'
import { usePicStore } from '../store/picStore'
import { cartListService, cartUpdateNumService, cartRemoveService, cartCheckoutService } from '../apis/cart';
import { addressListService } from '../apis/address';

const router=useRouter();
const goBack=()=>{
    router.go(-1)
}
const userStore=useUserStore();
const picIp=usePicStore().picIp

const items=ref([])
const loaded=ref(false)
const load=async()=>{
    const result=await cartListService()
    items.value=result.data || []
    loaded.value=true
}
load()
const refresh=async()=>{
    loaded.value=false
    await load()
}

// 失效条目：已下架或库存不足（结算前需移除）
const isInvalid=(item)=>{
    if(!item.book) return true
    if(item.book.state!=0) return true
    return Number(item.book.inventory) < item.num
}
const invalidReason=(item)=>{
    if(!item.book) return '图书不存在'
    if(item.book.state!=0) return '已下架'
    if(Number(item.book.inventory) < item.num) return '库存不足'
    return ''
}
const hasInvalid=computed(()=>items.value.some(isInvalid))
const total=computed(()=>items.value.reduce((sum,item)=>{
    if(isInvalid(item) || !item.book || item.book.price==null) return sum
    return sum + Number(item.book.price)*item.num
}, 0))

const onNumChange=async(item,value)=>{
    const num=Number(value)
    if(!num || num<1) return
    try {
        await cartUpdateNumService({cid:item.cid, num})
        item.num=num
    } catch {
        refresh()
    }
}
const removeItem=async(item)=>{
    try {
        await showConfirmDialog({title:'移除商品', message:'确定从购物车移除该图书吗？'})
    } catch { return }
    await cartRemoveService({cid:item.cid})
    showNotify({type:'success', message:'已移除'})
    refresh()
}
const goDetail=(item)=>{
    if(item.bid){
        router.push('/detail/'+item.bid)
    }
}

const showCheckout=ref(false)
const receiver=ref(userStore.info.username || '')
const tel=ref(userStore.info.tel || '')
const addr=ref('')
const addresses=ref([])
const requestId=ref('')
const submitting=ref(false)
const newRequestId=()=>globalThis.crypto?.randomUUID?.() || (Date.now()+'-'+Math.random().toString(16).slice(2))
const applyAddress=(item)=>{
    receiver.value=item.name
    tel.value=item.tel
    addr.value=item.addr
}
const openCheckout=async()=>{
    if(items.value.length===0){
        showNotify({type:'danger', message:'购物车为空'})
        return
    }
    if(hasInvalid.value){
        showNotify({type:'danger', message:'请先移除已下架或库存不足的商品'})
        return
    }
    requestId.value=newRequestId()
    const result=await addressListService()
    addresses.value=result.data || []
    const selected=addresses.value.find(item=>item.isDefault===1) || addresses.value[0]
    if(selected) applyAddress(selected)
    showCheckout.value=true
}
const submitCheckout=async()=>{
    if(!receiver.value.trim() || !tel.value.trim() || !addr.value.trim()){
        showNotify({type:'danger', message:'请填写收件人、电话和地址'})
        return
    }
    submitting.value=true
    try {
        await cartCheckoutService({name:receiver.value, tel:tel.value, addr:addr.value, requestId:requestId.value})
        showCheckout.value=false
        showNotify({type:'success', message:'下单成功，请完成付款'})
        router.push('/order')
    } finally {
        submitting.value=false
    }
}
</script>

<template>
    <div class="cart">
        <van-nav-bar title="购物车" left-text="返回" left-arrow @click-left="goBack"/>
        <van-empty v-if="loaded && items.length===0" description="购物车还是空的">
            <van-button round type="primary" size="small" to="/home">去逛逛</van-button>
        </van-empty>
        <div class="list">
            <div class="item" v-for="item in items" :key="item.cid" :class="{invalid:isInvalid(item)}">
                <van-row>
                    <van-col span="6" class="img" @click="goDetail(item)">
                        <img :src="picIp+(item.book && item.book.image ? item.book.image : 'default.png')">
                    </van-col>
                    <van-col span="18" class="info">
                        <div class="name" @click="goDetail(item)">
                            {{ item.book ? item.book.name : '图书已删除' }}
                            <van-tag v-if="isInvalid(item)" type="danger">{{ invalidReason(item) }}</van-tag>
                        </div>
                        <div class="price" v-if="item.book">￥{{ item.book.price }}</div>
                        <div class="stock" v-if="item.book">剩余库存：{{ item.book.inventory }}</div>
                        <div class="ops">
                            <van-stepper :model-value="item.num" min="1" :max="999" :disabled="isInvalid(item)" @change="onNumChange(item,$event)"/>
                            <van-button size="mini" type="danger" plain @click="removeItem(item)">移除</van-button>
                        </div>
                    </van-col>
                </van-row>
            </div>
        </div>
        <van-submit-bar
            v-if="items.length>0"
            :price="Math.round(total*100)"
            :button-text="hasInvalid?'含失效商品':'结算'"
            :disabled="hasInvalid"
            @submit="openCheckout"
        />
        <van-popup v-model:show="showCheckout" position="bottom" round>
            <div class="form-title">填写收件信息</div>
            <div v-if="addresses.length" class="address-picker">
                <div class="picker-title">选择收货地址</div>
                <div v-for="item in addresses" :key="item.aid" class="address-option" @click="applyAddress(item)">
                    <strong>{{ item.name }}</strong>
                    <span>{{ item.tel }}</span>
                    <div>{{ item.addr }}</div>
                </div>
            </div>
            <van-field v-model="receiver" label="收件人" placeholder="请输入收件人" />
            <van-field v-model="tel" label="电话" placeholder="请输入 11 位手机号" maxlength="11" />
            <van-field v-model="addr" label="地址" placeholder="请输入收件地址" />
            <div style="margin: 16px;">
                <van-button round block type="primary" :loading="submitting" @click="submitCheckout">提交订单</van-button>
            </div>
        </van-popup>
    </div>
</template>

<style scoped>
.cart{
    padding-bottom: 60px;
}
.list{
    padding: 8px;
}
.item{
    border: 1px solid #ddd;
    margin-bottom: 8px;
}
.item.invalid{
    opacity: 0.65;
}
.img img{
    width: 100%;
    height: 90px;
    object-fit: cover;
}
.info{
    padding-left: 10px;
}
.name{
    font-weight: bold;
}
.price{
    color: #d10000;
}
.stock{
    color: #999;
    font-size: 12px;
}
.ops{
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 6px;
}
.form-title{
    text-align: center;
    font-weight: bold;
    padding: 12px 0;
}
.address-picker{
    max-height:170px;
    overflow:auto;
    padding:0 16px 8px;
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
