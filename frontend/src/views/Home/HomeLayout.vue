<!--
 * @Author: zyj 1814793994@qq.com
 * @Date: 2025-07-29 17:32:04
 * @LastEditors: zyj 1814793994@qq.com
 * @LastEditTime: 2025-08-10 17:40:17
 * @FilePath: \machine\src\views\Home\HomeLayout.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <el-container class="common-layout">
    <el-aside width="200px" class="sidebar">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
        background-color="#fff"
        text-color="#76797a"
        active-text-color="#87ceeb"
      >
        <el-menu-item index="user-management">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="device-details">
          <el-icon><Monitor /></el-icon>
          <span>设备详情</span>
        </el-menu-item>
        <el-menu-item index="system-settings">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
    </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="system-title">重庆云朵智能设备有限公司后台管理系统</div>
        <el-button type="info" @click="logout" class="logout-button">退出登录</el-button>
      </el-header>
      <el-main class="main-content">
        <router-view></router-view> </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Monitor,Setting } from '@element-plus/icons-vue';
import { computed } from 'vue';

const router = useRouter();
const route = useRoute();

const handleMenuSelect = (key) => {
  router.push(`/${key}`);
};

const logout = () => {
  // 实际的退出登录逻辑，清除token
  localStorage.removeItem('token');
  ElMessage.success('已退出登录！');
  router.push('/'); // 返回登录页面
};

const activeMenu = computed(() => {
  const currentPath = route.path.substring(1);
  return currentPath || 'user-management';
});
</script>

<style scoped lang="less">
.common-layout {
  height: 100vh;
}

.sidebar {
  background-color: #545c64;
}

.header {
  background-color: #87ceeb; /* 淡蓝色 */
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  .system-title {
    font-size: 30px;
    font-weight: bold;
  }
  .logout-button{
    font-size: 24px;
  }
}

.main-content {
  background-color: #f0f2f5; /* 浅灰色背景 */
  padding: 20px;
}

.el-menu-vertical-demo {
  height: 100%;
}
.el-menu-item{
  font-size:24px;
}

</style>