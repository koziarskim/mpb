<script>
import { Line } from 'vue-chartjs'

export default {
  extends: Line,
  props: ['chartdata', 'options'],
  watch:{
    chartdata: {
      handler(val){
        this.renderChart(this.chartdata, this.options)
      },
      deep: true
    }
  },
  mounted () {
    // this.renderChart(this.chartdata, this.options)
  }
}
</script>

<style>
</style>