<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑任务' : '新增任务'"
    width="600px"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="任务名称" prop="taskName">
        <el-input v-model="form.taskName" placeholder="请输入任务名称" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="任务大类" prop="taskCategory">
        <el-select v-model="form.taskCategory" placeholder="请选择任务大类" clearable style="width: 100%">
          <el-option
            v-for="item in categoryOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="任务小类" prop="taskSubcategory">
        <el-select v-model="form.taskSubcategory" placeholder="请选择任务小类" clearable style="width: 100%">
          <el-option
            v-for="item in subcategoryOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="任务来源" prop="taskSource">
        <el-select
          v-model="form.taskSource"
          placeholder="请选择或输入任务来源"
          clearable
          filterable
          allow-create
          style="width: 100%"
        >
          <el-option
            v-for="item in sourceOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="任务描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请输入任务描述、背景、目标等"
          maxlength="2000"
          show-word-limit
        />
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
      <el-form-item label="牵头人">
        <el-input v-model="form.taskOwner" disabled />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createTask, updateTask, getDictByType } from '@/api/tech-reform'
import { useUserStore } from '@/store/user'

interface Props {
  visible?: boolean
  task?: Record<string, any> | null
}
const props = withDefaults(defineProps<Props>(), {
  visible: false,
  task: null
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'saved'): void
}>()

const userStore = useUserStore()

const isEdit = computed(() => !!props.task?.id)

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  taskName: '',
  taskCategory: '',
  taskSubcategory: '',
  taskSource: '',
  description: '',
  startDate: '',
  endDate: '',
  taskOwner: ''
})

const rules = {
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
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
const categoryOptions = ref([])
const subcategoryOptions = ref([])
const sourceOptions = ref([])

async function loadDicts() {
  try {
    const [cat, subcat, src] = await Promise.all([
      getDictByType('task_category').catch(() => []),
      getDictByType('task_subcategory').catch(() => []),
      getDictByType('task_source').catch(() => [])
    ])
    categoryOptions.value = Array.isArray(cat) ? cat : []
    subcategoryOptions.value = Array.isArray(subcat) ? subcat : []
    sourceOptions.value = Array.isArray(src) ? src : []
  } catch (err) {
    // Dict load failure is non-fatal
  }
}

// Load dicts on mount
loadDicts()

// Watch visible to populate form
watch(
  () => props.visible,
  (val) => {
    if (val) {
      const defaultOwner =
        userStore.userInfo?.realName || userStore.userInfo?.username || userStore.userInfo?.authNo || ''
      if (props.task?.id) {
        // Edit mode: populate from task object
        Object.assign(form, {
          taskName: props.task.taskName || '',
          taskCategory: props.task.taskCategory || '',
          taskSubcategory: props.task.taskSubcategory || '',
          taskSource: props.task.taskSource || '',
          description: props.task.description || '',
          startDate: props.task.startDate || '',
          endDate: props.task.endDate || '',
          taskOwner: props.task.taskOwner || defaultOwner
        })
      } else {
        // Create mode: reset to defaults
        Object.assign(form, {
          taskName: '',
          taskCategory: '',
          taskSubcategory: '',
          taskSource: '',
          description: '',
          startDate: '',
          endDate: '',
          taskOwner: defaultOwner
        })
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
    const data = { ...form }
    if (isEdit.value) {
      await updateTask(props.task.id, data)
      ElMessage.success('更新成功')
    } else {
      await createTask(data)
      ElMessage.success('创建成功')
    }
    emit('update:visible', false)
    emit('saved')
  } finally {
    submitting.value = false
  }
}
</script>
