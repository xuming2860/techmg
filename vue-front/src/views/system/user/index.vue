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
        <el-table-column prop="authNo" label="认证号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
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
        <el-form-item label="认证号" prop="authNo">
          <el-input
            v-model="dialog.form.authNo"
            :disabled="dialog.isEdit"
            placeholder="统一认证号"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="dialog.form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="dialog.form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="密码" v-if="!dialog.isEdit">
          <el-input
            v-model="dialog.form.password"
            type="password"
            placeholder="登录密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="dialog.form.email" placeholder="邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="dialog.form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="dialog.form.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id' }"
            placeholder="选择部门"
            check-strictly
            clearable
            style="width: 100%"
          />
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

<script setup>
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
import { getDeptTree } from '@/api/system/dept'

const queryForm = reactive({ page: 1, size: 10, keyword: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const deptTree = ref([])

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
    authNo: '',
    realName: '',
    username: '',
    password: '',
    email: '',
    phone: '',
    deptId: null,
    status: 1
  }
})
const rules = {
  authNo: [{ required: true, message: '请输入认证号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

function handleAdd() {
  dialog.isEdit = false
  Object.assign(dialog.form, {
    id: null,
    authNo: '',
    realName: '',
    username: '',
    password: '',
    email: '',
    phone: '',
    deptId: null,
    status: 1
  })
  dialog.visible = true
}
function handleEdit(row) {
  dialog.isEdit = true
  Object.assign(dialog.form, { ...row, deptId: row.deptId || null })
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
  ElMessageBox.confirm(`确定删除「${row.realName || row.authNo}」?`, '确认', {
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
    deptTree.value = await getDeptTree()
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
