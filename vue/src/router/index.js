import { createRouter, createWebHashHistory } from 'vue-router'


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
                { path: '/detail/:bid', component: () => import('../views/DetailView.vue') },
            ]
        },
        { path: '/sdetail/:sid', component: () => import('../views/SDetailView.vue') },
        { path: '/login', component: () => import('../views/LoginView.vue') },
        { path: '/search', component: () => import('../views/SearchView.vue') },
        { path: '/searchResult/:content', component: () => import('../views/SearchResultView.vue') },
        { path: '/userComment', component: () => import('../views/UserCommentView.vue') },
        { path: '/updatePassword', component: () => import('../views/UpdatePasswordView.vue') },
        { path: '/order', component: () => import('../views/OrderView.vue') },
    ]
})
export default router 