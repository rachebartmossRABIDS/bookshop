<script setup>
import { computed, onMounted, ref } from 'vue'
import { adminStatsService } from '../../apis/admin'

const stats = ref({})
const loaded = ref(false)
const maxTrend = computed(() => Math.max(1, ...(stats.value.sevenDayTrend || []).map(item => Number(item.orderCount || 0))))
const maxVolume = computed(() => Math.max(1, ...(stats.value.topBooks || []).map(item => Number(item.volume || 0))))
onMounted(async () => {
  const result = await adminStatsService()
  stats.value = result.data || {}
  loaded.value = true
})
</script>

<template>
  <div class="overview">
    <div class="cards">
      <div class="card"><span>今日订单</span><strong>{{ stats.todayOrders || 0 }}</strong></div>
      <div class="card"><span>今日销售额</span><strong>￥{{ stats.todaySales || 0 }}</strong></div>
      <div class="card"><span>用户总数</span><strong>{{ stats.userCount || 0 }}</strong></div>
      <div class="card"><span>图书总数</span><strong>{{ stats.bookCount || 0 }}</strong></div>
    </div>
    <div class="section">
      <h3>近 7 日订单趋势</h3>
      <div v-for="row in stats.sevenDayTrend || []" :key="row.day" class="bar-row">
        <span>{{ row.day }}</span>
        <div class="bar-track"><div class="bar" :style="{width: (Number(row.orderCount || 0) / maxTrend * 100) + '%'}" /></div>
        <b>{{ row.orderCount || 0 }}</b>
      </div>
      <van-empty v-if="loaded && !(stats.sevenDayTrend || []).length" description="暂无订单趋势" image-size="70" />
    </div>
    <div class="section">
      <h3>销量 Top 5</h3>
      <div v-for="book in stats.topBooks || []" :key="book.bid" class="bar-row">
        <span class="book-name">{{ book.name }}</span>
        <div class="bar-track"><div class="bar hot" :style="{width: (Number(book.volume || 0) / maxVolume * 100) + '%'}" /></div>
        <b>{{ book.volume || 0 }}</b>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overview { padding: 12px; background: #f7f8fa; min-height: 480px; }
.cards { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.card { background: white; border-radius: 10px; padding: 14px; display: flex; flex-direction: column; gap: 8px; }
.card span { color: #888; font-size: 13px; }
.card strong { color: #ee0a24; font-size: 22px; }
.section { background: white; border-radius: 10px; padding: 12px; margin-top: 12px; }
.section h3 { margin: 0 0 12px; font-size: 15px; }
.bar-row { display: grid; grid-template-columns: 78px 1fr 35px; align-items: center; gap: 8px; margin: 9px 0; font-size: 12px; }
.book-name { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.bar-track { height: 8px; border-radius: 8px; background: #eef1f5; overflow: hidden; }
.bar { height: 100%; min-width: 2px; border-radius: 8px; background: #1989fa; }
.bar.hot { background: #ee0a24; }
</style>
