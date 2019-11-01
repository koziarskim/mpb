<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=9>
          <span>Production output for {{scheduleEvent.saleItem.item.name}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
			<b-row>
				<b-col cols=2>
					<label class="top-label">Units Produced:</label>
					<input class="form-control"  type="tel" v-model="production.units" placeholder="0">
				</b-col>
				<b-col cols="2">
					<label class="top-label">People Assigned:</label>
					<input class="form-control" type="tel" v-model="production.people" placeholder="0">
				</b-col>
				<b-col cols="3">
          <label class="top-label">Time Finished:</label>
					<input class="form-control" type="time" v-model="production.finishTime">
				</b-col>
			</b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  props: {
    scheduleEvent: Object,
  },
  data() {
    return {
      production: {
        finishTime: moment().format("HH:mm"),
      },
	    visible: true,
    };
  },
  computed: {
  },
  watch:{},
  methods: {
    validate() {
			if(!this.production.units || this.production.units<1){
				alert("Enter Units Produced!")
				return false;
			}
			if(!this.production.people || this.production.people<1){
				alert("Enter Assigned People!")
				return false;
      }
      if(!this.production.finishTime){
				alert("Enter Finish Time!")
				return false;
			}
			var totalUnits = +this.scheduleEvent.totalProduced + +this.production.units;
			if(totalUnits > this.scheduleEvent.unitsScheduled){
				alert("Cannot enter more units than scheduled");
				return false;
			}
			if(moment(this.production.finishTime, "HH:mm:ss").isBefore(moment(this.scheduleEvent.startTime, "HH:mm:ss"))){
				alert("Finish time cannot be before Start time " +this.scheduleEvent.startTime);
				return false;
			}
      var prevTime = moment(this.scheduleEvent.startTime, 'HH:mm:ss');
      var sortedProductions = this.scheduleEvent.productions.sort(function(a, b){
				return moment(a.finishTime, 'HH:mm:ss').diff(moment(b.finishTime, 'HH:mm:ss'));
			});
			if(sortedProductions.length>0){
				prevTime = moment(sortedProductions[sortedProductions.length-1].finishTime, 'HH:mm:ss');
			}
			var secs = moment(this.production.finishTime,'HH:mm:ss').diff(prevTime, 'seconds');
			if(secs<60){
				alert("It took "+secs+" seconds to produce "+this.production.units +" units?\n\n"
					+"Few tips: \n"
					+"Click Start Production button when production started (not later).\n"
					+"Units entered don't have to equal previously entered\n"
					+"Add units as soon as possible.\n"
					+"Add units as often as possible.\n\n"
					+"Units will added this time but please, follow the tips!");
			}
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
      }
      var production = {
				scheduleEvent: {id: this.scheduleEvent.id},
				finishTime: moment(this.production.finishTime,'HH:mm:ss').format("HH:mm:ss"),
				unitsProduced: this.production.units,
				people: this.production.people
			};
      http.post("/production", production).then(response => {
        this.closeModal();
			}).catch(e => {
				console.log("API error: " + e);
			});
      this.closeModal(this.production);
    },
    closeModal() {
      this.$emit("closeModal");
    }
  },
  mounted() {
  }
};
</script>

<style>
</style>
