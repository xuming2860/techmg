<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>子任务管理</h2>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <!-- Tab 1: 子任务详情 -->
        <el-tab-pane label="子任务详情" name="subtask">
          <!-- Toolbar -->
          <div class="toolbar">
            <el-button
              v-if="canCreate"
              type="primary"
              size="small"
              @click="handleAdd"
            >
              + 新建子任务
            </el-button>
          </div>

          <!-- Filter -->
          <el-form :inline="true" :model="queryForm" class="search-bar">
            <el-form-item label="父任务">
              <el-select
                v-model="queryForm.parentTaskId"
                filterable
                remote
                reserve-keyword
                placeholder="搜索父任务"
                :remote-method="searchParentTasks"
                :loading="parentTaskLoading"
                clearable
                style="width: 200px"
                @change="handleSearch"
              >
                <el-option
                  v-for="t in parentTaskOptions"
                  :key="t.id"
                  :label="t.taskName"
                  :value="t.id"
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
                  v-for="s in statusOptions"
                  :key="s.value"
                  :label="s.label"
                  :value="s.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="搜索">
              <el-input
                v-model="queryForm.keyword"
                placeholder="子任务名称"
                clearable
                style="width: 200px"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
              <el-button size="small" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- Table -->
          <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
            <el-table-column prop="subtaskName" label="子任务名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="parentTaskName" label="父任务" width="140" show-overflow-tooltip />
            <el-table-column label="数据库类型" width="150">
              <template #default="{ row }">
                <template v-if="row.dbTypes && row.dbTypes.length">
                  <el-tag
                    v-for="(t, i) in row.dbTypes"
                    :key="i"
                    size="small"
                    style="margin-right: 4px; margin-bottom: 2px"
                  >
                    {{ getDictLabel(dbTypeOptions, t) }}
                  </el-tag>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="数据来源" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="dataSourceTagType(row.dataSource)">
                  {{ dataSourceLabel(row.dataSource) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="涉及部门" width="140">
              <template #default="{ row }">
                <template v-if="row.departments && row.departments.length">
                  <el-tag
                    v-for="(d, i) in row.departments.slice(0, 2)"
                    :key="i"
                    size="small"
                    style="margin-right: 4px; margin-bottom: 2px"
                  >
                    {{ d }}
                  </el-tag>
                  <el-tag v-if="row.departments.length > 2" size="small">+{{ row.departments.length - 2 }}</el-tag>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="应用范围" width="110">
              <template #default="{ row }">
                {{ appScopeLabel(row.appScope) }}
              </template>
            </el-table-column>
            <el-table-column label="起止日期" width="200">
              <template #default="{ row }">
                <span v-if="row.startDate || row.endDate">
                  {{ row.startDate || '-' }} ~ {{ row.endDate || '-' }}
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">
                  {{ statusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="300" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="goToGovernance(row)">
                  查看治理详情
                </el-button>
                <el-button
                  v-if="canCreate"
                  type="primary"
                  link
                  size="small"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  v-if="canCreate"
                  type="warning"
                  link
                  size="small"
                  @click="handleStatusChange(row)"
                >
                  状态变更
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
            @change="fetchSubtasks"
          />
        </el-tab-pane>

        <!-- Tab 2: 治理清单详情 -->
        <el-tab-pane label="治理清单详情" name="governance">
          <GovernanceDetail
            ref="governanceRef"
            :default-subtask-id="routeSubtaskId"
            @import-wizard-open="onImportWizardOpenFromGovernance"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Subtask Form Dialog -->
    <SubtaskFormDialog
      v-model:visible="dialog.visible"
      :subtask="dialog.subtask"
      :default-parent-task-id="defaultParentTaskId"
      :default-parent-task-name="defaultParentTaskName"
      @saved="onSubtaskSaved"
      @need-import="onNeedImport"
    />

    <!-- Import Wizard -->
    <ImportWizard
      v-model:visible="importWizardVisible"
      :subtask-id="importSubtaskId"
      @imported="onImported"
    />

    <!-- Status Change Dialog -->
    <el-dialog v-model="statusDialog.visible" title="状态变更" width="400px">
      <el-form label-width="80px">
        <el-form-item label="新状态">
          <el-select v-model="statusDialog.newStatus" placeholder="请选择状态" style="width: 100%">
            <el-option
              v-for="s in statusOptions"
              :key="s.value"
              :label="s.label"
              :value="s.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="statusDialog.loading" @click="confirmStatusChange">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageSubtasks, updateSubtaskStatus, deleteSubtask, pageTasks, getDictByType
} from '@/api/tech-reform'
import { useUserStore } from '@/store/user'
import SubtaskFormDialog from '@/components/TechReform/SubtaskFormDialog.vue'
import ImportWizard from '@/components/TechReform/ImportWizard.vue'
import GovernanceDetail from './GovernanceDetail.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isPlatformAdmin = computed(() =>
  userStore.roles.includes('ROLE_PLATFORM_ADMIN')
)
const isDeptAdmin = computed(() =>
  userStore.roles.includes('ROLE_DEPT_ADMIN')
)
const canCreate = computed(() =>
  isPlatformAdmin.value || isDeptAdmin.value
)

// Tab state
const activeTab = ref('subtask')
const governanceRef = ref(null)

// Route query-driven state
const routeSubtaskId = computed(() => {
  const id = route.query.subtaskId
  return id ? (isNaN(Number(id)) ? id : Number(id)) : null
})
const defaultParentTaskId = computed(() => {
  const id = route.query.parentTaskId
  return id ? (isNaN(Number(id)) ? id : Number(id)) : null
})
const defaultParentTaskName = computed(() => route.query.parentTaskName || '')

// Query form for Tab1
const queryForm = reactive({
  page: 1,
  size: 10,
  parentTaskId: null,
  status: '',
  keyword: ''
})

// Table data
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// Parent task remote search
const parentTaskLoading = ref(false)
const parentTaskOptions = ref([])

// Dict options
const dbTypeOptions = ref([])

// Static status options
const statusOptions = [
  { label: '待开始', value: 'PENDING' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已关闭', value: 'CLOSED' }
]

// Status change dialog
const statusDialog = reactive({
  visible: false,
  loading: false,
  subtask: null,
  newStatus: ''
})

// Subtask form dialog
const dialog = reactive({
  visible: false,
  subtask: null
})

// Import wizard
const importWizardVisible = ref(false)
const importSubtaskId = ref(null)

// Label helpers
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

function dataSourceLabel(ds) {
  const map = { FILE_IMPORT: '文件导入', MANUAL: '手工录入', NONE: '无数据' }
  return map[ds] || ds || '-'
}

function dataSourceTagType(ds) {
  const map = { FILE_IMPORT: 'primary', MANUAL: 'success', NONE: 'info' }
  return map[ds] || 'info'
}

function appScopeLabel(scope) {
  const map = { ALL: '全部应用', KEY: '重点应用', GOVERNANCE_LIST: '治理清单' }
  return map[scope] || scope || '-'
}

// Remote search parent tasks
let searchTimer = null
function searchParentTasks(keyword) {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    parentTaskLoading.value = true
    try {
      const params = { page: 1, size: 20 }
      if (keyword) params.keyword = keyword
      const res = await pageTasks(params)
      parentTaskOptions.value = (res && res.records) ? res.records : []
    } catch {
      parentTaskOptions.value = []
    } finally {
      parentTaskLoading.value = false
    }
  }, 300)
}

// Fetch subtasks
async function fetchSubtasks() {
  loading.value = true
  try {
    const params = {
      page: queryForm.page,
      size: queryForm.size
    }
    if (queryForm.parentTaskId) params.parentTaskId = queryForm.parentTaskId
    if (queryForm.status) params.status = queryForm.status
    if (queryForm.keyword) params.keyword = queryForm.keyword

    const res = await pageSubtasks(params)
    const records = (res && res.records) ? res.records : []
    // Parse JSON string fields from backend into arrays for display
    records.forEach((r) => {
      if (typeof r.departments === 'string') {
        try { r.departments = JSON.parse(r.departments) } catch { r.departments = [] }
      }
      if (!Array.isArray(r.departments)) r.departments = []
      if (typeof r.dbTypes === 'string') {
        try { r.dbTypes = JSON.parse(r.dbTypes) } catch { r.dbTypes = [] }
      }
      if (!Array.isArray(r.dbTypes)) r.dbTypes = []
    })
    tableData.value = records
    total.value = (res && res.total) || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchSubtasks()
}

function handleReset() {
  queryForm.parentTaskId = null
  queryForm.status = ''
  queryForm.keyword = ''
  queryForm.page = 1
  fetchSubtasks()
}

// CRUD
function handleAdd() {
  dialog.subtask = null
  dialog.visible = true
}

function handleEdit(row) {
  dialog.subtask = { ...row }
  dialog.visible = true
}

function onSubtaskSaved() {
  fetchSubtasks()
}

function handleStatusChange(row) {
  statusDialog.subtask = row
  statusDialog.newStatus = row.status || ''
  statusDialog.visible = true
}

async function confirmStatusChange() {
  if (!statusDialog.subtask || !statusDialog.newStatus) return
  statusDialog.loading = true
  try {
    await updateSubtaskStatus(statusDialog.subtask.id, statusDialog.newStatus)
    ElMessage.success('状态变更成功')
    statusDialog.visible = false
    fetchSubtasks()
  } finally {
    statusDialog.loading = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除子任务「${row.subtaskName}」?`, '确认', {
    type: 'warning'
  }).then(async () => {
    await deleteSubtask(row.id)
    ElMessage.success('已删除')
    fetchSubtasks()
  })
}

// Tab navigation
function goToGovernance(row) {
  activeTab.value = 'governance'
  updateRouteQuery({ subtaskId: row.id, tab: 'governance' })
  // Wait for tab switch and component render, then set subtask
  setTimeout(() => {
    governanceRef.value?.setSubtask(row.id)
  }, 100)
}

function onTabChange(tabName) {
  updateRouteQuery({ tab: tabName })
}

// Import wizard handlers
function onNeedImport(subtaskId) {
  importSubtaskId.value = subtaskId
  importWizardVisible.value = true
}

function onImportWizardOpenFromGovernance(subtaskId) {
  importSubtaskId.value = subtaskId
  importWizardVisible.value = true
}

function onImported() {
  fetchSubtasks()
  if (activeTab.value === 'governance') {
    governanceRef.value?.fetchData()
  }
}

// Route query sync
function updateRouteQuery(query) {
  const current = { ...route.query }
  let changed = false
  for (const key of Object.keys(query)) {
    if (String(current[key]) !== String(query[key])) {
      current[key] = query[key]
      changed = true
    }
  }
  if (changed) {
    router.replace({ query: current })
  }
}

// Init
onMounted(async () => {
  // Load dicts
  try {
    const types = await getDictByType('subtask_db_type').catch(() => [])
    dbTypeOptions.value = Array.isArray(types) ? types : []
  } catch {
    // Non-fatal
  }

  // Pre-populate parent task filter if coming from overview
  if (defaultParentTaskId.value) {
    queryForm.parentTaskId = defaultParentTaskId.value
    try {
      const res = await pageTasks({ page: 1, size: 20, keyword: defaultParentTaskName.value || '' })
      if (res && res.records) {
        parentTaskOptions.value = res.records
      }
    } catch {
      // Non-fatal
    }
  }

  // Set active tab from route query
  if (route.query.tab === 'governance') {
    activeTab.value = 'governance'
  }

  fetchSubtasks()
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

.toolbar {
  margin-bottom: 12px;
}

.search-bar {
  margin-bottom: 12px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
