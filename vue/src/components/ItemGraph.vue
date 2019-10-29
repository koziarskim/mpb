<template>
  <b-container fluid>
    <b-row>
      <b-col cols="6">
        <span style="font-size: 18px; font-weight: bold">Production Output for Item: </span><span>{{item.number}} ({{item.name}})</span>
      </b-col>
    </b-row>
	<br/>
	<b-row>
		<b-col cols=12>
			<div class="chartWrapper">
    		<div class="chartAreaWrapper">
					<chart :chartdata="chartData" :options="chartOptions" :height="300" :width="50000" style="width: 50000px"></chart>
				</div>
			</div>
		</b-col>
	</b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";
import store from "../store.js";

export default {
  name: "edit-component",
  data() {
    return {
			item: {},
			scheduleEvents: [],
	  	chartData: {datasets: []},
	  	chartOptions: {
				legend: {display: false},
				scales: {
					yAxes: [{
						scaleLabel: {
							display: true,
							labelString: 'Units Per Hour'
						}
					}],
        	xAxes: [{
						type: 'time',
						time: {
							distribution: 'linear',
							unit: 'day',
							stepSize: 1,
							displayFormats: {
								minute: 'mm:ss',
								hour: 'HH:mm:ss',
								day : 'MM-DD-YYYY'
								}
						}
					}]
				},
				tooltips: {
        	callbacks: {
          	label: function(tooltipItem, data) {
            	return data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index].tooltipLabel;
          	}
					}
				}
			},
    };
  },
  watch: {},
  methods: {
		getItem(item_id) {
			http.get("/item/"+item_id).then(response => {
				this.item = response.data;
				this.getScheduleEvents(response.data.id);
			}).catch(e => {
				console.log("API error: " + e);
			});
		},
    getScheduleEvents(item_id) {
      http
        .get("/scheduleEvent/item/" + item_id)
        .then(response => {
		  		this.scheduleEvents = response.data;
		  		this.updateChart();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
		},
		mtime(time){
			var mtime = moment(time,'YYYY-MM-DD HH:mm:ss');
			return mtime;
		},	
		updateChart(){
			// this.chartOptions.scales.xAxes[0].time.unit = 'day';
			var dsIndex = 0;
			this.scheduleEvents.forEach(se => {
				if(!se.startTime){
					return;
				}
				var startDate = moment(se.schedule.date +" "+ se.startTime, 'YYYY-MM-DD HH:mm:ss');
				var prevTime = moment(se.startTime, 'HH:mm:ss');
				var tooltipLabel = "Started at "+ moment(se.startTime, 'HH:mm:ss').format('HH:mm');
				var data = [{x: startDate, y: 0, tooltipLabel: tooltipLabel}];
				var sortedProductions = se.productions.sort(function(a, b){
					return moment(a.finishTime, 'HH:mm:ss').diff(moment(b.finishTime, 'HH:mm:ss'));
				});
				sortedProductions.forEach(p => {
					var secs = moment(p.finishTime, 'HH:mm:ss').diff(prevTime, 'seconds');
					var time = moment().startOf('day').seconds(secs).format('HH:mm:ss')
					var perf = !secs?0:((p.unitsProduced/secs)*3600).toFixed(0);
					var tooltipLabel= perf+" u/h (" +p.unitsProduced+" units in "+time+")"
					data.push({x: moment(se.schedule.date +" "+ p.finishTime, 'YYYY-MM-DD HH:mm:ss'), y: perf, tooltipLabel: tooltipLabel});
					prevTime = moment(p.finishTime, 'HH:mm:ss');
				})
				this.chartData.datasets.push({
					// label: this.scheduleEvent.saleItem.item.name,
					datasetIndex: dsIndex, 
					data: data, 
					steppedLine: 'after',
					fill: false,
					borderColor: '#C28535',
				});
				dsIndex++;
			})
		}
  },
  mounted() {
    var item_id = this.$route.params.item_id;
		this.getItem(item_id);
		this.chartWidth="width: 600px";
  }
};
</script>

<style>
.chartWrapper {
		position: relative;
}

.chartWrapper > canvas {
		position: absolute;
		left: 0;
		top: 0;
		pointer-events:none;
}

.chartAreaWrapper {
		width: 1200px;
		overflow-x: scroll;
}
</style>
