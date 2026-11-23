<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { usePicStore } from '../../store/picStore'
import { shopListAllService, saveShopService, removeShopService } from '../../apis/shop';
import { kindListService } from '../../apis/kind';

const picIp=usePicStore().picIp
const shops=ref([])
const kinds=ref([])
const load=async()=>{
    const result=await shopListAllService()
    shops.value=result.data || []
    const kindResult=await kindListService()
    kinds.value=kindResult.data || []
}
load()

const kindName=(kid)=>{
    const kind=kinds.value.find(k=>k.kid===kid)
    return kind ? kind.content : kid
}
const showForm=ref(false)
const emptyForm=()=>({sid:null, name:'', logo:'', picture:'', score:'', description:'', state:'0', kid:null})
const form=ref(emptyForm())
const showKindPicker=ref(false)
const kindColumns=()=>kinds.value.map(k=>({text:k.content, value:k.kid}))
const onKindConfirm=({selectedOptions})=>{
    form.value.kid=selectedOptions[0].value
    showKindPicker.value=false
}
const openAdd=()=>{
    form.value=emptyForm()
    showForm.value=true
}
const openEdit=(item)=>{
    form.value={...emptyForm(), ...item}
    showForm.value=true
}
const save=async()=>{
    if(!form.value.name.trim()){
        showNotify({type:'danger', message:'请填写店铺名称'})
        return
    }
    if(!form.value.kid){
        showNotify({type:'danger', message:'请选择所属分类'})
        return
    }
    await saveShopService(form.value)
    showNotify({type:'success', message:'保存成功'})
    showForm.value=false
    load()
}
const remove=async(item)=>{
    try {
        await showConfirmDialog({title:'删除店铺', message:`确定删除店铺「${item.name}」吗？`})
    } catch { return }
    await removeShopService({sid:item.sid})
    showNotify({type:'success', message:'删除成功'})
    load()
}
</script>

<template>
    <div class="manage">
        <van-button size="small" type="primary" icon="plus" @click="openAdd">新增店铺</van-button>
        <div class="item" v-for="item in shops" :key="item.sid">
            <van-row>
                <van-col span="6" class="img">
                    <img :src="picIp+(item.logo || 'default.png')">
                </van-col>
                <van-col span="18" class="info">
                    <div class="name">{{ item.name }}</div>
                    <div>分类：{{ kindName(item.kid) }} ｜ 评分：{{ item.score || '-' }}</div>
                    <div>状态：{{ item.state=='0'?'营业中':'已关闭' }}</div>
                </van-col>
            </van-row>
            <div class="actions">
                <van-button size="mini" @click="openEdit(item)">编辑</van-button>
                <van-button size="mini" type="danger" plain @click="remove(item)">删除</van-button>
            </div>
        </div>
        <van-popup v-model:show="showForm" position="bottom" round class="form-popup">
            <div class="form-title">{{ form.sid?'编辑店铺':'新增店铺' }}</div>
            <van-field v-model="form.name" label="店铺名称" placeholder="请输入店铺名称" maxlength="255"/>
            <van-field v-model="form.score" label="评分" placeholder="如 4.8"/>
            <van-field v-model="form.logo" label="Logo文件名" placeholder="如 6.jpg"/>
            <van-field v-model="form.picture" label="图片文件名" placeholder="如 6.jpg"/>
            <van-field :model-value="form.kid ? kindName(form.kid) : ''" label="所属分类" readonly is-link placeholder="请选择分类" @click="showKindPicker=true"/>
            <van-field v-model="form.state" label="状态" placeholder="0 营业 / 1 关闭"/>
            <van-field v-model="form.description" label="店铺简介" type="textarea" rows="2" autosize placeholder="请输入简介"/>
            <div style="margin:16px">
                <van-button round block type="primary" @click="save">保存</van-button>
            </div>
        </van-popup>
        <van-popup v-model:show="showKindPicker" position="bottom" round>
            <van-picker :columns="kindColumns()" @confirm="onKindConfirm" @cancel="showKindPicker=false"/>
        </van-popup>
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
    height: 70px;
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
.actions{
    display: flex;
    justify-content: flex-end;
    gap: 6px;
    padding: 6px 8px;
    border-top: 1px dashed #eee;
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
</style>