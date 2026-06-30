<template>
  <div class="dashboard">
    <h2>首页工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>技术治理任务</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>数据库治理任务</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>数据库巡检任务</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>资产管理</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.dashboard { .stat { font-size: 28px; font-weight: bold; color: #409EFF; text-align: center; } }
</style>
