<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showNotify } from 'vant'
import { notificationListService, notificationReadService } from '../apis/notification'

const router = useRouter()
const items = ref([])
const pageNo = ref(1)
const pageSize = ref(10)
const pages = ref(0)
const loaded = ref(false)

const load = async () => {
  const result = await notificationListService({ pageNo: pageNo.value, pageSize: pageSize.value })
  items.value.push(...(result.data.list || []))
  pages.value = result.data.pages
  loaded.value = true
}
const read = async (item) => {
  if (item.isRead === 1) return
  await notificationReadService({ nid: item.nid })
  item.isRead = 1
  showNotify({ type: 'success', message: '已标记为已读' })
}
const more = () => {
  if (pageNo.value >= pages.value) return
  pageNo.value++
  load()
}
onMounted(load)
</script>

<template>
  <div>
    <van-nav-bar title="站内通知" left-text="返回" left-arrow @click-left="router.back()" />
    <div class="list">
      <div v-for="item in items" :key="item.nid" class="notice" :class="{ unread: item.isRead === 0 }"
        @click="read(item)">
        <div class="row">
          <strong>{{ item.title }}</strong>
          <van-tag v-if="item.isRead === 0" type="danger">未读</van-tag>
        </div>
        <div class="content">{{ item.content }}</div>
        <div class="time">{{ item.time }}</div>
      </div>
      <van-empty v-if="loaded && items.length === 0" description="暂无通知" />
      <div v-if="pageNo < pages" class="more" @click="more">加载更多</div>
    </div>
  </div>
</template>

<style scoped>
.list { padding: 12px; }
.notice { border: 1px solid #e5e5e5; border-radius: 10px; padding: 12px; margin-bottom: 10px; }
.notice.unread { border-left: 4px solid #ee0a24; background: #fff8f8; }
.row { display: flex; justify-content: space-between; align-items: center; }
.content { margin: 8px 0; color: #555; line-height: 1.55; }
.time { color: #999; font-size: 12px; }
.more { text-align: center; color: #1989fa; padding: 12px; }
</style>
