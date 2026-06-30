<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>部门管理</h2>
          <el-button type="primary" @click="handleAdd(null)">新增顶级部门</el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        border
        stripe
        row-key="id"
        v-loading="loading"
        :tree-props="{ children: 'children' }"
        default-expand-all
      >
        <el-table-column prop="deptName" label="部门名称" min-width="180" />
        <el-table-column prop="deptCode" label="部门编码" width="160" />
        <el-table-column prop="ancestors" label="祖级" min-width="200" show-overflow-tooltip />
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
            <el-button type="success" link size="small" @click="handleAdd(row)"
              >添加子部门</el-button
            >
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialogTitle" width="480px">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="80px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="dialog.form.parentId"
            :data="tableData"
            :props="{ label: 'deptName', value: 'id' }"
            placeholder="不选则为顶级"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="dialog.form.deptName" placeholder="如: 上海研发部" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="dialog.form.deptCode" placeholder="如: SH_RD" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeptTree, createDept, updateDept, deleteDept } from '@/api/system/dept'

const tableData = ref([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    tableData.value = (await getDeptTree()) || []
  } finally {
    loading.value = false
  }
}

const formRef = ref(null)
const dialog = reactive({
  visible: false,
  isEdit: false,
  loading: false,
  form: { id: null, parentId: null, deptName: '', deptCode: '', sort: 0, status: 1 }
})
const dialogTitle = computed(() =>
  dialog.isEdit ? '编辑部门' : dialog.form.parentId ? '添加子部门' : '新增顶级部门'
)
const rules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }]
}

function handleAdd(parent) {
  dialog.isEdit = false
  Object.assign(dialog.form, {
    id: null,
    parentId: parent?.id || null,
    deptName: '',
    deptCode: '',
    sort: 0,
    status: 1
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
    const { children, ancestors, createTime, updateTime, deleted, ...data } = dialog.form
    data.parentId = data.parentId || 0
    dialog.isEdit ? await updateDept(data) : await createDept(data)
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
    hasChildren ? `「${row.deptName}」有子部门，确定删除?` : `确定删除「${row.deptName}」?`,
    '确认',
    { type: 'warning' }
  ).then(async () => {
    await deleteDept(row.id)
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
