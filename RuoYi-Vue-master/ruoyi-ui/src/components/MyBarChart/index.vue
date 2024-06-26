<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
import resize from "@/views/dashboard/mixins/resize";
require('echarts/theme/macarons') // echarts theme

const animationDuration = 6000

export default {
  name: "MyBarChart",
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.chart = echarts.init(this.$el, 'macarons')
      this.initChart();
    })
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({conditions, coverageData} = {}) {
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: conditions,
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value',
          axisTick: {
            show: false
          }
        },
        series: [{
          name: '覆盖率',
          type: 'bar',
          barWidth: '60%',
          data: coverageData
        }]
      })
    }
  }
}
</script>

<style scoped>
/* 添加任何自定义样式 */
</style>
