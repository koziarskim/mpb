<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
			<b-row>
				<b-col cols=4 style="margin-top: 10px">
					<span>{{saleItem.item.number}}</span><span style="font-size: 11px"> {{saleItem.item.name}}</span>
				</b-col>
        <b-col cols=6>
          <b-select option-value="id" option-text="name" :list="availableSaleItems" v-model="saleItemFromDto"></b-select>
        </b-col>
        <b-col cols=1 style="margin-top: 10px">
          <b-button size="md" style="margin-left: 40px" @click="saveModal()" variant="success">Close</b-button>
        </b-col>
      </b-row>
			<div v-if="!saleItemFrom.id" style="height: 57px"></div>
      <b-row v-if="saleItemFrom.id">
        <b-col cols=2 style="margin-top: 5px">
					<label class="top-label">Sold: {{saleItemFrom.units}}</label><br/>
					<label class="top-label">Trasfered: {{saleItemFrom.unitsTransferedTo}}-{{saleItemFrom.unitsTransferedFrom}}</label>
        </b-col>
        <b-col cols=2 style="margin-top: 5px">
					<label class="top-label">Produced: {{saleItemFrom.unitsProduced}}</label><br/>
					<label class="top-label">Shipped: {{saleItemFrom.unitsShipped}}</label>
        </b-col>
        <b-col cols=2 style="margin-top: 5px">
					<label class="top-label">Stock: {{saleItemFrom.unitsOnStock}}</label><br/>
        </b-col>
        <b-col cols=3  style="margin-top: -7px; margin-bottom: 3px">
					<label class="top-label">Units To Transfer</label>
					<input class="form-control" type="tel" v-model="unitsTrasfered">
        </b-col>
        <b-col cols=2 style="margin-top: -10px">
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addSaleItem()">Add &#x25BC;</b-button>
        </b-col>
      </b-row>
			<label class="top-label" style="font-weight: bold">Transfers for Sale: {{saleItem.saleNumber}}</label>
			<b-row>
				<b-col>
					<b-table style="font-size: 12px" :items="getSaleItems()" :fields="columns">
						<template v-slot:cell(unitsTransfered)="row">
							<span>{{row.item.negative}}{{row.item.unitsTransfered}}</span>
						</template>
						<template v-slot:cell(action)="row">
							<b-button v-if="!row.item.negative" size="sm" @click="deleteSaleItemTransfer(row.item)">x</b-button>
						</template>
					</b-table>
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
		saleItem: Object,
  },
  data() {
    return {
			availableSaleItems: [], //kv
			saleItemFromDto: {id: null, name: null},
			saleItemFrom: {},
			unitsTrasfered: null,
			visible: true,
			columns: [
				{ key: "saleFromToName", label: "Sale", sortable: false },
				{ key: "unitsTransfered", label: "Transfered", sortable: false },
        { key: "action", label: "Action", sortable: false },
      ],
    };
  },
  computed: {
  },
  watch:{
		saleItemFromDto(new_value, old_value){
			if(new_value.id){
				this.getSaleItem(new_value.id).then(si => {
					this.saleItemFrom = si;
				})
			}else{
				this.saleItemFrom = {};
			}
		}
	},
  methods: {
		getSaleItems(){
			this.saleItem.transfersTo.forEach(sit => {
				sit.saleFromToName = sit.saleFromName;
			})
			this.saleItem.transfersFrom.forEach(sit => {
				sit.saleFromToName = sit.saleToName;
			})
			return this.saleItem.transfersTo.concat(this.saleItem.transfersFrom)
		},
		addSaleItem(){
			if(!this.unitsTrasfered || this.unitsTrasfered < 0){
				alert("Enter positive number of units to transfer");
				return;
			}
			if(+this.saleItemFrom.unitsOnStock - +this.unitsTrasfered < 0){
				alert("Cannot transfer more that on Stock");
				return;
			}
			var saleItemTransfer = {
				saleFromName: this.saleItemFrom.sale.number+" ("+this.saleItemFrom.sale.customer.name+")",  
				saleItemFrom: this.saleItemFrom,
				saleFromId: this.saleItemFrom.sale.id, 
				saleItemTo: {id: this.saleItem.id}, 
				unitsTransfered: this.unitsTrasfered,
			}
			this.saleItem.transfersTo.push(saleItemTransfer);
			this.saleItem.unitsTransferedTo += +saleItemTransfer.unitsTransfered;
			this.saleItemFromDto = {};
			this.unitsTrasfered = null;
		},
		deleteSaleItemTransfer(saleItemTransfer){
      var idx = this.saleItem.transfersTo.findIndex(sit => sit.id == saleItemTransfer.id);
			this.saleItem.transfersTo.splice(idx, 1);
			this.saleItem.unitsTransferedTo -= saleItemTransfer.unitsTransfered;
		},
		getSaleItem(id){
			return http.get("/saleItem/"+id).then(r => {
				return r.data;
			}).catch(e => {
				console.log("API error: " + e)
			})
		},
		getAvailableSaleItems(){
			http.get("/saleItem/kv/item/"+this.saleItem.item.id).then(r => {
				r.data.forEach(function(si,i){
					if(si.name.includes("Marketplace Brands")){
						r.data.splice(i, 1);
						r.data.unshift(si);
					}
				});
				this.availableSaleItems = r.data.filter(si => si.id != this.saleItem.id);
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
			this.$emit("saveModal", this.saleItemTransferes);
    },
  },
  mounted() {
		this.saleItem.transfersFrom.forEach(sit => {
			sit.negative = '-';
		})
		this.getAvailableSaleItems();
  }
};
</script>

<style>
</style>
