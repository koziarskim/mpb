<template>
  <b-container fluid>
    <b-row>
      <b-col cols="6">
        <span style="font-size: 18px; font-weight: bold">Production Output for Item: </span><span>{{item.number}} ({{item.name}})</span>
      </b-col>
    </b-row>
	<br/>
	<b-row>
		<b-col cols=8>
			<chart :chartdata="chartData" :options="chartOptions" :width="900" :height="300"></chart>
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
			chartData: {},
			chartOptions: {
				legend: {
					display: false,
				},
				scales: {
					yAxes: [{
						scaleLabel: {
							display: true,
							labelString: 'Units Per Hour'
						}
					}]
				},
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
		updateChart(){
			var productions = [];
			var ses = this.scheduleEvents = this.scheduleEvents.sort(function(a, b){
					return moment(a.schedule.date, 'MM-DD-YYYY').diff(moment(b.schedule.date, 'MM-DD-YYYY'));
				});
			ses.forEach(se => {
				var ps = se.productions.sort(function(a, b){
					return moment(a.finishTime, 'HH:mm:ss').diff(moment(b.finishTime, 'HH:mm:ss'));
				});
				productions.push(...ps);
			});
			this.chartData = {
				labels: [moment(ses[0].startTime,'HH:mm:ss').format('HH:mm')],
				datasets: [{data: [0], lineTension: 0}]
			}
			var lastTime = moment(ses[0].startTime, 'HH:mm:ss');
			productions.forEach(production => {
				var currentTime = moment(production.finishTime, 'HH:mm:ss');
				var diffMins = currentTime.diff(lastTime, 'minutes');
				var unitsPerMinute = (production.unitsProduced/diffMins)*60;
				this.chartData.labels.push(moment(production.finishTime,'HH:mm:ss').format('HH:mm'));
				this.chartData.datasets[0].data.push(unitsPerMinute);
				lastTime = currentTime;
			})
		},
  },
  mounted() {
    var item_id = this.$route.params.item_id;
    this.getItem(item_id);
  }
};
</script>

<style>
</style>
