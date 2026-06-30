<template>
  <el-dialog
    :model-value="visible"
    title="数据导入向导"
    width="850px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-steps :active="activeStep" align-center finish-status="success">
      <el-step title="上传文件" />
      <el-step title="导入模式" />
      <el-step title="字段映射" />
      <el-step title="预览确认" />
    </el-steps>

    <div class="step-content">
      <!-- Step 1: File Upload -->
      <div v-if="activeStep === 0" class="step-pane">
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :limit="1"
          :accept="'.csv,.txt,.xls,.xlsx'"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 .csv / .txt / .xls / .xlsx 格式，文件大小不超过 10MB
            </div>
          </template>
        </el-upload>
      </div>

      <!-- Step 2: Import Mode -->
      <div v-else-if="activeStep === 1" class="step-pane">
        <div class="mode-section">
          <h4>选择导入模式</h4>
          <el-radio-group v-model="importMode" size="large">
            <el-radio value="OVERWRITE" border class="mode-radio">
              <div class="mode-label">全量覆盖</div>
              <div class="mode-desc">删除该子任务下的所有旧数据，导入文件中的全部内容</div>
            </el-radio>
            <el-radio value="MERGE" border class="mode-radio">
              <div class="mode-label">Merge 合并</div>
              <div class="mode-desc">保留已有治理项的反馈信息，仅更新或新增文件中的内容</div>
            </el-radio>
          </el-radio-group>
        </div>
      </div>

      <!-- Step 3: Header Mapping -->
      <div v-else-if="activeStep === 2" class="step-pane">
        <div v-if="parseError" class="parse-error">
          <el-alert :title="parseError" type="warning" show-icon :closable="false" />
        </div>
        <div v-else>
          <p class="mapping-tip">
            请确认文件表头与系统字段的对应关系。可手动调整映射。
          </p>
          <el-table :data="headerMappings" border size="small" style="width: 100%">
            <el-table-column prop="header" label="文件表头" width="200" show-overflow-tooltip />
            <el-table-column label="映射字段" width="220">
              <template #default="{ row }">
                <el-select
                  v-model="row.mappedField"
                  placeholder="选择映射字段"
                  size="small"
                  style="width: 200px"
                >
                  <el-option label="-- 忽略此列 --" value="" />
                  <el-option
                    v-for="f in availableFields"
                    :key="f.value"
                    :label="f.label"
                    :value="f.value"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="预览（首行）" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">
                {{ row.sample || '-' }}
              </template>
            </el-table-column>
          </el-table>
          <div class="mapping-actions">
            <el-button type="primary" size="small" @click="applyPreview" :loading="previewLoading">
              预览匹配结果
            </el-button>
          </div>
        </div>
      </div>

      <!-- Step 4: Preview & Confirm -->
      <div v-else-if="activeStep === 3" class="step-pane">
        <p class="mapping-tip">
          以下为导入数据预览（前 20 行），请确认无误后点击「确认导入」。
        </p>
        <el-table :data="previewRows" border size="small" style="width: 100%" max-height="400">
          <el-table-column
            v-for="col in previewColumns"
            :key="col"
            :prop="col"
            :label="col"
            min-width="140"
            show-overflow-tooltip
          />
        </el-table>
        <div class="preview-summary">
          共解析 <strong>{{ totalRows }}</strong> 行数据
          <span v-if="totalRows > 20">（预览仅显示前 20 行）</span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="wizard-footer">
        <el-button @click="$emit('update:visible', false)">取消</el-button>
        <el-button v-if="activeStep > 0" @click="activeStep--">上一步</el-button>
        <el-button
          v-if="activeStep < 3"
          type="primary"
          :disabled="!canProceed"
          @click="goNext"
        >
          下一步
        </el-button>
        <el-button
          v-if="activeStep === 3"
          type="primary"
          :loading="importing"
          @click="confirmImport"
        >
          确认导入
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { importItems } from '@/api/tech-reform'

const props = defineProps({
  visible: { type: Boolean, default: false },
  subtaskId: { type: [String, Number], default: null }
})

const emit = defineEmits(['update:visible', 'imported'])

// Step state
const activeStep = ref(0)
const fileList = ref([])
const uploadRef = ref(null)
const selectedFile = ref(null)
const importMode = ref('OVERWRITE')

// CSV parse state
const parsedHeaders = ref([])
const parsedRows = ref([])
const totalRows = ref(0)
const parseError = ref('')
const previewRows = ref([])
const previewColumns = ref([])
const previewLoading = ref(false)
const importing = ref(false)

// Header mapping
const headerMappings = ref([])

// Available target fields for governance items
const availableFields = [
  { label: '应用名称', value: 'applicationName' },
  { label: '治理项', value: 'governanceItem' },
  { label: '问题描述', value: 'issueDescription' },
  { label: '治理计划', value: 'governancePlan' },
  { label: '责任人', value: 'responsiblePerson' },
  { label: '反馈', value: 'feedback' },
  { label: '状态', value: 'itemStatus' }
]

// Auto-map header names to field values (case-insensitive)
function autoMapField(header) {
  const h = header.trim().toLowerCase()
  const map = {
    '应用名称': 'applicationName', '应用': 'applicationName', 'app': 'applicationName',
    'application': 'applicationName', 'applicationname': 'applicationName',
    '治理项': 'governanceItem', 'governanceitem': 'governanceItem',
    '问题描述': 'issueDescription', '问题': 'issueDescription', '描述': 'issueDescription',
    'issuedescription': 'issueDescription', 'description': 'issueDescription',
    '治理计划': 'governancePlan', '计划': 'governancePlan', 'governanceplan': 'governancePlan',
    'plan': 'governancePlan',
    '责任人': 'responsiblePerson', '负责人': 'responsiblePerson', 'responsibleperson': 'responsiblePerson',
    'responsible': 'responsiblePerson', 'person': 'responsiblePerson',
    '反馈': 'feedback', 'feedback': 'feedback',
    '状态': 'itemStatus', 'itemstatus': 'itemStatus', 'status': 'itemStatus'
  }
  return map[h] || ''
}

// Parse file (client-side for CSV/TXT)
function parseCSV(text) {
  const lines = text.split(/\r?\n/).filter((line) => line.trim() !== '')
  if (lines.length === 0) {
    throw new Error('文件为空')
  }

  // Simple CSV parser: split by comma, handle quoted fields
  function parseLine(line) {
    const result = []
    let current = ''
    let inQuotes = false
    for (let i = 0; i < line.length; i++) {
      const ch = line[i]
      if (ch === '"') {
        inQuotes = !inQuotes
      } else if (ch === ',' && !inQuotes) {
        result.push(current.trim())
        current = ''
      } else {
        current += ch
      }
    }
    result.push(current.trim())
    return result
  }

  const headers = parseLine(lines[0])
  const rows = lines.slice(1).map(parseLine)

  return { headers, rows }
}

function handleFileChange(file) {
  fileList.value = [file]
  selectedFile.value = file.raw || file
  parseError.value = ''

  const fileName = file.name.toLowerCase()
  if (fileName.endsWith('.csv') || fileName.endsWith('.txt')) {
    const reader = new FileReader()
    reader.onload = (e) => {
      try {
        const text = e.target.result
        const { headers, rows } = parseCSV(text)
        parsedHeaders.value = headers
        parsedRows.value = rows
        totalRows.value = rows.length
        parseError.value = ''

        // Build header mapping table
        headerMappings.value = headers.map((h, idx) => ({
          header: h,
          mappedField: autoMapField(h),
          sample: rows.length > 0 ? (rows[0][idx] || '') : ''
        }))
      } catch (err) {
        parseError.value = '文件解析失败：' + err.message
        parsedHeaders.value = []
        parsedRows.value = []
        totalRows.value = 0
      }
    }
    reader.readAsText(selectedFile.value)
  } else if (fileName.endsWith('.xls') || fileName.endsWith('.xlsx')) {
    parseError.value = 'Excel 文件需要后端解析。请使用 CSV 格式，或直接点击下一步跳过字段映射预览。'
    parsedHeaders.value = []
    parsedRows.value = []
    totalRows.value = 0
    headerMappings.value = []
  }
}

function handleFileRemove() {
  fileList.value = []
  selectedFile.value = null
  parsedHeaders.value = []
  parsedRows.value = []
  totalRows.value = 0
  parseError.value = ''
  headerMappings.value = []
  previewRows.value = []
  previewColumns.value = []
}

function applyPreview() {
  previewLoading.value = true
  try {
    // Build columns from mapped fields (exclude empty mappings)
    const cols = []
    const colIndexes = []
    headerMappings.value.forEach((m, idx) => {
      if (m.mappedField) {
        cols.push(m.mappedField)
        colIndexes.push(idx)
      }
    })
    previewColumns.value = cols

    // Build preview rows: use mapped field names as keys, original data as values
    const preview = parsedRows.value.slice(0, 20).map((row) => {
      const obj = {}
      headerMappings.value.forEach((m, idx) => {
        if (m.mappedField) {
          obj[m.mappedField] = row[idx] || ''
        }
      })
      return obj
    })
    previewRows.value = preview
  } finally {
    previewLoading.value = false
  }
}

const canProceed = computed(() => {
  if (activeStep.value === 0) return !!selectedFile.value
  if (activeStep.value === 1) return !!importMode.value
  return true
})

function goNext() {
  if (activeStep.value === 2 && parsedHeaders.value.length > 0) {
    applyPreview()
  }
  activeStep.value++
}

async function confirmImport() {
  if (!selectedFile.value || !props.subtaskId) {
    ElMessage.warning('缺少文件或子任务信息')
    return
  }

  importing.value = true
  try {
    await importItems(props.subtaskId, selectedFile.value, importMode.value)
    ElMessage.success('导入成功')
    emit('update:visible', false)
    emit('imported')
  } catch {
    // Error handled by request interceptor
  } finally {
    importing.value = false
  }
}

// Reset state when dialog opens
watch(
  () => props.visible,
  (val) => {
    if (val) {
      activeStep.value = 0
      fileList.value = []
      selectedFile.value = null
      importMode.value = 'OVERWRITE'
      parsedHeaders.value = []
      parsedRows.value = []
      totalRows.value = 0
      parseError.value = ''
      headerMappings.value = []
      previewRows.value = []
      previewColumns.value = []
      nextTick(() => {
        uploadRef.value?.clearFiles()
      })
    }
  }
)
</script>

<style lang="scss" scoped>
.step-content {
  margin-top: 24px;
  min-height: 300px;
}

.step-pane {
  padding: 8px 0;
}

.mode-section {
  h4 {
    margin: 0 0 16px 0;
    font-size: 15px;
    font-weight: 600;
  }
}

.mode-radio {
  display: block;
  margin-bottom: 12px;
  padding: 12px 16px;
  height: auto;
  width: 100%;

  .mode-label {
    font-weight: 600;
    font-size: 14px;
  }

  .mode-desc {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}

.mapping-tip {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}

.mapping-actions {
  margin-top: 12px;
  text-align: right;
}

.parse-error {
  margin-bottom: 16px;
}

.preview-summary {
  margin-top: 12px;
  font-size: 13px;
  color: #606266;
  text-align: right;
}

.wizard-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
