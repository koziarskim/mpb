<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
				<b-col cols=4>
					<label class="top-label">Address Distribution Center</label>
					<input class="form-control" maxlength="20" type="text" v-model="address.dc">
				</b-col>
				<b-col v-if="addressType=='FRG'">
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
					<label class="top-label">Address Line</label>
					<input class="form-control" type="tel" v-model="address.line">
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
			<b-row>
				<b-col cols=5>
					<label class="top-label">Phone:</label>
					<input class="form-control" type="tel" v-model="address.phone"/>
				</b-col>
				<b-col cols=5>
					<label class="top-label">Location ID:</label>
					<input class="form-control" type="tel" v-model="address.locationName"/>
				</b-col>
			</b-row>
			<b-row>
				<b-col cols=7>
					<label class="top-label">Notes:</label>
					<input class="form-control" type="tel" v-model="address.notes"/>
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
		addressId: Number,
		addressType: String,
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
			})
		},
    validate() {
			if(!this.address.dc || !this.address.street || !this.address.city || !this.address.state || !this.address.zip){
				alert("Required: DC Name, Street, City, Zip, State");
				return false;
			}
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
			}
			this.address.type = this.addressType;
      http.post("/address", this.address).then(response => {
        this.closeModal(response.data);
			})
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
