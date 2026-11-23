import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css'
import {createPinia} from 'pinia'
import {createPersistedState} from 'pinia-persistedstate-plugin'
import {usePicStore} from './store/picStore'
const pinia=createPinia()
const persist=createPersistedState()
pinia.use(persist)
// 演示图片未就位时加载失败统一回退默认图，避免全站破图
document.addEventListener('error',e=>{
  const el=e.target
  if(el && el.tagName==='IMG' && !el.dataset.fbk){
    el.dataset.fbk='1'
    el.src=usePicStore().picIp+'default.png'
  }
},true)
createApp(App).use(router).use(Vant).use(pinia).mount('#app')