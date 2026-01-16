import store from '@/store'

const { body } = document
const WIDTH = 992 // refer to Bootstrap's responsive design

export default {
  watch: {
    $route(route) {
      // 禁用路由变化时自动关闭侧边栏
    }
  },
  beforeMount() {
    window.addEventListener('resize', this.$_resizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.$_resizeHandler)
  },
  mounted() {
    const isMobile = this.$_isMobile()
    if (isMobile) {
      store.dispatch('app/toggleDevice', 'mobile')
      // 禁用移动端自动关闭侧边栏
    }
  },
  methods: {
    // use $_ for mixins properties
    // https://vuejs.org/v2/style-guide/index.html#Private-property-names-essential
    $_isMobile() {
      const rect = body.getBoundingClientRect()
      return rect.width - 1 < WIDTH
    },
    $_resizeHandler() {
      if (!document.hidden) {
        const isMobile = this.$_isMobile()
        store.dispatch('app/toggleDevice', isMobile ? 'mobile' : 'desktop')

        // 禁用窗口大小变化时自动关闭侧边栏
      }
    }
  }
}
