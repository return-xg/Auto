import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          
          // 根据用户角色判断是否隐藏侧边栏
          const userRoles = store.getters.roles || []
          if (userRoles.includes('common')) {
            // 隐藏侧边栏
            store.dispatch('app/toggleSideBarHide', true)
            // 允许common角色访问的页面列表
            const allowedPaths = [
              '/product/userIndex',
              '/address',
              '/favorite',
              '/cart',
              '/order'
            ]
            // 如果当前路径不在允许列表中，则跳转到商品列表
            if (!allowedPaths.includes(to.path)) {
              next({ path: '/product/userIndex', replace: true })
              return
            }
          } else {
            // 显示侧边栏
            store.dispatch('app/toggleSideBarHide', false)
          }
          
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
      } else {
        // 已经获取了用户角色信息，再次判断角色
        const userRoles = store.getters.roles || []
        if (userRoles.includes('common')) {
          // 允许common角色访问的页面列表
          const allowedPaths = [
            '/product/userIndex',
            '/address',
            '/favorite',
            '/cart',
            '/order'
          ]
          // 如果是common角色，只允许访问指定页面
          if (!allowedPaths.includes(to.path)) {
            next({ path: '/product/userIndex', replace: true })
            return
          }
        }
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
