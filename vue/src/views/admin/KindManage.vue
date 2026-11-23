<script setup>
import { ref } from 'vue';
import { showNotify, showConfirmDialog } from 'vant';
import { kindListService, saveKindService, removeKindService } from '../../apis/kind';

const kinds=ref([])
const load=async()=>{
    const result=await kindListService()
    kinds.value=result.data || []
}
load()

const showForm=ref(false)
const form=ref({kid:null, content:''})
const openAdd=()=>{
    form.value={kid:null, content:''}
    showForm.value=true
}
const openEdit=(item)=>{
    form.value={kid:item.kid, content:item.content}
    showForm.value=true
}
const save=async()=>{
    if(!form.value.content.trim()){
        showNotify({type:'danger', message:'请填写分类名称'})
        return
    }
    await saveKindService({kid:form.value.kid, content:form.value.content.trim()})
    showNotify({type:'success', message:'保存成功'})
    showForm.value=false
    load()
}
const remove=async(item)=>{
    try {
        await showConfirmDialog({title:'删除分类', message:`确定删除分类「${item.content}」吗？`})
    } catch { return }
    await removeKindService({kid:item.kid})
    showNotify({type:'success', message:'删除成功'})
    load()
}
</script>

<template>
    <div class="manage">
        <van-button size="small" type="primary" icon="plus" @click="openAdd">新增分类</van-button>
        <van-cell v-for="item in kinds" :key="item.kid" :title="item.content" :label="'编号：'+item.kid">
            <template #value>
                <van-button size="mini" @click="openEdit(item)">编辑</van-button>
                <van-button size="mini" type="danger" plain style="margin-left:6px" @click="remove(item)">删除</van-button>
            </template>
        </van-cell>
        <van-popup v-model:show="showForm" position="bottom" round>
            <div class="form-title">{{ form.kid?'编辑分类':'新增分类' }}</div>
            <van-field v-model="form.content" label="分类名称" placeholder="请输入分类名称" maxlength="255"/>
            <div style="margin:16px">
                <van-button round block type="primary" @click="save">保存</van-button>
            </div>
        </van-popup>
    </div>
</template>

<style scoped>
.manage{
    padding: 10px;
}
.form-title{
    text-align: center;
    font-weight: bold;
    padding: 12px 0;
}
</style>