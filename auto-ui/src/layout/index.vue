<template>
  <div :class="classObj" class="app-wrapper" :style="{'--current-color': theme, 'background-color': 'white'}">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <!-- 只有当sidebar.hide为false且用户不是common角色时，才显示侧边栏 -->
    <sidebar 
      v-if="!sidebar.hide && !isCommonUser" 
      class="sidebar-container"
    />
    <!-- 当sidebar.hide为true或用户是common角色时，main-container的margin-left为0 -->
    <div :class="{hasTagsView:needTagsView,sidebarHide:sidebar.hide || isCommonUser}" class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar @setLayout="setLayout"/>
        <tags-view v-if="needTagsView"/>
      </div>
      <app-main/>
      <settings ref="settingRef"/>
    </div>
  </div>
</template>

<script>
import { AppMain, Navbar, Settings, Sidebar, TagsView } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    Settings,
    Sidebar,
    TagsView
  },
  mixins: [ResizeMixin],
  computed: {
    ...mapState({
      theme: state => state.settings.theme,
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    classObj() {
      return {
        hideSidebar: false,
        openSidebar: false,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables
    },
    // 判断当前用户是否是common角色
    isCommonUser() {
      const userRoles = this.$store.getters.roles || []
      return userRoles.includes('common')
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    setLayout() {
      this.$refs.settingRef.openSetting()
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;

    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
  }

  .main-container:has(.fixed-header) {
    height: 100vh;
    overflow: hidden;
  }

  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$base-sidebar-width});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px);
  }

  .sidebarHide .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }
  /* 为common角色用户隐藏侧边栏的兜底样式 */
  .sidebar-container.common-role-hidden {
    display: none !important;
    visibility: hidden !important;
    width: 0 !important;
    height: 0 !important;
    opacity: 0 !important;
  }
</style>
