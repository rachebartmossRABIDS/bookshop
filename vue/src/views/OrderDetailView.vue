<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderDetailService } from '../apis/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loaded = ref(false)

const stateText = (state) => ({
  '0': '待付款', '1': '已付款', '2': '已发货', '3': '已收货',
  '4': '交易成功', '5': '已取消', '6': '已退款'
})[state] || state
const steps = computed(() => {
  const base = ['下单', '付款', '发货', '收货']
  if (order.value?.state === '5') base[3] = '已取消'
  if (order.value?.state === '6') base[3] = '已退款'
  return base
})
const active = computed(() => ({ '0': 0, '1': 1, '2': 2, '3': 3, '4': 3, '5': 3, '6': 3 }[order.value?.state] || 0))
onMounted(async () => {
  const result = await orderDetailService({ oid: route.params.oid })
  order.value = result.data
  loaded.value = true
})
</script>

<template>
  <div>
    <van-nav-bar title="订单详情" left-text="返回" left-arrow @click-left="router.back()" />
    <div v-if="order" class="detail">
      <van-steps :active="active" active-color="#ee0a24">
        <van-step v-for="item in steps" :key="item">{{ item }}</van-step>
      </van-steps>
      <van-cell-group inset title="订单信息">
        <van-cell title="订单编号" :value="'#' + order.oid" />
        <van-cell title="当前状态" :value="stateText(order.state)" />
        <van-cell title="下单时间" :value="order.time" />
        <van-cell title="订单金额" :value="'￥' + order.amount" />
        <van-cell title="购买数量" :value="order.num" />
      </van-cell-group>
      <van-cell-group inset title="图书" v-if="order.book">
        <van-cell :title="order.book.name" :label="order.book.author" :value="'￥' + order.book.price" />
      </van-cell-group>
      <van-cell-group inset title="收货信息">
        <van-cell title="收件人" :value="order.name" />
        <van-cell title="电话" :value="order.tel" />
        <van-cell title="地址" :label="order.addr" />
      </van-cell-group>
      <van-cell-group v-if="order.logisticsCompany || order.logisticsNo || order.shipTime" inset title="物流信息">
        <van-cell title="发货时间" :value="order.shipTime || '暂无'" />
        <van-cell title="物流公司" :value="order.logisticsCompany || '未填写'" />
        <van-cell title="物流单号" :value="order.logisticsNo || '未填写'" />
      </van-cell-group>
    </div>
    <van-empty v-if="loaded && !order" description="订单不存在" />
  </div>
</template>

<style scoped>
.detail { padding-bottom: 20px; }
.detail .van-cell-group { margin-top: 12px; }
</style>
