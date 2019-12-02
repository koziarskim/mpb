<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
				<b-col cols=4>
					<label class="top-label">Address Distribution Center</label>
					<input class="form-control" type="tel" v-model="address.dc">
				</b-col>
				<b-col>
					<label class="top-label">Keep it? </label><br/>
					<input type="checkbox" v-model="address.visible">
				</b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
			<b-row>
				<b-col cols=6>
					<label class="top-label">Street</label>
					<input class="form-control" type="tel" v-model="address.street">
				</b-col>
			</b-row>
			<b-row>
				<b-col cols=4>
					<label class="top-label">City</label>
					<input class="form-control" type="tel" v-model="address.city">
				</b-col>
				<b-col cols=1.5>
					<label class="top-label">State</label>
					<input style="width:60px;" class="form-control" type="tel" v-model="address.state">
				</b-col>
				<b-col cols=2>
					<label class="top-label">Zip Code</label>
					<input class="form-control" type="tel" v-model="address.zip">
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
    addressId: Number,
  },
  data() {
    return {
			address: {},
			addresses: [],
	    visible: true,
    };
  },
  computed: {
  },
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
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
			}
			this.address.type = "FRG";
      http.post("/address", this.address).then(response => {
        this.closeModal(response.data);
			}).catch(e => {
				console.log("API error: " + e);
			});
    },
    closeModal(address) {
      this.$emit("closeModal", address);
    }
  },
  mounted() {
		if(this.addressId){
			this.getAddress();
		}
  }
};
</script>

<style>
</style>
