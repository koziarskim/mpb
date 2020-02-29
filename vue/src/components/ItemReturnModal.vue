<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
				<b-col cols=2>
					<span>Returns for Item: {{item.number}}</span>
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
					<label class="top-label">Units Received</label>
					<input class="form-control" type="tel" v-model="itemReturn.unitsReceived">
				</b-col>
				<b-col cols=2>
					<span>Units Returned: {{itemReturn.unitsReturned}}</span>
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
		itemId: Number,
		itemReturnId: Number,
  },
  data() {
    return {
			item: {},
			itemReturn: {
				saleItemReturns: [],
				item: {}
			},
	    visible: true,
    };
  },
  computed: {
  },
  watch:{},
  methods: {
		getItem(){
			http.get("/item/"+this.itemId).then(r=> {
				this.item = r.data;
			}).catch(e => {
				console.log("API error: " + e)
			})
		},
		getItemReturn(){
			http.get("/itemReturn/"+this.itemReturnId).then(r=> {
				this.itemReturn = r.data;
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
			this.itemReturn.item.id = this.item.id;
      http.post("/itemReturn", this.itemReturn).then(r=> {
        this.closeModal();
			}).catch(e => {
				console.log("API error: " + e);
			});
    },
    closeModal() {
      this.$emit("closeModal");
    }
  },
  mounted() {
		this.getItem();
		if(this.itemReturnId){
			this.getItemReturn();
		}
  }
};
</script>

<style>
</style>
