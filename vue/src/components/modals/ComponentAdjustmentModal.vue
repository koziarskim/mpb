<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      	<b-row>
			<b-col>
				<div style="text-align: right;">
					<b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
					<b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
				</div>
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
	  componentId: {type: Number, required: true},
	  componentAdjustmentId: {type: Number, required: false},
  },
  data() {
    return {
		componentAdjustment: {
			component: {}
		},
	    visible: true,
    };
  },
  computed: {},
  watch:{},
  methods: {
		getAddress(){
			http.get("/address/"+this.addressId).then(r => {
				this.address = r.data;
			}).catch(e => {
				console.log("API error: " + e)
			})
		},
    validate() {
			// if(!this.address.dc || !this.address.street || !this.address.city || !this.address.state || !this.address.zip){
			// 	alert("Required: DC Name, Street, City, Zip, State");
			// 	return false;
			// }
      return true;
    },
    saveModal() {
	  if (!this.validate()) {return;}
	  this.componentAdjustment.component.id = this.componentId;
      http.post("/componentAdjustment", this.componentAdjustment).then(response => {
        this.closeModal(response.data);
			}).catch(e => {
				console.log("API error: " + e);
			});
    },
    closeModal() {
      this.$emit("close");
    }
  },
  mounted() {
		if(this.componentAdjustmentId){
			this.getComponentAdjustment();
		}
  }
};
</script>

<style>
</style>
