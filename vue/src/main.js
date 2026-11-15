import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css'
import {createPinia} from 'pinia'
import {createPersistedState} from 'pinia-persistedstate-plugin'
const pinia=createPinia()
const persist=createPersistedState()
pinia.use(persist)
createApp(App).use(router).use(Vant).use(pinia).mount('#app')