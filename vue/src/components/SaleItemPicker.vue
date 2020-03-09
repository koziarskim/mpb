<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
			<b-row>
				<b-col cols=5>
	        <b-select option-value="id" option-text="name" :list="availableSales" v-model="saleKv" placeholder="Sale"></b-select>
  	    </b-col>
        <b-col cols=1 offset=4>
          <b-button size="sm" @click="closeModal()" variant="secondary">Close</b-button>
        </b-col>
        <b-col cols=2>
          <b-button size="sm" @click="closeModal(selectedSaleItems)" variant="success">Save ({{selectedSaleItems.length}})</b-button>
        </b-col>
			</b-row>
			<br/>
			<b-table style="font-size: 12px" :items="availableSaleItems" :fields="columns">
				<template v-slot:cell(item)="row">
					<b-link disabled role="button">{{row.item.itemNumber}}</b-link>
					<span style="font-size:11px"> ({{row.item.itemName}})</span>
				</template>
				<template v-slot:cell(action)="row">
	        <input type="checkbox" v-model="selectedSaleItems" :value="row.item.id">
				</template>
			</b-table>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  props: {
		customerId: Number,
		addedSaleItemIds: Array,
  },
  data() {
    return {
			availableSales: [],
			saleKv: {},
			availableSaleItems: [], //dtos
			selectedSaleItems: [], //dtos
			visible: true,
			columns: [
				{ key: "saleNumber", label: "Sale", sortable: false },
				{ key: "item", label: "Item", sortable: false },
        { key: "action", label: "", sortable: false },
			],
			pageable: {totalElements: 1, currentPage: 1, perPage: 1000000, sortBy: 'id', sortDesc: false},
    };
  },
  computed: {
  },
  watch:{
		saleKv(oldValue, newValue){
			this.getAvailableSaleItems();
		}
	},
  methods: {
		getAvailableSaleItems(){
			http.get("/saleItem/pageable", {params: {
					pageable: this.pageable,  
					customerId: this.customerId, 
					saleId: this.saleKv.id, 
				}}).then(r => {
				this.availableSaleItems = [];
				r.data.content.forEach(si=> {
					var found = this.addedSaleItemIds.find(siId=> siId == si.id);
					if(!found){
						this.availableSaleItems.push(si);
					}
				})
			}).catch(e => {
				console.log("API error: "+e);
			});
		},
		getAvailableSales(){
			http.get("/sale/kv/shipment/customer/"+this.customerId).then(r => {
				this.availableSales = r.data;
			}).catch(e => {
				console.log("API error: "+e);
			});
		},
    closeModal(selectedSaleItems) {
			this.$emit("closeModal", selectedSaleItems);
    },
  },
  mounted() {
		this.getAvailableSales();
  }
};
</script>

<style>
</style>
