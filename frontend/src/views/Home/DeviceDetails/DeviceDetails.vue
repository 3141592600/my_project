<template>
  <div>
    <div style="margin-bottom: 20px; display: flex; align-items: center; justify-content: flex-end;">
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
    <div v-for="merchant in deviceData" :key="merchant.merchantName">
      <div class="merchant-title">{{ merchant.merchantName }}</div>
        <template v-for="device in merchant.devices" :key="device.deviceName">
          <el-descriptions :column="3" border class="device-descriptions">
          <el-descriptions-item label="设备名称">{{ device.deviceName }}</el-descriptions-item>
          <el-descriptions-item label="设备编码">{{ device.data.device_code }}</el-descriptions-item>
          <el-descriptions-item label="设备类型">{{ device.data.device_type }}</el-descriptions-item>
          <el-descriptions-item label="使用状态">{{ device.data.latest_status }}</el-descriptions-item>
          <el-descriptions-item label="是否缴费">{{ device.data.pay_status ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="出库总量">{{ device.data.total_output_quantity }}</el-descriptions-item>
          <el-descriptions-item label="入库总量">{{ device.data.total_input_quantity }}</el-descriptions-item>
          <el-descriptions-item label="剩余数量">{{ device.data.remaining_quantity }}</el-descriptions-item>
          <el-descriptions-item label="操作">
            <el-button class="edit-button" size="small" @click="handleEdit(merchant.merchantName, device)">编辑</el-button>
            <el-button class="delete-button" size="small" type="danger" @click="handleDelete(merchant.merchantName, device)">删除</el-button>
            <el-switch
                v-model="device.data.is_disabled"
                :active-text="'禁用'"
                :inactive-text="'启用'"
                @change="(value) => handleDisableChange(merchant.merchantName, device.deviceName, value)"
                style="margin-left: 10px;border-color:#87ceeb;"
            />
          </el-descriptions-item>
          </el-descriptions>
        </template>
    </div>

    <div class="pagination-container" v-if="!searchQuery">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[1,2,3, 5, 7, 9, 11, 13]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        prev-text="上一页"
        next-text="下一页"
        :locale="{ goto: '前往', page: '页' }"
      />
    </div>

    <el-dialog v-model="editDeviceDialogVisible" title="编辑设备" width="55%">
      <el-form :model="editDeviceForm" ref="editDeviceFormRef" :rules="editDeviceRules" label-width="120px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="editDeviceForm.deviceName" disabled />
        </el-form-item>
        <el-form-item label="设备编码" prop="device_code">
          <el-input v-model="editDeviceForm.device_code" />
        </el-form-item>
        <el-form-item label="设备类型" prop="device_type">
          <el-input v-model="editDeviceForm.device_type" />
        </el-form-item>
        <el-form-item label="使用状态" prop="latest_status">
          <el-input v-model="editDeviceForm.latest_status" />
        </el-form-item>
        <el-form-item label="出库总量" prop="total_output_quantity">
          <el-input-number v-model="editDeviceForm.total_output_quantity" :min="0" />
        </el-form-item>
        <el-form-item label="入库总量" prop="total_input_quantity">
          <el-input-number v-model="editDeviceForm.total_input_quantity" :min="0" />
        </el-form-item>
        <el-form-item label="剩余数量" prop="remaining_quantity">
          <el-input-number v-model="editDeviceForm.remaining_quantity" :min="0" />
        </el-form-item>
        <el-form-item label="是否缴费" prop="pay_status">
          <el-switch v-model="editDeviceForm.pay_status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDeviceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditDeviceForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// 设备列表数据
const deviceData = ref([]);

// 查询框数据
const searchQuery = ref('');

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 2,
  total: 0,
});

// 新增一个标志，用于控制在数据更新时不触发禁用操作
const isUpdatingData = ref(false);

// 编辑设备弹窗相关
const editDeviceDialogVisible = ref(false);
const editDeviceFormRef = ref(null);

const editDeviceForm = reactive({
  id: '', // 新增 id 字段
  merchant_id: '',
  deviceName: '',
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

// 新增用于存储 merchantName 的变量
const currentMerchantName = ref('');

// 编辑设备表单验证规则
const editDeviceRules = reactive({
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  device_code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  device_type: [{ required: true, message: '请输入设备类型', trigger: 'blur' }],
  latest_status: [{ required: true, message: '请输入使用状态', trigger: 'blur' }],
});

// 获取所有公司设备列表的函数
const fetchDeviceData = (isSearch = false) => {
  isUpdatingData.value = true; // 开始更新数据时设置为true
  const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';

  if (isSearch && searchQuery.value) {
    axios.get('http://10.20.248.137:8080/admin/auth/getAllDeviceInfo', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        merchantName: searchQuery.value,
      },
    })
    .then(response => {
      if (response.data.code === 200) {
        if (response.data.data && response.data.data.length > 0) {
          // 直接将后端返回的数组赋值给 deviceData.value
          deviceData.value = response.data.data;
          // 重新计算总设备数量
          const totalDevices = response.data.data.reduce((sum, merchant) => {
            return sum + (merchant.devices ? merchant.devices.length : 0);
          }, 0);
          pagination.total = totalDevices;
        } else {
          deviceData.value = [];
          pagination.total = 0;
        }
      } else {
        ElMessage.error(response.data.message || '查询设备列表失败');
        deviceData.value = [];
        pagination.total = 0;
      }
    })
    .catch(error => {
      console.error('查询设备列表请求出错:', error);
      ElMessage.error('查询设备列表失败，请检查网络或服务器');
      deviceData.value = [];
      pagination.total = 0;
    })
    .finally(() => {
        isUpdatingData.value = false; // 数据更新完成时设置为false
    });
  } else {
    axios.get('http://10.20.248.137:8080/admin/auth/getAllMerchantsDeviceInfo', {
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
        deviceData.value = response.data.data.list;
        pagination.total = response.data.data.total;
      } else {
        ElMessage.error(response.data.message || '获取设备列表失败');
      }
    })
    .catch(error => {
      console.error('获取设备列表请求出错:', error);
      ElMessage.error('获取设备列表失败，请检查网络或服务器');
    })
    .finally(() => {
        isUpdatingData.value = false; // 数据更新完成时设置为false
    });
  }
};

// 在组件挂载时调用，以初始化列表
onMounted(() => {
  fetchDeviceData();
});

// 使用watch来监听searchQuery的变化，当查询框清空时，自动重新加载所有数据
watch(searchQuery, (newVal) => {
  if (!newVal) {
    fetchDeviceData();
  }
});

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val;
  fetchDeviceData();
};

// 处理当前页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  fetchDeviceData();
};

// 处理查询操作
const handleSearch = () => {
  pagination.currentPage = 1;
  fetchDeviceData(true);
};

// 修改 handleEdit 函数以接收 merchantName
const handleEdit = (merchantName, device) => {
  editDeviceDialogVisible.value = true;
  currentMerchantName.value = merchantName;// 存储 merchantName
  console.log(currentMerchantName.value);
  Object.assign(editDeviceForm, {
    id: device.data.id, // 从设备数据中获取 id
    merchant_id: device.merchant_id,
    deviceName: device.deviceName,
    device_code: device.data.device_code,
    device_type: device.data.device_type,
    latest_status: device.data.latest_status,
    total_output_quantity: device.data.total_output_quantity,
    total_input_quantity: device.data.total_input_quantity,
    remaining_quantity: device.data.remaining_quantity,
    pay_status: device.data.pay_status,
    create_time: '',
    update_time: ''
  });
};

const submitEditDeviceForm = () => {
  // 这里需要手动创建一个 ref 来绑定到 el-form
  // 或者在 <template> 中直接使用 editDeviceFormRef
  // 比如 <el-form ref="editDeviceFormRef" ...>
  // 目前代码中没有这个 ref，所以暂时忽略验证
  // editDeviceFormRef.value.validate((valid) => {
  //   if (valid) {
  const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
  const requestBody = {
    id: editDeviceForm.id, // 在请求体中添加 id
    merchant_id: '',
    device_code: editDeviceForm.device_code,
    device_type: editDeviceForm.device_type,
    latest_status: editDeviceForm.latest_status,
    total_output_quantity: editDeviceForm.total_output_quantity,
    total_input_quantity: editDeviceForm.total_input_quantity,
    remaining_quantity: editDeviceForm.remaining_quantity,
    pay_status: editDeviceForm.pay_status,
    create_time: '',
    update_time: '',
  };
  
  axios.post('http://10.20.248.137:8080/admin/auth/edit_device', requestBody, {
    headers: {
      Authorization: `Bearer ${token}`
    },
    params: {
      merchantName: currentMerchantName.value,
      deviceName: editDeviceForm.deviceName,
    },
  })
  .then(response => {
    if (response.data.code === 200) {
      ElMessage.success('编辑成功！');
      editDeviceDialogVisible.value = false;
      fetchDeviceData();
    } else {
      ElMessage.error(response.data.message || '编辑失败');
    }
  })
  .catch(error => {
    console.error('编辑设备请求出错:', error);
    ElMessage.error('编辑设备失败，请检查网络或服务器');
  });
  //   } else {
  //     ElMessage.error('请填写完整信息！');
  //     return false;
  //   }
  // });
};


const handleDelete = (merchantName, device) => {
  ElMessageBox.confirm(
    `确定要删除设备: ${device.deviceName} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(() => {
    const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
    axios.delete('http://10.20.248.137:8080/admin/auth/delete_device', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        merchantName: merchantName,
        deviceName: device.deviceName,
      },
    })
    .then(response => {
      if (response.data.code === 200) {
        ElMessage.success('删除成功！');
        fetchDeviceData();
      } else {
        ElMessage.error(response.data.message || '删除失败');
      }
    })
    .catch(error => {
      console.error('删除设备请求出错:', error);
      ElMessage.error('删除设备失败，请检查网络或服务器');
    });
  })
  .catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 新增 handleDisableChange 方法
const handleDisableChange = (merchantName, deviceName, isDisabled) => {
    if (isUpdatingData.value) {
        return;
    }

    const token = localStorage.getItem('token') || 'eyJhbGciOiJIUzI1NiIsInR...';
    const requestBody = {
        merchantName,
        deviceName,
        isDisabled: isDisabled ? 'true' : 'false',
    };

    axios.post('http://10.20.248.137:8080/admin/auth/disable_device', requestBody, {
        headers: {
            Authorization: `Bearer ${token}`
        },
    })
    .then(response => {
        if (response.data.code === 200) {
            ElMessage.success(response.data.message || '操作成功');
            fetchDeviceData();
        } else {
            ElMessage.error(response.data.message || '操作失败');
        }
    })
    .catch(error => {
        console.error('禁用设备请求出错:', error);
        ElMessage.error('操作失败，请检查网络或服务器');
        // 如果请求失败，将开关状态恢复为原来的值
        // 重新获取数据会刷新页面，从而恢复原始状态
        fetchDeviceData();
    });
};
</script>

<style scoped lang="less">
/* 复用 UserManagement.vue 的样式 */
/* 新增的样式：设置 el-descriptions-item 的固定宽度 */
// :deep(.el-descriptions-item__label) {
//   width: 150px; /* 设置标签的固定宽度 */
//   min-width: 150px; /* 确保最小宽度也固定 */
//   font-size: 20px;
//   font-weight: bold;
// }
// :deep(.el-descriptions-item__content) {
//   width: 250px; /* 设置内容的固定宽度 */
//   min-width: 250px; /* 确保最小宽度也固定 */
//   font-size: 20px;
// }
:deep(.el-descriptions__label.el-descriptions__cell.is-bordered-label){
  width:10% ;
  font-size: 22px;
}
:deep(.el-descriptions__content.el-descriptions__cell.is-bordered-content){
  width:15%;
  font-size: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
/* 复制 UserManagement.vue 中的按钮样式 */
.edit-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size: 18px;
  &:hover {
    opacity: 0.8;
  }
}

.delete-button {
  background-color: #87ceeb;
  border-color: #87ceeb;
  color: #fff;
  font-size: 18px;
  &:hover {
    opacity: 0.8;
  }
}

.merchant-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color:#545c64;
}

.device-descriptions {
  margin-bottom: 20px; /* 新增的样式：增加表单之间的间距 */
}

/* 弹窗样式 */
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
/* 修改 el-switch 激活时的背景颜色 */
:deep(.el-switch.is-checked .el-switch__core) {
background-color: #87ceeb !important;
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
</style>