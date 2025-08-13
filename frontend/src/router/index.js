/*
 * @Author: zyj 1814793994@qq.com
 * @Date: 2025-07-26 14:56:41
 * @LastEditors: zyj 1814793994@qq.com
 * @LastEditTime: 2025-08-07 18:51:05
 * @FilePath: \machine\src\router\index.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/*
 * @Author: zyj 1814793994@qq.com
 * @Date: 2025-07-26 14:56:41
 * @LastEditors: zyj 1814793994@qq.com
 * @LastEditTime: 2025-07-26 15:43:34
 * @FilePath: \machine\src\router\index.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/login/index.vue'),
    },
    {
      path: '/home', // 主布局页面路径
      name: 'home',
      component: () => import('../views/Home/HomeLayout.vue'),
      redirect: '/user-management', // 默认重定向到用户管理页面
      children: [
        {
          path: '/user-management', // 用户管理页面路径
          name: 'user-management',
          component: () => import('../views/Home/UserManagement/UserManagement.vue'),
        },
        {
          path: '/device-details', // 设备详情页面路径
          name: 'device-details',
          component: () => import('../views/Home/DeviceDetails/DeviceDetails.vue'),
        },
        {
          path: '/system-settings', // 系统设置页面路径
          name: 'system-settings',
          component: () => import('../views/Home/SystemSettings/SystemSettings.vue'),
        },
      ],
    },
  ],
})

export default router