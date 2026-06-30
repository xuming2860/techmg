<template>
  <div class="governance-detail">
    <!-- Subtask selector -->
    <div class="subtask-selector">
      <span class="selector-label">选择子任务：</span>
      <el-select
        v-model="selectedSubtaskId"
        filterable
        placeholder="请选择子任务查看治理清单"
        clearable
        style="width: 360px"
        @change="onSubtaskChange"
      >
        <el-option
          v-for="s in subtaskOptions"
          :key="s.id"
          :label="s.subtaskName"
          :value="s.id"
        />
      </el-select>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-button type="primary" size="small" @click="handleDownload" :disabled="!selectedSubtaskId">
        <el-icon><download /></el-icon> 下载清单
      </el-button>
      <el-upload
        :show-file-list="false"
        :before-upload="handleBatchUpload"
        accept=".csv,.txt,.xls,.xlsx"
        :disabled="!selectedSubtaskId"
        style="display: inline-block; margin-left: 8px"
      >
        <el-button type="success" size="small" :disabled="!selectedSubtaskId">
          <el-icon><upload /></el-icon> 批量上传登记
        </el-button>
      </el-upload>
      <el-button type="warning" size="small" :disabled="!selectedSubtaskId" @click="openImportWizard" style="margin-left: 8px">
        <el-icon><upload /></el-icon> 导入数据
      </el-button>
    </div>

    <!-- Filter -->
    <el-form :inline="true" :model="queryForm" class="search-bar">
      <el-form-item label="应用">
        <el-input
          v-model="queryForm.appName"
          placeholder="应用名称"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select
          v-model="queryForm.itemStatus"
          placeholder="全部"
          clearable
          style="width: 130px"
          @change="handleSearch"
        >
          <el-option
            v-for="s in itemStatusOptions"
            :key="s.value"
            :label="s.label"
            :value="s.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="搜索">
        <el-input
          v-model="queryForm.keyword"
          placeholder="治理项/问题描述"
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
      <el-table-column prop="applicationName" label="应用名称" width="140" show-overflow-tooltip />
      <el-table-column prop="governanceItem" label="治理项" width="160" show-overflow-tooltip />
      <el-table-column prop="issueDescription" label="问题描述" min-width="180" show-overflow-tooltip />
      <el-table-column label="治理计划" min-width="180">
        <template #default="{ row }">
          <div
            v-if="!row._editing || row._editField !== 'governancePlan'"
            class="editable-cell"
            @click="startEdit(row, 'governancePlan')"
          >
            <span v-if="row.governancePlan">{{ row.governancePlan }}</span>
            <span v-else class="placeholder">点击编辑</span>
          </div>
          <el-input
            v-else
            v-model="row._editValue"
            size="small"
            @blur="endEdit(row)"
            @keyup.enter="endEdit(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="责任人" width="100">
        <template #default="{ row }">
          <div
            v-if="!row._editing || row._editField !== 'responsiblePerson'"
            class="editable-cell"
            @click="startEdit(row, 'responsiblePerson')"
          >
            <span v-if="row.responsiblePerson">{{ row.responsiblePerson }}</span>
            <span v-else class="placeholder">点击编辑</span>
          </div>
          <el-input
            v-else
            v-model="row._editValue"
            size="small"
            @blur="endEdit(row)"
            @keyup.enter="endEdit(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="反馈" min-width="160">
        <template #default="{ row }">
          <div
            v-if="!row._editing || row._editField !== 'feedback'"
            class="editable-cell"
            @click="startEdit(row, 'feedback')"
          >
            <span v-if="row.feedback">{{ row.feedback }}</span>
            <span v-else class="placeholder">点击编辑</span>
          </div>
          <el-input
            v-else
            v-model="row._editValue"
            size="small"
            @blur="endEdit(row)"
            @keyup.enter="endEdit(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="itemStatusTagType(row.itemStatus)" size="small">
            {{ itemStatusLabel(row.itemStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="saveItem(row)" :loading="row._saving">
            保存
          </el-button>
          <el-button
            v-if="isPlatformAdmin"
            type="danger"
            link
            size="small"
            @click="handleDeleteItem(row)"
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

    <!-- Import Wizard -->
    <ImportWizard
      v-model:visible="importWizardVisible"
      :subtask-id="selectedSubtaskId"
      @imported="onImported"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Upload } from '@element-plus/icons-vue'
import { pageSubtasks, pageItems, updateItem, deleteItem, exportItems, batchUpdateItems } from '@/api/tech-reform'
import { useUserStore } from '@/store/user'
import ImportWizard from '@/components/TechReform/ImportWizard.vue'

const props = defineProps({
  defaultSubtaskId: { type: [String, Number], default: null }
})

const emit = defineEmits(['import-wizard-open'])

const userStore = useUserStore()

const isPlatformAdmin = computed(() =>
  userStore.roles.includes('ROLE_PLATFORM_ADMIN')
)

// Subtask options
const subtaskOptions = ref([])
const selectedSubtaskId = ref(null)

// Query form
const queryForm = reactive({
  page: 1,
  size: 10,
  appName: '',
  itemStatus: '',
  keyword: ''
})

// Table
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// Import wizard
const importWizardVisible = ref(false)

// Item status options
const itemStatusOptions = [
  { label: '待处理', value: 'PENDING' },
  { label: '处理中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已关闭', value: 'CLOSED' }
]

function itemStatusLabel(status) {
  const map = { PENDING: '待处理', IN_PROGRESS: '处理中', COMPLETED: '已完成', CLOSED: '已关闭' }
  return map[status] || status || '-'
}

function itemStatusTagType(status) {
  const map = { PENDING: 'info', IN_PROGRESS: '', COMPLETED: 'success', CLOSED: 'danger' }
  return map[status] || 'info'
}

// Load subtask options
async function loadSubtasks() {
  try {
    const res = await pageSubtasks({ page: 1, size: 100 })
    subtaskOptions.value = (res && res.records) ? res.records : []
  } catch {
    subtaskOptions.value = []
  }
}

// Fetch governance items
async function fetchData() {
  if (!selectedSubtaskId.value) {
    tableData.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    const params = {
      page: queryForm.page,
      size: queryForm.size,
      subtaskId: selectedSubtaskId.value
    }
    if (queryForm.appName) params.appName = queryForm.appName
    if (queryForm.itemStatus) params.itemStatus = queryForm.itemStatus
    if (queryForm.keyword) params.keyword = queryForm.keyword

    const res = await pageItems(params)
    const records = (res && res.records) ? res.records : []
    // Attach editing state
    records.forEach((r) => {
      r._editing = false
      r._editField = ''
      r._editValue = ''
      r._saving = false
    })
    tableData.value = records
    total.value = (res && res.total) || 0
  } finally {
    loading.value = false
  }
}

function onSubtaskChange() {
  queryForm.page = 1
  fetchData()
}

function handleSearch() {
  queryForm.page = 1
  fetchData()
}

function handleReset() {
  queryForm.appName = ''
  queryForm.itemStatus = ''
  queryForm.keyword = ''
  queryForm.page = 1
  fetchData()
}

// Inline edit
function startEdit(row, field) {
  // Save any other ongoing edit first
  tableData.value.forEach((r) => {
    if (r._editing && r !== row) {
      r[ r._editField ] = r._editValue
      r._editing = false
      r._editField = ''
    }
  })
  row._editing = true
  row._editField = field
  row._editValue = row[field] || ''
}

function endEdit(row) {
  if (row._editing) {
    row[row._editField] = row._editValue
    row._editing = false
    row._editField = ''
  }
}

async function saveItem(row) {
  endEdit(row)
  row._saving = true
  try {
    await updateItem(row.id, {
      governancePlan: row.governancePlan,
      responsiblePerson: row.responsiblePerson,
      feedback: row.feedback
    })
    ElMessage.success('保存成功')
  } finally {
    row._saving = false
  }
}

function handleDeleteItem(row) {
  ElMessageBox.confirm(`确定删除治理项「${row.governanceItem || row.applicationName}」?`, '确认', {
    type: 'warning'
  }).then(async () => {
    await deleteItem(row.id)
    ElMessage.success('已删除')
    fetchData()
  })
}

// Download
async function handleDownload() {
  if (!selectedSubtaskId.value) return
  try {
    const blob = await exportItems(selectedSubtaskId.value)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `治理清单_${selectedSubtaskId.value}.xlsx`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch {
    // Error handled by interceptor
  }
}

// Batch upload
async function handleBatchUpload(file) {
  if (!selectedSubtaskId.value) {
    ElMessage.warning('请先选择子任务')
    return false
  }
  try {
    await batchUpdateItems(selectedSubtaskId.value, file)
    ElMessage.success('批量上传成功')
    fetchData()
  } catch {
    // Error handled by interceptor
  }
  return false // Prevent default upload
}

function openImportWizard() {
  emit('import-wizard-open', selectedSubtaskId.value)
  importWizardVisible.value = true
}

function onImported() {
  fetchData()
}

// Expose method for parent to set subtask
function setSubtask(subtaskId) {
  selectedSubtaskId.value = subtaskId
  queryForm.page = 1
  fetchData()
}

// Expose import wizard
function showImportWizard(subtaskId) {
  if (subtaskId) selectedSubtaskId.value = subtaskId
  importWizardVisible.value = true
}

// Watch for defaultSubtaskId prop
watch(
  () => props.defaultSubtaskId,
  (val) => {
    if (val) {
      selectedSubtaskId.value = val
      queryForm.page = 1
      fetchData()
    }
  },
  { immediate: true }
)

onMounted(() => {
  loadSubtasks()
  if (props.defaultSubtaskId) {
    selectedSubtaskId.value = props.defaultSubtaskId
    fetchData()
  }
})

defineExpose({ setSubtask, showImportWizard, fetchData })
</script>

<style lang="scss" scoped>
.governance-detail {
  padding: 0;
}

.subtask-selector {
  display: flex;
  align-items: center;
  margin-bottom: 16px;

  .selector-label {
    font-weight: 600;
    margin-right: 8px;
    white-space: nowrap;
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

.editable-cell {
  cursor: pointer;
  min-height: 24px;
  padding: 2px 4px;
  border-radius: 4px;
  transition: background 0.2s;

  &:hover {
    background: #f0f2f5;
  }

  .placeholder {
    color: #c0c4cc;
    font-style: italic;
  }
}
</style>
