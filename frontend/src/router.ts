import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import LoginPage from './pages/LoginPage.vue'
import OnboardingPage from './pages/OnboardingPage.vue'
import MainLayout from './pages/MainLayout.vue'
import DiaryEditPage from './pages/DiaryEditPage.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: LoginPage
  },
  {
    path: '/onboarding',
    component: OnboardingPage
  },
  {
    path: '/app',
    component: MainLayout,
    redirect: {
  path: '/app/tree-hole',
  query: {
    view: 'content'
  }
},
    children: [
      {
        path: 'tree-hole',
        name: 'TreeHole',
        component: () => import('./pages/TreeHolePage.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('./pages/ProfilePage.vue')
      },

      // 旧功能保留，但不作为主入口
      {
        path: 'diary',
        name: 'Diary',
        component: () => import('./pages/DiaryPage.vue')
      },
      {
        path: 'diary/edit',
        name: 'DiaryEdit',
        component: DiaryEditPage
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')

  /**
   * 登录页永远允许访问。
   * 即使本地有 token，也不要自动跳过登录页。
   * 这样打开 http://localhost:5174 会先看到登录 / 注册界面。
   */
  if (to.path === '/login') {
    return true
  }

  /**
   * 访问 app 页面时必须登录。
   */
  if (to.path.startsWith('/app') && !token) {
    return '/login'
  }

  return true
})

export default router