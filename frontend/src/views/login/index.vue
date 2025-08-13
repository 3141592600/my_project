<template>
  <div class="login-container">
    <div class="top-area">
      <img src="@/assets/managesystem.png" alt="system" class="system-image" />
      <span class="title">重庆云朵智能设备有限公司后台管理系统</span>
    </div>
    <div class="logo-area">
      <img src="@/assets/logo.png" alt="logo" class="logo-image" />
      <div class="company-info">重庆云朵智能设备有限公司</div>
    </div>
    <div class="login-right">
      <div class="login-form-card">
        <el-form :model="loginForm" ref="loginFormRef" class="login-form">
          <div class="form-title">欢迎您的回来</div>
          <div class="form-timestamp">{{ currentTime }}</div>
          <el-form-item prop="username" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]">
            <el-input v-model="loginForm.username" placeholder="请输入用户名">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="remember-password">
            <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLogin" class="login-button">
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router'; // 引入 useRouter
import axios from 'axios';

const router = useRouter(); // 获取 router 实例

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
});

// 是否记住密码
const rememberPassword = ref(false);

const loginFormRef = ref(null);

// 实时时间
const currentTime = ref('');
let timer = null;

// 获取当前时间并格式化
const updateTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const date = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  currentTime.value = `${year}-${month}-${date} ${hours}:${minutes}:${seconds}`;
};

// 在组件挂载时开始计时
onMounted(() => {
  updateTime();
  timer = setInterval(updateTime, 1000);
});

// 在组件卸载时清除计时器
onUnmounted(() => {
  clearInterval(timer);
});

// 登录按钮点击事件
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      // 在这里执行登录逻辑
      const loginPayload = {
        username: loginForm.username,
        password: loginForm.password,
      };

      axios.post('http://10.20.248.137:8080/admin/auth/login', loginPayload)
        .then(response => {
          // 检查返回的 code
          if (response.data.code === 200) {
            ElMessage.success('登录成功！');
            // 存储 token，例如在 localStorage 中
            localStorage.setItem('token', response.data.data);
            // 登录成功后跳转到 /home 路径
            router.push('/home');
          } else {
            ElMessage.error(response.data.message || '登录失败');
          }
        })
        .catch(error => {
          console.error('登录请求出错:', error);
          ElMessage.error('登录请求失败，请检查网络或服务器');
        });

    } else {
      ElMessage.error('请填写完整信息！');
      return false;
    }
  });
};
</script>

<style scoped lang="less">
/* 整个页面的容器，设置背景色和布局 */
.login-container {
  // display: flex;
  // justify-content: center;
  // align-items: center;
  width: 100vw;
  height: 100vh;
  background-color: #87ceeb;/* 浅天空蓝色 */
  position: relative;
  .top-area {
    width: 100%;
    display: flex;
    align-items: center;
    padding-left: 2%;
    padding-top: 2%;
    padding-bottom: 1%;
    border-bottom: 1px solid #feffff;
    .system-image {
      width: 5%;
    }
    .title {
      font-size: 32px;
      color: #feffff;
    }
  }
}

.logo-area {
  display: flex;
  width: 33%;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  margin-top: 18%;
  margin-left: 3%;
  .logo-image {
    width: 90%; /* 调整logo大小 */
    height: 170px;
    margin-right: 15px;
  }

  .company-info {
    // display: flex;
    // flex-direction: column;
    font-size: 32px;
    color: #feffff;
  }
}
/* 右侧登录表单卡片 */
.login-right {
  position: absolute;
  top: 50%;
  right: 15%;
  transform: translateY(-50%);
}

.login-form-card {
  width: 100%;
  padding: 40px;
  background-color: #feffff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.form-title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  color: #87ceeb;
}

.form-timestamp {
  font-size: 16px;
  text-align: center;
  color: #999;
  margin-bottom: 30px;
}

.login-form {
  padding-top: 20px;
}

.el-input {
  height: 40px;
  margin-bottom: 20px;
}
.login-form-card :deep(.el-input__inner) {
  font-size: 20px; /* 直接修改输入框内部的字体大小 */
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 22px;
  background-color: #87ceeb;
}

.remember-password {
  text-align: right;
  font-size: 18px;
}
</style>