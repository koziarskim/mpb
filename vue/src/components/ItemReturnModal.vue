<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
				<b-col cols=4>
					<span style="font-weight: bold">Returns for Item:</span><br/>
          <span>{{item.number}}</span>
				</b-col>
        <b-col cols=3>
          <label class="top-label">Returned Date:</label>
          <input class="form-control" type="date" v-model="itemReturn.dateReturned" placeholder="Date">
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
			<b-row>
        <b-col cols=4>
          <label class="top-label">Customer:</label>
          <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customerKv"></b-select>
        </b-col>
				<b-col cols=2>
					<label class="top-label">Units Received</label>
					<input class="form-control" type="tel" v-model="itemReturn.unitsReceived">
				</b-col>
				<b-col cols=2>
          <label class="top-label">Units Returned <br/> (Assigned to Sale)</label>
					<span>{{itemReturn.unitsReturned}}</span>
				</b-col>
			</b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Item sales:</label>
          <b-select option-value="id" option-text="name" :list="availableSaleItems" v-model="saleItemKv"></b-select>
        </b-col>
        <b-col cols=2>
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addSaleItem()">Add &#x25BC;</b-button>
        </b-col>
      </b-row>
      <br>
			<b-row>
        <b-table :items="itemReturn.saleItemReturns" :fields="fields" no-local-sorting>
          <template v-slot:cell(unitsReturned)="row">
            <input class="form-control" style="width:100px" type="tel" v-model="row.item.unitsReturned">
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="deleteSaleItemReturn(row.item.id)">x</b-button>
          </template>
        </b-table>
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
        item: {},
        dateReturned: moment.utc().format("YYYY-MM-DD")
			},
			availableSaleItems: [],
      saleItemKv: {},
      availableCustomers: [],
      customerKv: {},
			visible: true,
			fields: [
        { key: 'saleItem.sale.number', sortable: true, label: 'Sale #'},
				{ key: 'saleItem.units', sortable: false, label: 'Sold'},
				{ key: 'saleItem.unitsShipped', sortable: false, label: 'Shipped'},
        { key: 'unitsReturned', sortable: false, label: 'Returned'},
        { key: 'action', sortable: false, label: ''},
      ],
    };
  },
  computed: {
  },
  watch:{
    customerKv(old_value, new_value){
      this.getAvailableSaleItems();
    }
  },
  methods: {
    deleteSaleItemReturn(sirId) {
      var idx = this.itemReturn.saleItemReturns.findIndex(sir => sir.id == sirId);
      this.itemReturn.saleItemReturns.splice(idx, 1);
    },
    addSaleItem(){
			http.get("/saleItem/"+this.saleItemKv.id).then(r=> {
        var sir = {
          item: {id: this.itemId},
          saleItem: r.data,
          unitsReturned: 0,
        };
        this.itemReturn.saleItemReturns.push(sir);
			});
    },
		getAvailableSaleItems(){
			http.get("/saleItem/kv/item/"+this.itemId+"/customer/"+this.customerKv.id).then(r=> {
				this.availableSaleItems = r.data;
			});
		},
		getAvailableCustomers(){
			http.get("/customer/kv/item/"+this.itemId).then(r=> {
				this.availableCustomers = r.data;
			});
		},
		getItem(){
			return http.get("/item/"+this.itemId).then(r=> {
        this.item = r.data;
        return r.data;
			});
		},
		getItemReturn(){
			return http.get("/itemReturn/"+this.itemReturnId).then(r=> {
        this.itemReturn = r.data;
        this.customerKv = {id: r.data.customer.id};
        // this.getAvailableSaleItems();
        return r.data;
			});
		},
    validate() {
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
			}
      this.itemReturn.item.id = this.item.id;
      this.itemReturn.customer = {id: this.customerKv.id};
      http.post("/itemReturn", this.itemReturn).then(r=> {
        this.closeModal();
			});
    },
    closeModal() {
      this.$emit("closeModal");
    }
  },
  mounted() {
    this.getItem();
    this.getAvailableCustomers();
		if(this.itemReturnId){
			this.getItemReturn();
		}
  }
};
</script>

<style>
</style>
