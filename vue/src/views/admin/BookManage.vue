<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { usePicStore } from '../../store/picStore'
import { searchService, bookDetailService, saveBookService, removeBookService, uploadBookImageService } from '../../apis/book';
import { kindListService } from '../../apis/kind';
import { shopListAllService } from '../../apis/shop';

const picIp=usePicStore().picIp
const keyword=ref('')
const books=ref([])
const pageNo=ref(1)
const pageSize=ref(8)
const pages=ref(0)
const tip=ref('加载更多')

const load=async()=>{
    const result=await searchService({name:keyword.value, pageNo:pageNo.value, pageSize:pageSize.value})
    books.value.push(...(result.data.list || []))
    pages.value=result.data.pages
}
load()
const refresh=async()=>{
    books.value=[]
    pageNo.value=1
    pages.value=0
    tip.value='加载更多'
    await load()
}
const search=()=>{
    refresh()
}
const getMore=()=>{
    pageNo.value++
    if(pageNo.value>pages.value){
        tip.value='没有更多了'
        return
    }
    load()
}

const kinds=ref([])
const shops=ref([])
const loadOptions=async()=>{
    const kindResult=await kindListService()
    kinds.value=kindResult.data || []
    const shopResult=await shopListAllService()
    shops.value=shopResult.data || []
}
loadOptions()
const kindName=(kid)=>{
    const kind=kinds.value.find(k=>k.kid===kid)
    return kind ? kind.content : ''
}
const shopName=(sid)=>{
    const shop=shops.value.find(s=>s.sid===sid)
    return shop ? shop.name : ''
}

const showForm=ref(false)
const emptyForm=()=>({bid:null, name:'', author:'', press:'', isbn:'', price:'', inventory:0, time:'', description:'', backlist:'', image:'', kid:null, sid:null, state:0})
const form=ref(emptyForm())
const showKindPicker=ref(false)
const showShopPicker=ref(false)
const kindColumns=()=>kinds.value.map(k=>({text:k.content, value:k.kid}))
const shopColumns=()=>shops.value.map(s=>({text:s.name, value:s.sid}))
const onKindConfirm=({selectedOptions})=>{
    form.value.kid=selectedOptions[0].value
    showKindPicker.value=false
}
const onShopConfirm=({selectedOptions})=>{
    form.value.sid=selectedOptions[0].value
    showShopPicker.value=false
}
const openAdd=()=>{
    form.value=emptyForm()
    showForm.value=true
}
const openEdit=async(item)=>{
    const result=await bookDetailService({bid:item.bid})
    const book=result.data || {}
    form.value={...emptyForm(), ...book, price:book.price==null?'':String(book.price), inventory:book.inventory==null?0:book.inventory, state:book.state==null?0:book.state, time:book.time || ''}
    showForm.value=true
}
const uploading=ref(false)
const onImageRead=async(file)=>{
    uploading.value=true
    try {
        const result=await uploadBookImageService(file.file)
        form.value.image=result.data
        showNotify({type:'success', message:'图片上传成功'})
    } finally {
        uploading.value=false
    }
}
const save=async()=>{
    if(!form.value.name.trim()){
        showNotify({type:'danger', message:'请填写图书名称'})
        return
    }
    if(form.value.price==='' || Number(form.value.price)<0 || isNaN(Number(form.value.price))){
        showNotify({type:'danger', message:'请填写正确的价格'})
        return
    }
    if(!form.value.kid){
        showNotify({type:'danger', message:'请选择所属分类'})
        return
    }
    if(!form.value.sid){
        showNotify({type:'danger', message:'请选择所属店铺'})
        return
    }
    if(form.value.time && !/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(form.value.time)){
        showNotify({type:'danger', message:'出版时间格式应为 yyyy-MM-dd HH:mm:ss'})
        return
    }
    const payload={...form.value}
    if(!payload.time){
        delete payload.time
    }
    await saveBookService(payload)
    showNotify({type:'success', message:'保存成功'})
    showForm.value=false
    refresh()
}
const remove=async(item)=>{
    try {
        await showConfirmDialog({title:'删除图书', message:`确定删除《${item.name}》吗？有订单/评论/收藏记录时将无法删除。`})
    } catch { return }
    await removeBookService({bid:item.bid})
    showNotify({type:'success', message:'删除成功'})
    refresh()
}
</script>

<template>
    <div class="manage">
        <van-search v-model="keyword" placeholder="按书名搜索" @search="search" @clear="search"/>
        <van-button size="small" type="primary" icon="plus" @click="openAdd">新增图书</van-button>
        <div class="item" v-for="item in books" :key="item.bid">
            <van-row>
                <van-col span="6" class="img">
                    <img :src="picIp+(item.image || 'default.png')">
                </van-col>
                <van-col span="18" class="info">
                    <div class="name">{{ item.name }}</div>
                    <div>{{ item.author }} ｜ ￥{{ item.price }}</div>
                    <div>库存：{{ item.inventory }} ｜ 销量：{{ item.volume }}</div>
                    <div>
                        <van-tag :type="item.state==0?'success':'danger'">{{ item.state==0?'上架':'下架' }}</van-tag>
                        <span class="meta">{{ kindName(item.kid) }} / {{ shopName(item.sid) }}</span>
                    </div>
                </van-col>
            </van-row>
            <div class="actions">
                <van-button size="mini" @click="openEdit(item)">编辑</van-button>
                <van-button size="mini" type="danger" plain @click="remove(item)">删除</van-button>
            </div>
        </div>
        <div class="load" @click="getMore">{{tip}}</div>

        <van-popup v-model:show="showForm" position="bottom" round class="form-popup">
            <div class="form-title">{{ form.bid?'编辑图书':'新增图书' }}</div>
            <van-field v-model="form.name" label="书名" placeholder="请输入书名" maxlength="255"/>
            <van-field v-model="form.author" label="作者" placeholder="请输入作者"/>
            <van-field v-model="form.press" label="出版社" placeholder="请输入出版社"/>
            <van-field v-model="form.isbn" label="ISBN" placeholder="请输入 ISBN"/>
            <van-field v-model="form.price" label="价格" type="number" placeholder="如 35.00"/>
            <van-field v-model.number="form.inventory" label="库存" type="digit" placeholder="请输入库存"/>
            <van-field v-model="form.time" label="出版时间" placeholder="yyyy-MM-dd HH:mm:ss"/>
            <van-field :model-value="form.kid ? kindName(form.kid) : ''" label="所属分类" readonly is-link placeholder="请选择分类" @click="showKindPicker=true"/>
            <van-field :model-value="form.sid ? shopName(form.sid) : ''" label="所属店铺" readonly is-link placeholder="请选择店铺" @click="showShopPicker=true"/>
            <van-field v-model.number="form.state" label="状态" type="digit" placeholder="0 上架 / 1 下架"/>
            <van-field label="封面图片">
                <template #input>
                    <div class="cover">
                        <van-image v-if="form.image" width="60" height="80" :src="picIp+form.image"/>
                        <van-uploader :after-read="onImageRead" :disabled="uploading" accept="image/*">
                            <van-button size="small" icon="photograph" :loading="uploading">上传</van-button>
                        </van-uploader>
                    </div>
                </template>
            </van-field>
            <van-field v-model="form.description" label="简介" type="textarea" rows="2" autosize placeholder="请输入简介"/>
            <van-field v-model="form.backlist" label="目录" type="textarea" rows="2" autosize placeholder="请输入目录"/>
            <div style="margin:16px">
                <van-button round block type="primary" @click="save">保存</van-button>
            </div>
        </van-popup>
        <van-popup v-model:show="showKindPicker" position="bottom" round>
            <van-picker :columns="kindColumns()" @confirm="onKindConfirm" @cancel="showKindPicker=false"/>
        </van-popup>
        <van-popup v-model:show="showShopPicker" position="bottom" round>
            <van-picker :columns="shopColumns()" @confirm="onShopConfirm" @cancel="showShopPicker=false"/>
        </van-popup>
        <van-back-top right="5vw" bottom="5vh"/>
    </div>
</template>

<style scoped>
.manage{
    padding: 10px;
}
.item{
    border: 1px solid #ddd;
    margin-top: 8px;
}
.img img{
    width: 100%;
    height: 90px;
    object-fit: cover;
}
.info{
    padding-left: 8px;
    font-size: 13px;
}
.name{
    font-weight: bold;
    font-size: 15px;
}
.meta{
    color: #666;
    margin-left: 6px;
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
.form-title{
    text-align: center;
    font-weight: bold;
    padding: 12px 0;
}
.form-popup{
    max-height: 85%;
    overflow-y: auto;
}
.cover{
    display: flex;
    align-items: center;
    gap: 10px;
}
</style>