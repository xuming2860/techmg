<template>
  <template v-if="menu.children && menu.children.length > 0 && menu.type !== 2">
    <el-sub-menu :index="String(menu.id)">
      <template #title>
        <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
        <span>{{ menu.menuName }}</span>
      </template>
      <menu-item v-for="child in menu.children" :key="child.id" :menu="child" />
    </el-sub-menu>
  </template>
  <template v-else-if="menu.type === 1 && menu.path">
    <el-menu-item :index="menu.path">
      <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
      <span>{{ menu.menuName }}</span>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
interface MenuItemData {
  id: number
  menuName: string
  type: number
  icon?: string
  path?: string
  children?: MenuItemData[]
}
interface Props {
  menu: MenuItemData
}
defineProps<Props>()
</script>
