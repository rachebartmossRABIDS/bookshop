import { createRouter, createWebHashHistory } from 'vue-router'
import { showNotify } from 'vant'
import { useUserStore } from '../store/userStore'


const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            component: () => import('../views/MainView.vue'),
            redirect: '/home',
            children: [
                { path: '/mine', component: () => import('../views/MineView.vue') },
                { path: '/home', component: () => import('../views/HomeView.vue') },
                { path: '/shop', component: () => import('../views/ShopView.vue') },
                { path: '/cart', component: () => import('../views/CartView.vue'), meta: { requiresAuth: true } },
                { path: '/detail/:bid', component: () => import('../views/DetailView.vue') },
            ]
        },
        { path: '/sdetail/:sid', component: () => import('../views/SDetailView.vue') },
        { path: '/login', component: () => import('../views/LoginView.vue') },
        { path: '/register', component: () => import('../views/RegisterView.vue') },
        { path: '/forgot-password', component: () => import('../views/ForgotPasswordView.vue') },
        { path: '/search', component: () => import('../views/SearchView.vue') },
        { path: '/searchResult/:content', component: () => import('../views/SearchResultView.vue') },
        { path: '/userComment', component: () => import('../views/UserCommentView.vue'), meta: { requiresAuth: true } },
        { path: '/updatePassword', component: () => import('../views/UpdatePasswordView.vue'), meta: { requiresAuth: true } },
        { path: '/order', component: () => import('../views/OrderView.vue'), meta: { requiresAuth: true } },
        { path: '/order/detail/:oid', component: () => import('../views/OrderDetailView.vue'), meta: { requiresAuth: true } },
        { path: '/address', component: () => import('../views/AddressView.vue'), meta: { requiresAuth: true } },
        { path: '/notifications', component: () => import('../views/NotificationView.vue'), meta: { requiresAuth: true } },
        { path: '/profile', component: () => import('../views/ProfileView.vue'), meta: { requiresAuth: true } },
        { path: '/favorites', component: () => import('../views/FavoritesView.vue'), meta: { requiresAuth: true } },
        { path: '/admin', component: () => import('../views/AdminView.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
        { path: '/:pathMatch(.*)*', component: () => import('../views/NotFoundView.vue') },
    ]
})

// 需要登录的页面：未登录时跳转登录页，并记录原目标路径便于登录后回跳
router.beforeEach((to) => {
    if (to.meta.requiresAuth) {
        const userStore = useUserStore()
        if (!userStore.token) {
            showNotify({ type: 'warning', message: '请先登录' })
            return { path: '/login', query: { redirect: to.fullPath } }
        }
        if (to.meta.requiresAdmin && userStore.info.type != 2) {
            showNotify({ type: 'danger', message: '需要管理员权限' })
            return { path: '/mine' }
        }
    }
})

export default router
