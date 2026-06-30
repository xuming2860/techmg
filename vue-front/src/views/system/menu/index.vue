<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>菜单管理</h2>
          <el-button type="primary" @click="handleAdd(null)">新增顶级菜单</el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        border
        stripe
        row-key="id"
        v-loading="loading"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        default-expand-all
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.type === 0 ? '' : row.type === 1 ? 'success' : 'info'">
              {{ { 0: '目录', 1: '菜单', 2: '按钮' }[row.type] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="160" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" width="200" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" width="160" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="可见" width="60">
          <template #default="{ row }">
            <el-tag :type="row.visible ? 'success' : 'info'" size="small">{{
              row.visible ? '是' : '否'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              type="success"
              link
              size="small"
              @click="handleAdd(row)"
              v-if="row.type !== 2"
              >添加子项</el-button
            >
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialogTitle" width="560px">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="80px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="dialog.form.parentId"
            :data="tableData"
            :props="{ label: 'menuName', value: 'id' }"
            placeholder="不选则为顶级"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="dialog.form.menuName" placeholder="如: 用户管理" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="dialog.form.type">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" v-if="dialog.form.type === 1">
          <el-input v-model="dialog.form.path" placeholder="/system/user" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="dialog.form.type === 1">
          <el-input v-model="dialog.form.component" placeholder="views/system/user/index" />
        </el-form-item>
        <el-form-item label="图标" v-if="dialog.form.type !== 2">
          <el-input v-model="dialog.form.icon" placeholder="HomeFilled" />
        </el-form-item>
        <el-form-item label="权限标识" v-if="dialog.form.type === 2">
          <el-input v-model="dialog.form.perms" placeholder="system:user:delete" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dialog.form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch v-model="dialog.form.visible" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="dialog.loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/system/menu'

const tableData = ref([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    tableData.value = (await getMenuTree()) || []
  } finally {
    loading.value = false
  }
}

const formRef = ref(null)
const dialog = reactive({
  visible: false,
  isEdit: false,
  loading: false,
  form: {
    id: null,
    parentId: null,
    menuName: '',
    type: 1,
    path: '',
    component: '',
    icon: '',
    perms: '',
    sort: 0,
    status: 1,
    visible: 1
  }
})
const dialogTitle = computed(() =>
  dialog.isEdit ? '编辑菜单' : dialog.form.parentId ? '添加子菜单' : '新增顶级菜单'
)
const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

function handleAdd(parent) {
  dialog.isEdit = false
  Object.assign(dialog.form, {
    id: null,
    parentId: parent?.id || null,
    menuName: '',
    type: parent ? 1 : 0,
    path: '',
    component: '',
    icon: '',
    perms: '',
    sort: 0,
    status: 1,
    visible: 1
  })
  dialog.visible = true
}
function handleEdit(row) {
  dialog.isEdit = true
  Object.assign(dialog.form, { ...row, parentId: row.parentId || null })
  dialog.visible = true
}
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.loading = true
  try {
    const { children, createTime, updateTime, deleted, ...data } = dialog.form
    data.parentId = data.parentId || 0
    if (data.type === 0) {
      data.path = ''
      data.component = ''
    }
    dialog.isEdit ? await updateMenu(data) : await createMenu(data)
    ElMessage.success(dialog.isEdit ? '更新成功' : '创建成功')
    dialog.visible = false
    fetchData()
  } finally {
    dialog.loading = false
  }
}
function handleDelete(row) {
  const hasChildren = row.children && row.children.length > 0
  ElMessageBox.confirm(
    hasChildren
      ? `「${row.menuName}」有子菜单，删除后子菜单也将无法访问。确定删除?`
      : `确定删除「${row.menuName}」?`,
    '确认',
    { type: 'warning' }
  ).then(async () => {
    await deleteMenu(row.id)
    ElMessage.success('已删除')
    fetchData()
  })
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
</style>
