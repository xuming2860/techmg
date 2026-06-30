<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>角色管理</h2>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleCode" label="编码" width="160" />
        <el-table-column prop="description" label="描述" min-width="240" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleMenu(row)">菜单</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total,sizes,prev,pager,next,jumper"
        v-model:current-page="page"
        v-model:page-size="size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        @change="fetchData"
      />
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.isEdit ? '编辑角色' : '新增角色'"
      width="480px"
    >
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="dialog.form.roleName" placeholder="如: 平台管理员" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input
            v-model="dialog.form.roleCode"
            :disabled="dialog.isEdit"
            placeholder="如: PLATFORM_ADMIN"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dialog.form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sort" :min="0" />
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

    <!-- 菜单分配弹窗 -->
    <el-dialog v-model="menuDialog.visible" title="分配菜单" width="400px">
      <el-tree
        ref="menuTreeRef"
        :data="menuDialog.tree"
        show-checkbox
        node-key="id"
        :default-checked-keys="menuDialog.checkedIds"
        :props="{ label: 'menuName', children: 'children' }"
        default-expand-all
        check-strictly
        v-loading="menuDialog.loading"
      />
      <template #footer>
        <el-button @click="menuDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit" :loading="menuDialog.loading"
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
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  getRoleMenus,
  assignRoleMenus
} from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = ref(10)

async function fetchData() {
  loading.value = true
  try {
    const res = await getRoleList({ page: page.value, size: size.value })
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const formRef = ref(null)
const dialog = reactive({
  visible: false,
  isEdit: false,
  loading: false,
  form: { id: null, roleName: '', roleCode: '', description: '', sort: 0, status: 1 }
})
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

function handleAdd() {
  dialog.isEdit = false
  Object.assign(dialog.form, {
    id: null,
    roleName: '',
    roleCode: '',
    description: '',
    sort: 0,
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
    dialog.isEdit ? await updateRole(data) : await createRole(data)
    ElMessage.success(dialog.isEdit ? '更新成功' : '创建成功')
    dialog.visible = false
    fetchData()
  } finally {
    dialog.loading = false
  }
}
function handleDelete(row) {
  ElMessageBox.confirm(`确定删除「${row.roleName}」?`, '确认', { type: 'warning' }).then(
    async () => {
      await deleteRole(row.id)
      ElMessage.success('已删除')
      fetchData()
    }
  )
}

const menuTreeRef = ref(null)
const menuDialog = reactive({
  visible: false,
  loading: false,
  roleId: null,
  tree: [],
  checkedIds: []
})
async function handleMenu(row) {
  menuDialog.roleId = row.id
  menuDialog.visible = true
  menuDialog.loading = true
  try {
    const [tree, menus] = await Promise.all([getMenuTree(), getRoleMenus(row.id)])
    menuDialog.tree = tree || []
    menuDialog.checkedIds = (menus || []).map(m => m.id || m.menuId)
  } finally {
    menuDialog.loading = false
  }
}
async function handleMenuSubmit() {
  menuDialog.loading = true
  try {
    const checked = menuTreeRef.value.getCheckedKeys()
    const halfChecked = menuTreeRef.value.getHalfCheckedKeys()
    await assignRoleMenus(menuDialog.roleId, [...checked, ...halfChecked])
    ElMessage.success('菜单分配成功')
    menuDialog.visible = false
  } finally {
    menuDialog.loading = false
  }
}

onMounted(() => fetchData())
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
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
