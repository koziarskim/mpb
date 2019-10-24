<script>
import { Bar } from 'vue-chartjs'

export default {
  extends: Bar,
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