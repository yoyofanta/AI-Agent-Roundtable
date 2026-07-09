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
        view: 'roundtable'
      }
    },
    meta: {
      requiresAuth: true
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

      // 旧功能保留，但不作为新项目主入口
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

  if (!token && to.path.startsWith('/app')) {
    return '/login'
  }

  if (token && to.path === '/login') {
    return {
      path: '/app/tree-hole',
      query: {
        view: 'roundtable'
      }
    }
  }

  return true
})

export default router