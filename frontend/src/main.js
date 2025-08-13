/*
 * @Author: zyj 1814793994@qq.com
 * @Date: 2025-07-26 14:56:41
 * @LastEditors: zyj 1814793994@qq.com
 * @LastEditTime: 2025-07-26 20:22:07
 * @FilePath: \machine\src\main.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
// import './assets/main.css'
import 'normalize.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(createPinia())
app.use(ElementPlus)
app.use(router)

app.mount('#app')
