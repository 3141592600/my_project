<template>
  <div>
    <div style="margin-bottom: 20px; display: flex; align-items: center; justify-content: space-between;">
      <div>
        <el-button class="add-button" type="primary" @click="handleCreate">新增公司账户</el-button>
      </div>
      <div style="display: flex; align-items: center;">
        <el-input
          v-model="searchQuery"
          placeholder="请输入公司名称"
          clearable
          style="width: 250px; margin-right: 10px;font-size: 16px;"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" class="search-button" @click="handleSearch">查询</el-button>
      </div>
    </div>
    <el-table :data="users" style="width: 100%">
      <el-table-column class="table-label" prop="merchant_name" label="公司名称" />
      <el-table-column class="table-label" prop="contact_name" label="联系人姓名" />
      <el-table-column class="table-label" prop="contact_phone" label="联系人电话号码" />
      <el-table-column class="table-label" prop="email" label="联系人邮箱" />
      <el-table-column class="table-label" prop="username" label="用户名" />
      <el-table-column label="操作" width="400">
        <template #default="scope">
          <el-button class="edit-button" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button class="delete-button" size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          <el-button class="generate-jwt-button" size="small" @click="handleGenerateJWT(scope.row)">生成JWT</el-button>
          <el-button class="create-device-button" size="small" @click="handleCreateDevice(scope.row)">新建设备</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[9, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        prev-text="上一页"
        next-text="下一页"
        :locale="{ goto: '前往', page: '页' }"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="55%">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="120px">
        <el-form-item label="公司名称" prop="merchant_name">
          <el-input v-model="form.merchant_name" />
        </el-form-item>
        <el-form-item label="联系人姓名" prop="contact_name">
          <el-input v-model="form.contact_name" />
        </el-form-item>
        <el-form-item label="联系人电话号码" prop="contact_phone">
          <el-input v-model="form.contact_phone" />
        </el-form-item>
        <el-form-item label="联系人邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="deviceDialogVisible" title="新建设备" width="55%">
      <el-form :model="deviceForm" ref="deviceFormRef" :rules="deviceRules" label-width="120px">
        <el-form-item label="设备名称" prop="tableName">
          <el-input v-model="deviceForm.tableName" />
        </el-form-item>
        <el-form-item label="设备编码" prop="device_code">
          <el-input v-model="deviceForm.device_code" />
        </el-form-item>
        <el-form-item label="设备类型" prop="device_type">
          <el-input v-model="deviceForm.device_type" />
        </el-form-item>
        <el-form-item label="使用状态" prop="latest_status">
          <el-input v-model="deviceForm.latest_status" />
        </el-form-item>
        <el-form-item label="出库总量" prop="total_output_quantity">
          <el-input-number v-model="deviceForm.total_output_quantity" :min="0" />
        </el-form-item>
        <el-form-item label="入库总量" prop="total_input_quantity">
          <el-input-number v-model="deviceForm.total_input_quantity" :min="0" />
        </el-form-item>
        <el-form-item label="剩余数量" prop="remaining_quantity">
          <el-input-number v-model="deviceForm.remaining_quantity" :min="0" />
        </el-form-item>
        <el-form-item label="是否缴费" prop="pay_status">
          <el-switch v-model="deviceForm.pay_status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="deviceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDeviceForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// 弹窗相关
const dialogVisible = ref(false);
const dialogTitle = ref('新增公司账户');
const formRef = ref(null);

// 新增设备弹窗相关
const deviceDialogVisible = ref(false);
const deviceFormRef = ref(null);

// 查询框数据
const searchQuery = ref('');

// 表单数据，与后端接口字段对应
const form = reactive({
  merchant_id: null,
  merchant_name: '',
  contact_name: '',
  contact_phone: '',
  email: '',
  username: '',
  password: '',
  status: 1
});

// 设备表单数据
const deviceForm = reactive({
  merchant_id: '',
  tableName: '',
  device_code: '',
  device_type: '',
  latest_status: '',
  total_output_quantity: 0,
  total_input_quantity: 0,
  remaining_quantity: 0,
  pay_status: false,
  create_time: '',
  update_time: ''
});

// 表单验证规则
const rules = reactive({
  merchant_name: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  contact_name: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contact_phone: [{ required: true, message: '请输入联系人电话号码', trigger: 'blur' }],
  email: [{ required: false, message: '请输入联系人邮箱', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: false, message: '请输入密码', trigger: 'blur' }],
});

// 设备表单验证规则
const deviceRules = reactive({
  tableName: [{ required: true, message: '请输入设备名', trigger: 'blur' }],
  device_code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  device_type: [{ required: true, message: '请输入设备类型', trigger: 'blur' }],
  latest_status: [{ required: true, message: '请输入使用状态', trigger: 'blur' }],
});

// 用户列表数据
const users = ref([]);

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 9, 
  total: 0,
});

// 获取用户列表的函数
const fetchUsers = (isSearch = false) => {
  const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
  
  if (isSearch && searchQuery.value) {
    axios.get('http://10.20.248.137:8080/admin/auth/getMerchantByMerchantName', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        merchantName: searchQuery.value,
      },
    })
    .then(response => {
      if (response.data.code === 200) {
        if (response.data.data) {
          users.value = [response.data.data];
          pagination.total = 1;
        } else {
          users.value = [];
          pagination.total = 0;
        }
      } else {
        ElMessage.error(response.data.message || '查询失败');
        users.value = [];
        pagination.total = 0;
      }
    })
    .catch(error => {
      console.error('查询请求出错:', error);
      ElMessage.error('查询失败，请检查网络或服务器');
      users.value = [];
      pagination.total = 0;
    });
  } else {
    axios.get('http://10.20.248.137:8080/merchant/getMerchantAllInfo', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
      },
    })
    .then(response => {
      if (response.data.code === 200) {
        users.value = response.data.data.list;
        pagination.total = response.data.data.total;
      } else {
        ElMessage.error(response.data.message || '获取用户列表失败');
      }
    })
    .catch(error => {
      console.error('获取用户列表请求出错:', error);
      ElMessage.error('获取用户列表失败，请检查网络或服务器');
    });
  }
};

// 在组件挂载时调用，以初始化列表
onMounted(() => {
  fetchUsers();
});

// 使用watch来监听searchQuery的变化，当查询框清空时，自动重新加载所有数据
watch(searchQuery, (newVal) => {
  if (!newVal) {
    fetchUsers();
  }
});

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val;
  fetchUsers();
};

// 处理当前页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  fetchUsers();
};

// 处理查询操作
const handleSearch = () => {
  pagination.currentPage = 1;
  fetchUsers(true);
};

// 打开新增公司账户弹窗
const handleCreate = () => {
  dialogTitle.value = '新增公司账户';
  if (formRef.value) {
    formRef.value.resetFields();
  }
  Object.assign(form, {
    merchant_id: null,
    merchant_name: '',
    contact_name: '',
    contact_phone: '',
    email: '',
    username: '',
    password: '',
    status: 1
  });
  rules.password[0].required = true;
  dialogVisible.value = true;
};

// 打开编辑公司账户弹窗
const handleEdit = (row) => {
  dialogTitle.value = '编辑公司账户';
  Object.assign(form, row); 
  form.password = '';
  rules.password[0].required = false;
  dialogVisible.value = true;
};

// 提交公司账户表单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
      if (dialogTitle.value === '新增公司账户') {
        axios.post('http://10.20.248.137:8080/merchant/register', form, {
          headers: {
            Authorization: `Bearer ${token}`
          },
        })
        .then(response => {
          if (response.data.code === 200) {
            ElMessage.success('新增公司账户成功！');
            dialogVisible.value = false;
            fetchUsers();
          } else {
            ElMessage.error(response.data.message || '新增失败');
          }
        })
        .catch(error => {
          console.error('新增账户请求出错:', error);
          ElMessage.error('请求失败，请检查网络或服务器');
        });
      } else { 
        const data = {
          merchant_id: form.merchant_id,
          merchant_name: form.merchant_name,
          contact_name: form.contact_name,
          contact_phone: form.contact_phone,
          email: form.email,
          password: form.password,
          status: form.status,
        };

        axios.put('http://10.20.248.137:8080/admin/auth/update_merchant', data, {
          headers: {
            Authorization: `Bearer ${token}`
          },
        })
        .then(response => {
          if (response.data.code === 200) {
            ElMessage.success('编辑公司账户成功！');
            dialogVisible.value = false;
            fetchUsers();
          } else {
            ElMessage.error(response.data.message || '编辑失败');
          }
        })
        .catch(error => {
          console.error('编辑账户请求出错:', error);
          ElMessage.error('请求失败，请检查网络或服务器');
        });
      }
    } else {
      ElMessage.error('请填写完整信息！');
      return false;
    }
  });
};

// 处理删除操作
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除公司账户: ${row.merchant_name} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(() => {
    const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
    axios.delete('http://10.20.248.137:8080/admin/auth/deleteUserByMerchantID', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        merchantID: row.merchant_id,
      },
    })
    .then(response => {
      if (response.data.code === 200) {
        ElMessage.success('删除成功！');
        fetchUsers();
      } else {
        ElMessage.error(response.data.message || '删除失败');
      }
    })
    .catch(error => {
      console.error('删除账户请求出错:', error);
      ElMessage.error('删除失败，请检查网络或服务器');
    });
  })
  .catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 处理生成JWT操作
const handleGenerateJWT = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要为 ${row.merchant_name} 生成JWT吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
    
    const response = await axios.get('http://10.20.248.137:8080/admin/auth/getToken', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        merchantId: row.merchant_id,
      }
    });

    if (response.data.code === 200) {
      await ElMessageBox.alert(response.data.data, '生成的JWT', {
        confirmButtonText: '好的',
        title: '生成的JWT',
        type: 'success',
      });
      ElMessage.success('JWT生成成功！');
    } else {
      ElMessage.error(response.data.message || 'JWT生成失败');
    }
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消生成JWT');
    } else {
      console.error('生成JWT请求出错:', error);
      ElMessage.error('生成JWT失败，请检查网络或服务器');
    }
  }
};

// 处理新建设备操作
const handleCreateDevice = (row) => {
  // 重置表单
  if (deviceFormRef.value) {
    deviceFormRef.value.resetFields();
  }
  // 清空并设置 merchant_id
  Object.assign(deviceForm, {
    merchant_id: row.merchant_id,
    tableName: '',
    device_code: '',
    device_type: '',
    latest_status: '',
    total_output_quantity: 0,
    total_input_quantity: 0,
    remaining_quantity: 0,
    pay_status: false,
    create_time: '',
    update_time: ''
  });
  deviceDialogVisible.value = true;
};

// 提交设备表单
const submitDeviceForm = () => {
  deviceFormRef.value.validate((valid) => {
    if (valid) {
      const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
      // 构造请求体
      const requestBody = {
        merchant_id: deviceForm.merchant_id,
        device_code: deviceForm.device_code,
        device_type: deviceForm.device_type,
        latest_status: deviceForm.latest_status,
        total_output_quantity: deviceForm.total_output_quantity,
        total_input_quantity: deviceForm.total_input_quantity,
        remaining_quantity: deviceForm.remaining_quantity,
        pay_status: deviceForm.pay_status,
        create_time: '', // 留空
        update_time: ''  // 留空
      };
      
      axios.post(`http://10.20.248.137:8080/admin/auth/add_device?merchantId=${deviceForm.merchant_id}&tableName=${deviceForm.tableName}`, requestBody, {
        headers: {
          Authorization: `Bearer ${token}`
        },
      })
      .then(response => {
        if (response.data.code === 200) {
          ElMessage.success('新建设备成功！');
          deviceDialogVisible.value = false;
        } else {
          ElMessage.error(response.data.message || '新建设备失败');
        }
      })
      .catch(error => {
        console.error('新建设备请求出错:', error);
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
.add-button{
  background-color:#87ceeb;
  border-color: #87ceeb;
  font-size:20px;
}
.add-button:hover {
  background-color: #87ceeb;
  border-color: #87ceeb;
  opacity: 0.7;
}

// 新增编辑按钮样式
.edit-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size:18px;
  &:hover {
    opacity: 0.8;
  }
}

// 新增删除按钮样式
.delete-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size:18px;
  &:hover {
    opacity: 0.8;
  }
}

// 新增生成JWT按钮样式
.generate-jwt-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size:18px;
  &:hover {
    opacity: 0.8;
  }
}

// 新增的“新建设备”按钮样式
.create-device-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size:18px;
  &:hover {
    opacity: 0.8;
  }
}

//查询按钮样式
.search-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size: 20px;
  &:hover {
    opacity: 0.8;
  }
}

:deep(.el-table .cell) {
  font-size: 22px;
  padding: 12px 14px;
}
:deep(.el-table th .cell) {
  font-size: 24px;
  font-weight: bold;
}
:deep(.el-dialog__title) {
  font-size: 24px;
  font-weight: bold;
}
:deep(.el-dialog .el-form-item__label) {
  font-size: 20px;
  width: 20% !important;
}
:deep(.el-dialog .el-input__inner) {
  font-size: 20px;
}
:deep(.el-dialog__footer .el-button) {
  font-size: 20px;
}
:deep(.el-dialog__body) {
  height: 400px;
  overflow-y: auto;
}
:deep(.el-dialog__footer .el-button--primary) {
  background-color: #87ceeb;
  border-color: #87ceeb;
}

:deep(.el-dialog__footer .el-button--primary:hover) {
  background-color: #87ceeb;
  border-color: #87ceeb;
  opacity: 0.8;
}
:deep(.el-dialog__footer .el-button:hover) {
  background-color: #f5f7fa;
  border-color: #87ceeb;
  color: #87ceeb;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
/* 修改 el-switch 激活时的背景颜色 */
:deep(.el-switch.is-checked .el-switch__core) {
background-color: #87ceeb !important;
}
</style>