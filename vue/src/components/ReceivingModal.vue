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
      <b-row>
				<b-col cols=4>
					<label class="top-label">Units Received</label>
					<input class="form-control" type="tel" v-model="receiving.units">
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
		purchaseComponent: Object,
  },
  data() {
    return {
			visible: true,
			receiving: {},
    };
  },
  computed: {},
  watch:{},
  methods: {
    validate() {
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
			}
			this.saveReceiving().then(receiving => {
				this.closeModal();
			})
		},
		saveReceiving(){
			this.receiving.purchaseComponent = {id: this.purchaseComponent.id}
			this.receiving.receivingDate = moment.utc().format("YYYY-MM-DD")
			return http.post("/receiving", this.receiving).then(r => {
				return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
    closeModal() {
      this.$emit("closeModal", this.purchaseComponent);
    }
  },
  mounted() {
  }
};
</script>

<style>
</style>
