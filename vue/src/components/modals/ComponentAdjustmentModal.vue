<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      	<b-row>
			<b-col cols=8>
				<label class="top-label">Component:</label>
				<div>{{componentAdjustment.component.number + " - "+componentAdjustment.component.name}}</div>				
			</b-col>
			<b-col>
				<div style="text-align: right;">
					<b-button style="margin: 0 2px 0 2px" @click="deleteModal()">Delete</b-button>
					<b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
					<b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
				</div>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=3>
				<label class="top-label">Date:</label>
				<input class="form-control"  type="date" v-model="componentAdjustment.date" placeholder="0">				
			</b-col>
			<b-col cols=2>
				<label class="top-label">Units:</label>
				<input class="form-control"  type="tel" v-model="componentAdjustment.unitsAdjusted" placeholder="0">				
			</b-col>
			<b-col cols=3>
				<label class="top-label">Reason:</label>
				<b-select option-value="id" option-text="name" :list="availableReasons" v-model="reasonKv" placeholder="Component"></b-select>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=8>
				<label class="top-label">Notes:</label>
				<b-form-textarea type="text" :rows="3" v-model="componentAdjustment.notes"></b-form-textarea>
			</b-col>
		</b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
import moment from "moment";

export default {
  props: {
	  componentId: {type: Number, required: false},
	  componentAdjustmentId: {type: Number, required: false},
  },
  data() {
    return {
		componentAdjustment: {
			component: {},
			date: moment().format("YYYY-MM-DD"),
		},
		visible: true,
		availableReasons: [
			{id: "DAMAGED", name: "Damaged"},
			{id: "LOST", name: "Lost"},
			{id: "WEARHOUSE_SALE", name: "Wearhouse Sale"},
		],
		reasonKv: {},
    };
  },
  computed: {},
  watch:{},
  methods: {
	getComponentAdjustment(id) {
      http.get("/componentAdjustment/" + id).then(r => {
		this.componentAdjustment = r.data;
		this.reasonKv = {id: r.data.reason};
    	}).catch(e => {
			console.log("API error: " + e);
		});
    },
	getComponent(id) {
      http.get("/component/" + id).then(r => {
		this.componentAdjustment.component = r.data;
    	}).catch(e => {
			console.log("API error: " + e);
		});
    },
    validate() {
			if(!this.componentAdjustment.date || !this.reasonKv.id || !this.componentAdjustment.unitsAdjusted){
				alert("Date, Reason and Units are required");
				return false;
			}
      return true;
    },
    saveModal() {
	  if (!this.validate()) {return;}
	  if(!this.componentAdjustment.component.id){
	  	this.componentAdjustment.component.id = this.componentId;
	  }
	  this.componentAdjustment.reason = this.reasonKv.id;
      http.post("/componentAdjustment", this.componentAdjustment).then(response => {
        this.closeModal(response.data);
			}).catch(e => {
				console.log("API error: " + e);
			});
    },
    deleteModal() {
      this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Adjustment?").then(ok => {
        if(ok){
          http.delete("/componentAdjustment/"+this.componentAdjustment.id).then(r => {
            this.closeModal();
          }).catch(e => {
            console.log("API Error: "+e);
          });
            }
        })
    },
    closeModal() {
      this.$emit("close");
    }
  },
  mounted() {
		if(this.componentAdjustmentId){
			this.getComponentAdjustment(this.componentAdjustmentId);
		}else{
			this.getComponent(this.componentId);
		}
  }
};
</script>

<style>
</style>
