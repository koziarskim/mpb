<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
			<b-row>
        <b-col>
          <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
          <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Sales:</label>
          <b-select option-value="id" option-text="name" :list="availableSaleItems" v-model="saleItem"></b-select>
        </b-col>
        <b-col cols=2>
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addSaleItemKv(saleItem.id)">Add &#x25BC;</b-button>
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
  },
  data() {
    return {
			availableSaleItems: [], //kv
			saleItem: {},
	    visible: true,
    };
  },
  computed: {
  },
  watch:{},
  methods: {
		getSaleItemsFrom(){
			http.get("/saleItem/kv/item/"+this.itemId).then(r => {
				this.availableSaleItems = r.data;
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
			this.$emit("saveModal", this.saleItemsFrom);
    },
    closeModal() {
      this.$emit("closeModal", this.saleItemsFrom);
    }
  },
  mounted() {
		this.getSaleItemsFrom();
  }
};
</script>

<style>
</style>
