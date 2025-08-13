<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>系统设置</span>
      </div>
    </template>
    <el-form :model="form" ref="formRef" :rules="rules" label-width="120px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" disabled />
      </el-form-item>
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmNewPassword">
        <el-input v-model="form.confirmNewPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">修改密码</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const formRef = ref(null);
const form = reactive({
  username: 'admin1',
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: '',
});

const rules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  confirmNewPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致!'));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
};

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
      axios.put(
        'http://10.20.248.137:8080/admin/auth/change_password',
        null,
        {
          params: {
            oldPassword: form.oldPassword,
            newPassword: form.newPassword,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )
        .then(response => {
          if (response.data.code === 200) {
            ElMessage.success('密码修改成功！请重新登录');
            localStorage.removeItem('token');
            router.push('/');
          } else {
            ElMessage.error(response.data.message || '密码修改失败');
          }
        })
        .catch(error => {
          console.error('修改密码请求出错:', error);
          ElMessage.error('请求失败，请检查网络或服务器');
        });
    } else {
      ElMessage.error('请填写完整信息！');
      return false;
    }
  });
};
</script>

<style scoped lang="less">
.box-card {
  max-width: 800px;
  margin: 0 auto;
}
.card-header {
  font-size: 24px;
  font-weight: bold;
}
// 修改表单项标签（label）的字体大小 
:deep(.el-form-item__label) {
  font-size: 20px;
  width: 140px !important;
}
//修改输入框内文字的字体大小
:deep(.el-input__inner) {
  font-size: 20px;
}
//修改按钮的字体大小 
:deep(.el-button) {
  font-size: 20px;
  margin-left:3%;
}
//修改主要按钮的背景颜色 
:deep(.el-button--primary) {
  background-color: #87ceeb;
  border-color: #87ceeb;
}

// 鼠标悬停时的样式
:deep(.el-button--primary:hover) {
  background-color: #87ceeb;
  border-color: #87ceeb;
  opacity: 0.8;
}
</style>