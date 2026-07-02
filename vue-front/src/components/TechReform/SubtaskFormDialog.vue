<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑子任务' : '新增子任务'"
    width="650px"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="父任务" prop="parentTaskId">
        <el-select
          v-model="form.parentTaskId"
          filterable
          remote
          reserve-keyword
          placeholder="搜索并选择父任务"
          :remote-method="searchParentTasks"
          :loading="parentTaskLoading"
          clearable
          style="width: 100%"
          @change="onParentTaskChange"
        >
          <el-option
            v-for="t in parentTaskOptions"
            :key="t.id"
            :label="t.taskName"
            :value="t.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item
        v-if="selectedParentTask && selectedParentTask.taskCategory === '数据库治理'"
        label="数据库类型"
        prop="dbTypes"
      >
        <el-select
          v-model="form.dbTypes"
          multiple
          placeholder="请选择数据库类型"
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="item in dbTypeOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="子任务名称" prop="subtaskName">
        <el-input
          v-model="form.subtaskName"
          placeholder="请输入子任务名称"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="任务描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入子任务描述"
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="数据来源" prop="dataSource">
        <el-radio-group v-model="form.dataSource">
          <el-radio value="FILE_IMPORT">文件导入</el-radio>
          <el-radio value="MANUAL">手工录入</el-radio>
          <el-radio value="NONE">无数据</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="涉及部门" prop="departments">
        <el-select
          v-model="form.departments"
          multiple
          placeholder="请选择涉及部门"
          clearable
          style="width: 100%"
        >
          <el-option label="见治理清单" value="GOVERNANCE_LIST" />
          <el-option
            v-for="d in deptOptions"
            :key="d.id"
            :label="d.label"
            :value="d.label"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="应用范围" prop="appScope">
        <el-select v-model="form.appScope" placeholder="请选择应用范围" style="width: 100%">
          <el-option label="全部应用" value="ALL" />
          <el-option label="重点应用" value="KEY" />
          <el-option label="治理清单应用" value="GOVERNANCE_LIST" />
        </el-select>
      </el-form-item>

      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="form.startDate"
          type="date"
          placeholder="选择开始日期"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="form.endDate"
          type="date"
          placeholder="选择结束日期"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="涉及负资产">
        <el-switch v-model="form.affectNegativeAsset" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { createSubtask, updateSubtask, pageTasks, getDictByType } from '@/api/tech-reform'
import { useUserStore } from '@/store/user'

interface Props {
  visible?: boolean
  subtask?: Record<string, any> | null
  defaultParentTaskId?: string | number | null
  defaultParentTaskName?: string
}
const props = withDefaults(defineProps<Props>(), {
  visible: false,
  subtask: null,
  defaultParentTaskId: null,
  defaultParentTaskName: ''
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'saved'): void
  (e: 'needImport', subtaskId: string | number): void
}>()

const userStore = useUserStore()

const isEdit = computed(() => !!props.subtask?.id)

const formRef = ref(null)
const submitting = ref(false)
const selectedParentTask = ref(null)
const parentTaskLoading = ref(false)
const parentTaskOptions = ref([])

const form = reactive({
  parentTaskId: null,
  subtaskName: '',
  description: '',
  dataSource: 'NONE',
  departments: [],
  appScope: 'ALL',
  dbTypes: [],
  startDate: '',
  endDate: '',
  affectNegativeAsset: false
})

const rules = {
  parentTaskId: [{ required: true, message: '请选择父任务', trigger: 'change' }],
  subtaskName: [{ required: true, message: '请输入子任务名称', trigger: 'blur' }],
  startDate: [
    {
      validator: (_rule, value, callback) => {
        if (value && form.endDate && value > form.endDate) {
          callback(new Error('开始日期不能晚于结束日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  endDate: [
    {
      validator: (_rule, value, callback) => {
        if (value && form.startDate && value < form.startDate) {
          callback(new Error('结束日期不能早于开始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// Dict options
const dbTypeOptions = ref([])

// Flattened department tree for select
const deptOptions = ref([])

// Safe parse: if value is a JSON string, parse it; otherwise return as-is.
// Handle both string and already-parsed array cases from the API response.
function safeParseArray(value) {
  if (Array.isArray(value)) return value
  if (typeof value === 'string') {
    try {
      const parsed = JSON.parse(value)
      return Array.isArray(parsed) ? parsed : []
    } catch (err) {
      return []
    }
  }
  return []
}

async function loadOptions() {
  try {
    const dbTypes = await getDictByType('subtask_db_type').catch(() => [])
    dbTypeOptions.value = Array.isArray(dbTypes) ? dbTypes : []
  } catch (err) {
    // Non-fatal
  }
}

loadOptions()

// Remote search for parent tasks
let searchTimer = null
async function searchParentTasks(keyword) {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    parentTaskLoading.value = true
    try {
      const params: Record<string, any> = { page: 1, size: 20 }
      if (keyword) params.keyword = keyword
      const res = await pageTasks(params)
      parentTaskOptions.value = (res && res.records) ? res.records : []
    } catch (err) {
      parentTaskOptions.value = []
    } finally {
      parentTaskLoading.value = false
    }
  }, 300)
}

function onParentTaskChange(taskId) {
  if (!taskId) {
    selectedParentTask.value = null
    return
  }
  const task = parentTaskOptions.value.find((t) => t.id === taskId)
  selectedParentTask.value = task || null
  if (task && !isEdit.value) {
    if (!form.subtaskName || form.subtaskName === '' || form.subtaskName.endsWith('—子任务')) {
      form.subtaskName = `${task.taskName}—子任务`
    }
    form.startDate = task.startDate || ''
    form.endDate = task.endDate || ''
  }
}

// Watch visible to populate form
watch(
  () => props.visible,
  async (val) => {
    if (val) {
      if (props.subtask?.id) {
        // Edit mode: populate from subtask object
        Object.assign(form, {
          parentTaskId: props.subtask.parentTaskId || null,
          subtaskName: props.subtask.subtaskName || '',
          description: props.subtask.description || '',
          dataSource: props.subtask.dataSource || 'NONE',
          departments: safeParseArray(props.subtask.departments),
          appScope: props.subtask.appScope || 'ALL',
          dbTypes: safeParseArray(props.subtask.dbTypes),
          startDate: props.subtask.startDate || '',
          endDate: props.subtask.endDate || '',
          affectNegativeAsset: props.subtask.affectNegativeAsset || false
        })
        // Load parent task info for conditional dbTypes visibility
        if (props.subtask.parentTaskId) {
          try {
            const res = await pageTasks({ page: 1, size: 1 })
            const all = (res && res.records) ? res.records : []
            const found = all.find((t) => t.id === props.subtask.parentTaskId)
            if (found) {
              selectedParentTask.value = found
              parentTaskOptions.value = [found]
            }
          } catch (err) {
            // Non-fatal
          }
        }
      } else {
        // Create mode: reset to defaults
        Object.assign(form, {
          parentTaskId: props.defaultParentTaskId || null,
          subtaskName: props.defaultParentTaskName ? `${props.defaultParentTaskName}—子任务` : '',
          description: '',
          dataSource: 'NONE',
          departments: [],
          appScope: 'ALL',
          dbTypes: [],
          startDate: '',
          endDate: '',
          affectNegativeAsset: false
        })
        selectedParentTask.value = null
        // If defaultParentTaskId provided, try to load its info
        if (props.defaultParentTaskId && props.defaultParentTaskName) {
          selectedParentTask.value = {
            id: props.defaultParentTaskId,
            taskName: props.defaultParentTaskName
          }
          // Try to fetch full task info for category and dates
          try {
            const res = await pageTasks({ page: 1, size: 1, keyword: props.defaultParentTaskName })
            const all = (res && res.records) ? res.records : []
            const found = all.find((t) => t.id === Number(props.defaultParentTaskId) || t.id === props.defaultParentTaskId)
            if (found) {
              selectedParentTask.value = found
              form.startDate = found.startDate || ''
              form.endDate = found.endDate || ''
            }
          } catch (err) {
            // Non-fatal
          }
        }
        // Pre-populate parent task options for the dropdown
        if (props.defaultParentTaskId && props.defaultParentTaskName) {
          parentTaskOptions.value = [{
            id: props.defaultParentTaskId,
            taskName: props.defaultParentTaskName
          }]
        } else {
          parentTaskOptions.value = []
        }
      }
      formRef.value?.clearValidate()
    }
  }
)

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      parentTaskId: form.parentTaskId,
      subtaskName: form.subtaskName,
      description: form.description,
      dataSource: form.dataSource,
      departments: JSON.stringify(form.departments || []),
      appScope: form.appScope,
      dbTypes: JSON.stringify(form.dbTypes || []),
      startDate: form.startDate,
      endDate: form.endDate,
      affectNegativeAsset: form.affectNegativeAsset
    }

    let savedId = null
    if (isEdit.value) {
      await updateSubtask(props.subtask.id, data)
      ElMessage.success('更新成功')
      savedId = props.subtask.id
    } else {
      const result = await createSubtask(data)
      ElMessage.success('创建成功')
      // result may contain the new subtask id
      savedId = result?.id || result
    }

    emit('update:visible', false)
    emit('saved')

    // If dataSource is FILE_IMPORT, emit needImport to open ImportWizard
    if (form.dataSource === 'FILE_IMPORT' && savedId) {
      emit('needImport', typeof savedId === 'object' ? savedId.id : savedId)
    }
  } finally {
    submitting.value = false
  }
}

onBeforeUnmount(() => {
  if (searchTimer) clearTimeout(searchTimer)
})
</script>
