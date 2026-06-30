<template>
  <div class="dashboard">
    <div class="welcome">
      <h2>👋 欢迎回来，{{ userStore.userInfo?.realName || '用户' }}</h2>
      <p>{{ today }} · 技术管理平台</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-body">
            <div class="stat-icon" style="background: #e6f4ff">
              <el-icon :size="32" color="#1677ff"><Setting /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.techGovernance }}</div>
              <div class="stat-label">技术治理任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-body">
            <div class="stat-icon" style="background: #f6ffed">
              <el-icon :size="32" color="#52c41a"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.dbGovernance }}</div>
              <div class="stat-label">数据库治理任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-body">
            <div class="stat-icon" style="background: #fff7e6">
              <el-icon :size="32" color="#fa8c16"><Aim /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.dbInspection }}</div>
              <div class="stat-label">数据库巡检任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-body">
            <div class="stat-icon" style="background: #f9f0ff">
              <el-icon :size="32" color="#722ed1"><FolderOpened /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.assetApps }}</div>
              <div class="stat-label">应用资产</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待完成任务 + 图表 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-title">📋 我的待完成任务</div>
          </template>
          <el-table :data="pendingTasks" stripe size="small">
            <el-table-column prop="taskName" label="任务名称" min-width="180" />
            <el-table-column prop="module" label="所属模块" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deadline" label="截止日期" width="120" />
          </el-table>
          <div v-if="pendingTasks.length === 0" class="empty-state">暂无待完成任务 🎉</div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-title">📊 治理进度概览</div>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="48" color="#dcdfe6"><TrendCharts /></el-icon>
            <p>图表区域 — 待 ECharts 集成</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
})

// 统计数据（后续对接后端API）
const stats = reactive({
  techGovernance: 12,
  dbGovernance: 8,
  dbInspection: 5,
  assetApps: 24
})

// 待完成任务（后续对接后端API）
const pendingTasks = ref([])
// 示例数据:
// { taskName:'MySQL慢查询治理', module:'数据库治理', status:'进行中', deadline:'2026-07-15' }

function statusType(status) {
  return (
    { 待开始: 'info', 进行中: 'warning', 已完成: 'success', 已逾期: 'danger' }[status] || 'info'
  )
}
</script>

<style lang="scss" scoped>
.dashboard {
  max-width: 1400px;
}
.welcome {
  margin-bottom: 24px;
  h2 {
    margin: 0 0 4px;
    font-size: 22px;
  }
  p {
    margin: 0;
    color: #909399;
    font-size: 14px;
  }
}
.stat-row {
  margin-bottom: 20px;
}
.stat-card {
  .stat-body {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .stat-num {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    line-height: 1.2;
  }
  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 2px;
  }
}
.content-row {
  margin-bottom: 20px;
}
.card-title {
  font-weight: 600;
}
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #c0c4cc;
  p {
    margin-top: 12px;
    font-size: 14px;
  }
}
.empty-state {
  text-align: center;
  padding: 32px;
  color: #c0c4cc;
  font-size: 14px;
}
</style>
