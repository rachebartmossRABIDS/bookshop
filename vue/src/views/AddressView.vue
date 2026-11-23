<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showNotify } from 'vant'
import {
  addressListService,
  addressRemoveService,
  addressSaveService,
  addressSetDefaultService
} from '../apis/address'

const router = useRouter()
const addresses = ref([])
const loaded = ref(false)
const showEditor = ref(false)
const form = ref({ aid: null, name: '', tel: '', addr: '', isDefault: 0 })

const load = async () => {
  const result = await addressListService()
  addresses.value = result.data || []
  loaded.value = true
}
const openEditor = (item) => {
  form.value = item ? {
    aid: item.aid,
    name: item.name,
    tel: item.tel,
    addr: item.addr,
    isDefault: item.isDefault
  } : { aid: null, name: '', tel: '', addr: '', isDefault: addresses.value.length === 0 ? 1 : 0 }
  showEditor.value = true
}
const save = async () => {
  if (!form.value.name.trim() || !form.value.tel.trim() || !form.value.addr.trim()) {
    showNotify({ type: 'danger', message: '请完整填写收件人、电话和地址' })
    return
  }
  await addressSaveService(form.value)
  showEditor.value = false
  showNotify({ type: 'success', message: '地址已保存' })
  load()
}
const remove = async (item) => {
  try {
    await showConfirmDialog({ title: '删除地址', message: '确定删除这条收货地址吗？' })
  } catch {
    return
  }
  await addressRemoveService({ aid: item.aid })
  showNotify({ type: 'success', message: '地址已删除' })
  load()
}
const setDefault = async (item) => {
  await addressSetDefaultService({ aid: item.aid })
  showNotify({ type: 'success', message: '已设为默认地址' })
  load()
}
onMounted(load)
</script>

<template>
  <div>
    <van-nav-bar title="收货地址" left-text="返回" left-arrow right-text="新增"
      @click-left="router.back()" @click-right="openEditor()" />
    <div class="list">
      <div v-for="item in addresses" :key="item.aid" class="address-card">
        <div class="line">
          <strong>{{ item.name }}</strong>
          <span>{{ item.tel }}</span>
          <van-tag v-if="item.isDefault === 1" type="danger">默认</van-tag>
        </div>
        <div class="addr">{{ item.addr }}</div>
        <div class="actions">
          <van-button v-if="item.isDefault !== 1" size="mini" plain type="primary"
            @click="setDefault(item)">设为默认</van-button>
          <van-button size="mini" plain @click="openEditor(item)">编辑</van-button>
          <van-button size="mini" plain type="danger" @click="remove(item)">删除</van-button>
        </div>
      </div>
      <van-empty v-if="loaded && addresses.length === 0" description="还没有收货地址">
        <van-button size="small" round type="primary" @click="openEditor()">新增地址</van-button>
      </van-empty>
    </div>
    <van-popup v-model:show="showEditor" position="bottom" round>
      <div class="editor-title">{{ form.aid ? '编辑地址' : '新增地址' }}</div>
      <van-cell-group inset>
        <van-field v-model="form.name" label="收件人" maxlength="100" />
        <van-field v-model="form.tel" label="手机号" maxlength="11" type="tel" />
        <van-field v-model="form.addr" label="地址" type="textarea" rows="2" maxlength="500" />
        <van-cell title="设为默认地址">
          <template #right-icon>
            <van-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
          </template>
        </van-cell>
      </van-cell-group>
      <div class="editor-actions">
        <van-button block round type="primary" @click="save">保存</van-button>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
.list { padding: 12px; }
.address-card { border: 1px solid #e5e5e5; border-radius: 10px; padding: 12px; margin-bottom: 10px; }
.line { display: flex; gap: 10px; align-items: center; }
.addr { margin: 10px 0; color: #555; line-height: 1.5; }
.actions { display: flex; justify-content: flex-end; gap: 8px; }
.editor-title { text-align: center; font-weight: bold; padding: 16px; }
.editor-actions { padding: 18px; }
</style>
