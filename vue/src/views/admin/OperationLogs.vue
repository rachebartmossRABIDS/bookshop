<script setup>
import { onMounted, ref } from 'vue'
import { adminLogsService } from '../../apis/admin'

const logs = ref([])
const pageNo = ref(1)
const pageSize = ref(20)
const pages = ref(0)
const loaded = ref(false)
const load = async () => {
  const result = await adminLogsService({ pageNo: pageNo.value, pageSize: pageSize.value })
  logs.value.push(...(result.data.list || []))
  pages.value = result.data.pages
  loaded.value = true
}
const more = () => {
  if (pageNo.value >= pages.value) return
  pageNo.value++
  load()
}
onMounted(load)
</script>

<template>
  <div class="logs">
    <div v-for="item in logs" :key="item.id" class="log-item">
      <div class="row"><strong>{{ item.action }}</strong><span>#{{ item.id }}</span></div>
      <div class="detail">{{ item.detail }}</div>
      <div class="meta">操作人 UID：{{ item.uid }} · {{ item.time }}</div>
    </div>
    <van-empty v-if="loaded && logs.length === 0" description="暂无操作日志" />
    <div v-if="pageNo < pages" class="more" @click="more">加载更多</div>
  </div>
</template>

<style scoped>
.logs { padding: 12px; }
.log-item { border: 1px solid #e5e5e5; border-radius: 8px; padding: 10px; margin-bottom: 8px; }
.row { display: flex; justify-content: space-between; }
.row span, .meta { color: #999; font-size: 12px; }
.detail { margin: 7px 0; color: #555; }
.more { color: #1989fa; text-align: center; padding: 12px; }
</style>
