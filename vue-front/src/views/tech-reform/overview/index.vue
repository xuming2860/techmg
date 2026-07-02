<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>任务总览</h2>
          <el-button v-if="isPlatformAdmin" type="primary" @click="handleAdd">
            + 新增任务
          </el-button>
        </div>
      </template>

      <!-- Filter row -->
      <el-form :inline="true" :model="queryForm" class="search-bar">
        <el-form-item label="任务大类">
          <el-select
            v-model="queryForm.category"
            placeholder="全部"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryForm.status"
            placeholder="全部"
            clearable
            style="width: 130px"
            @change="handleSearch"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 250px"
            @change="handleSearch"
          />
        </el-form-item>
        <el-form-item label="搜索">
          <el-input
            v-model="queryForm.keyword"
            placeholder="任务名称/牵头人"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- Table -->
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="expand-content">
              <p v-if="row.description">
                <strong>任务描述：</strong>{{ row.description }}
              </p>
              <p v-else style="color: #999">暂无描述</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="taskName" label="任务名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="任务大类" width="120">
          <template #default="{ row }">
            {{ getDictLabel(categoryOptions, row.taskCategory) }}
          </template>
        </el-table-column>
        <el-table-column label="任务小类" width="120">
          <template #default="{ row }">
            {{ getDictLabel(subcategoryOptions, row.taskSubcategory) }}
          </template>
        </el-table-column>
        <el-table-column prop="taskSource" label="任务来源" width="120" show-overflow-tooltip />
        <el-table-column prop="taskOwner" label="牵头人" width="100" />
        <el-table-column label="起止日期" width="200">
          <template #default="{ row }">
            <span v-if="row.startDate || row.endDate">
              {{ row.startDate || '-' }} ~ {{ row.endDate || '-' }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="goToSubtask(row)">
              创建子任务
            </el-button>
            <el-button
              v-if="isPlatformAdmin"
              type="primary"
              link
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="isPlatformAdmin"
              :type="row.status === 'CLOSED' ? 'success' : 'warning'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'CLOSED' ? '启用' : '停用' }}
            </el-button>
            <el-button
              v-if="isPlatformAdmin"
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
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

    <!-- Task Form Dialog -->
    <TaskFormDialog
      v-model:visible="dialog.visible"
      :task="dialog.task"
      @saved="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageTasks, updateTaskStatus, deleteTask, getDictByType } from '@/api/tech-reform'
import { useUserStore } from '@/store/user'
import TaskFormDialog from '@/components/TechReform/TaskFormDialog.vue'

const router = useRouter()
const userStore = useUserStore()

const isPlatformAdmin = computed(() =>
  userStore.roles.includes('ROLE_PLATFORM_ADMIN')
)

// Query form
const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  category: '',
  status: '',
  dateRange: null
})

// Table data
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// Dict options
const categoryOptions = ref([])
const subcategoryOptions = ref([])

// Static status options (from ReformStatus enum)
const statusOptions = [
  { label: '待开始', value: 'PENDING' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已关闭', value: 'CLOSED' }
]

function getDictLabel(options, value) {
  if (!value) return '-'
  const found = options.find((o) => o.dictValue === value)
  return found ? found.dictLabel : value
}

function statusLabel(status) {
  const map = { PENDING: '待开始', IN_PROGRESS: '进行中', COMPLETED: '已完成', CLOSED: '已关闭' }
  return map[status] || status || '-'
}

function statusTagType(status) {
  const map = { PENDING: 'info', IN_PROGRESS: '', COMPLETED: 'success', CLOSED: 'danger' }
  return map[status] || 'info'
}

// Dialog
const dialog = reactive({
  visible: false,
  task: null
})

// Fetch data
async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      page: queryForm.page,
      size: queryForm.size
    }
    if (queryForm.keyword) params.keyword = queryForm.keyword
    if (queryForm.category) params.category = queryForm.category
    if (queryForm.status) params.status = queryForm.status
    // Date range filtering — backend expects these params if implemented;
    // pass as startDate/endDate if the API supports them, otherwise filter is cosmetic
    if (queryForm.dateRange && queryForm.dateRange.length === 2) {
      params.startDate = queryForm.dateRange[0]
      params.endDate = queryForm.dateRange[1]
    }

    const res = await pageTasks(params)
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
  queryForm.category = ''
  queryForm.status = ''
  queryForm.dateRange = null
  queryForm.page = 1
  fetchData()
}

// CRUD operations
function handleAdd() {
  dialog.task = null
  dialog.visible = true
}

function handleEdit(row) {
  dialog.task = { ...row }
  dialog.visible = true
}

function handleToggleStatus(row) {
  const newStatus = row.status === 'CLOSED' ? 'PENDING' : 'CLOSED'
  const actionLabel = newStatus === 'CLOSED' ? '停用' : '启用'
  ElMessageBox.confirm(`确定${actionLabel}任务「${row.taskName}」?`, '确认', {
    type: 'warning'
  }).then(async () => {
    await updateTaskStatus(row.id, newStatus)
    ElMessage.success(`已${actionLabel}`)
    fetchData()
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除任务「${row.taskName}」?`, '确认', {
    type: 'warning'
  }).then(async () => {
    await deleteTask(row.id)
    ElMessage.success('已删除')
    fetchData()
  })
}

function goToSubtask(row) {
  router.push({ name: '子任务管理', query: { parentTaskId: row.id } })
}

// Init
onMounted(async () => {
  try {
    const [cat, subcat] = await Promise.all([
      getDictByType('task_category').catch(() => []),
      getDictByType('task_subcategory').catch(() => [])
    ])
    categoryOptions.value = Array.isArray(cat) ? cat : []
    subcategoryOptions.value = Array.isArray(subcat) ? subcat : []
  } catch (err) {
    // Non-fatal
  }
  fetchData()
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
    font-weight: 600;
  }
}
.search-bar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.expand-content {
  padding: 8px 16px;
  p {
    margin: 0;
    line-height: 1.7;
  }
}
</style>
