<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
			<b-row>
				<b-col cols=4 style="margin-top: 10px">
					<span>{{saleItemTo.item.number}}</span><span style="font-size: 11px"> {{saleItemTo.item.name}}</span>
				</b-col>
        <b-col cols=6>
        <b-select v-if="!readOnly" option-value="id" option-text="name" :list="availableSaleItems" v-model="transferedSaleItemDto"></b-select>
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
					<label class="top-label">Transfer Units</label>
					<input class="form-control" type="tel" v-model="unitsTrasfered">
        </b-col>
        <b-col cols=2 style="margin-top: -10px">
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addSaleItem()">Add &#x25BC;</b-button>
				</b-col>
				<b-col cols=1 style="margin-top: 10px">
					<label style="margin-left: -50px" class="top-label">Reverse:</label><br/>
					<input style="margin-left: -35px" type="checkbox" v-model="reverseTransfer">
        </b-col>
      </b-row>
			<label class="top-label" style="font-weight: bold">Transfers for Sale: {{saleItemTo.saleNumber}}</label>
			<span style="margin-left: 100px; color: red">Transfer from {{transferFromName}} to {{transferToName}}</span>
			<b-row>
				<b-col>
					<b-table style="font-size: 12px" :items="getSaleItems()" :fields="columns">
						<template v-slot:cell(unitsTransfered)="row">
							<span>{{row.item.negative}}{{row.item.unitsTransfered}}</span>
						</template>
						<template v-slot:cell(date)="row">
							<span>{{getDateTime(row.item)}}</span>
						</template>
						<template v-slot:cell(action)="row">
							<b-button v-if="!readOnly" size="sm" @click="deleteSaleItemTransfer(row.item)">x</b-button>
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
		saleItemTo: Object,
		readOnly: Boolean,
  },
  data() {
    return {
			availableSaleItems: [], //kv
			transferedSaleItemDto: {id: null, name: null},
			saleItemFrom: {},
			unitsTrasfered: null,
			visible: true,
			reverseTransfer: false,
			transferFromName: "",
			transferToName: "",
			columns: [
				{ key: "saleFromToName", label: "Sale", sortable: false },
				{ key: "unitsTransfered", label: "Transfered", sortable: false },
				{ key: "date", label: "Date & Time", sortable: false },
        { key: "action", label: "", sortable: false },
      ],
    };
  },
  computed: {
  },
  watch:{
		reverseTransfer(oldValue, newValue){
			this.setTransferName();
		},
		transferedSaleItemDto(new_value, old_value){
			if(new_value.id){
				this.getSaleItem(new_value.id).then(si => {
					this.saleItemFrom = si;
					this.setTransferName();
				})
			}else{
				this.saleItemFrom = {};
			}
		}
	},
  methods: {
		setTransferName(){
			if(this.reverseTransfer){
				this.transferFromName = this.saleItemTo.saleNumber;
				this.transferToName = this.saleItemFrom.sale.number;
			}else{
				this.transferFromName = this.saleItemFrom.sale.number;
				this.transferToName = this.saleItemTo.saleNumber;
			}
		},
		getDateTime(transfer){
			return moment.utc(transfer.date).local().format("YYYY-MM-DD @ HH:mm")
		},
		getSaleItems(){
			this.saleItemTo.transfersTo.forEach(sit => {
				sit.saleFromToName = sit.saleFromName;
			})
			this.saleItemTo.transfersFrom.forEach(sit => {
				sit.saleFromToName = sit.saleToName;
			})
			return this.saleItemTo.transfersTo.concat(this.saleItemTo.transfersFrom)
		},
		addReverseTransfer(){
			if(+this.saleItemTo.unitsOnStock - +this.unitsTrasfered < 0){
				alert("Cannot transfer more that on Stock");
				return;
			}
			var saleItemTransfer = {
				saleToName: this.saleItemFrom.sale.number+" ("+this.saleItemFrom.sale.customer.name+")",  
				saleItemFrom: {id: this.saleItemTo.id},
				saleFromId: this.saleItemTo.saleId, 
				saleItemTo: {id: this.saleItemFrom.id},
				saleToId: {id: this.saleItemFrom.sale.id}, 
				unitsTransfered: this.unitsTrasfered,
				date: moment.utc(),
				negative: '-'
			}
			this.saleItemTo.transfersFrom.push(saleItemTransfer);
			this.saleItemTo.unitsTransferedFrom += +saleItemTransfer.unitsTransfered;
			this.transferedSaleItemDto = {};
			this.unitsTrasfered = null;

		},
		addSaleItem(){
			if(!this.unitsTrasfered || this.unitsTrasfered < 0){
				alert("Enter positive number of units to transfer");
				return;
			}
			if(this.reverseTransfer){
				this.addReverseTransfer();
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
				saleItemTo: {id: this.saleItemTo.id}, 
				unitsTransfered: this.unitsTrasfered,
				date: moment.utc()
			}
			this.saleItemTo.transfersTo.push(saleItemTransfer);
			this.saleItemTo.unitsTransferedTo += +saleItemTransfer.unitsTransfered;
			this.transferedSaleItemDto = {};
			this.unitsTrasfered = null;
		},
		deleteSaleItemTransfer(saleItemTransfer){
			if(saleItemTransfer.negative){
				var idx = this.saleItemTo.transfersFrom.findIndex(sit => sit.id == saleItemTransfer.id);
				this.saleItemTo.transfersFrom.splice(idx, 1);
				this.saleItemTo.unitsTransferedFrom -= saleItemTransfer.unitsTransfered;
			}else{
				var idx = this.saleItemTo.transfersTo.findIndex(sit => sit.id == saleItemTransfer.id);
				this.saleItemTo.transfersTo.splice(idx, 1);
				this.saleItemTo.unitsTransferedTo -= saleItemTransfer.unitsTransfered;
			}
		},
		getSaleItem(id){
			return http.get("/saleItem/"+id).then(r => {
				return r.data;
			}).catch(e => {
				console.log("API error: " + e)
			})
		},
		getAvailableSaleItems(){
			http.get("/saleItem/kv/transfer/item/"+this.saleItemTo.item.id).then(r => {
				r.data.forEach(function(si,i){
					if(si.name.includes("Marketplace Brands")){
						r.data.splice(i, 1);
						r.data.unshift(si);
					}
				});
				this.availableSaleItems = r.data.filter(si => si.id != this.saleItemTo.id);
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
		this.saleItemTo.transfersFrom.forEach(sit => {
			sit.negative = '-';
		})
		this.getAvailableSaleItems();
  }
};
</script>

<style>
</style>
