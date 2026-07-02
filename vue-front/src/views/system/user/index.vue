<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>用户管理</h2>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-bar">
        <el-form-item label="关键字">
          <el-input
            v-model="queryForm.keyword"
            placeholder="认证号/姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="认证号" width="130" />
        <el-table-column prop="username" label="姓名" width="100" />
        <el-table-column prop="notesId" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleRole(row)">角色</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total,sizes,prev,pager,next,jumper"
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        @change="fetchData"
      />
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.isEdit ? '编辑用户' : '新增用户'"
      width="520px"
    >
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="80px">
        <el-form-item label="认证号" prop="userId">
          <el-input v-model="dialog.form.userId" :disabled="dialog.isEdit" placeholder="12位数字" />
        </el-form-item>
        <el-form-item label="姓名" prop="username">
          <el-input v-model="dialog.form.username" placeholder="中文姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="dialog.form.notesId" placeholder="邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dialog.form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="dialog.loading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 角色分配弹窗 -->
    <el-dialog v-model="roleDialog.visible" title="分配角色" width="460px">
      <el-checkbox-group v-model="roleDialog.checkedRoles" v-loading="roleDialog.loading">
        <el-checkbox v-for="r in roleDialog.allRoles" :key="r.id" :label="r.id" :value="r.id">
          {{ r.roleName }} — {{ r.description }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit" :loading="roleDialog.loading"
          >保存</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  assignUserRoles,
  getUserRoles
} from '@/api/system/user'
import { getRoleList } from '@/api/system/role'

const queryForm = reactive({ page: 1, size: 10, keyword: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList({ ...queryForm })
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
function handleSearch() {
  queryForm.page = 1
  fetchData()
}
function handleReset() {
  queryForm.keyword = ''
  queryForm.page = 1
  fetchData()
}

const formRef = ref(null)
const dialog = reactive({
  visible: false,
  isEdit: false,
  loading: false,
  form: {
    id: null,
    userId: '',
    username: '',
    notesId: '',
    status: 1
  }
})
const rules = {
  userId: [{ required: true, message: '请输入认证号', trigger: 'blur' }],
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

function handleAdd() {
  dialog.isEdit = false
  Object.assign(dialog.form, {
    id: null,
    userId: '',
    username: '',
    notesId: '',
    status: 1
  })
  dialog.visible = true
}
function handleEdit(row) {
  dialog.isEdit = true
  Object.assign(dialog.form, { ...row })
  dialog.visible = true
}
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.loading = true
  try {
    const { createTime, updateTime, deleted, ...data } = dialog.form
    dialog.isEdit ? await updateUser(data) : await createUser(data)
    ElMessage.success(dialog.isEdit ? '更新成功' : '创建成功')
    dialog.visible = false
    fetchData()
  } finally {
    dialog.loading = false
  }
}
function handleDelete(row) {
  ElMessageBox.confirm(`确定删除「${row.username || row.userId}」?`, '确认', {
    type: 'warning'
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('已删除')
    fetchData()
  })
}

const roleDialog = reactive({
  visible: false,
  loading: false,
  userId: null,
  allRoles: [],
  checkedRoles: []
})
async function handleRole(row) {
  roleDialog.userId = row.id
  roleDialog.visible = true
  roleDialog.loading = true
  try {
    const [roles, userRoles] = await Promise.all([
      getRoleList({ page: 1, size: 100 }),
      getUserRoles(row.id)
    ])
    roleDialog.allRoles = roles.records || []
    roleDialog.checkedRoles = (userRoles || []).map(r => r.id || r.roleId)
  } finally {
    roleDialog.loading = false
  }
}
async function handleRoleSubmit() {
  roleDialog.loading = true
  try {
    await assignUserRoles(roleDialog.userId, roleDialog.checkedRoles)
    ElMessage.success('角色分配成功')
    roleDialog.visible = false
  } finally {
    roleDialog.loading = false
  }
}

onMounted(async () => {
  fetchData()
  try {
  } catch (e) {
    /*empty*/
  }
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  h2 {
    margin: 0;
    font-size: 18px;
  }
}
.search-bar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
