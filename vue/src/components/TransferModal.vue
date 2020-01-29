<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
			<b-row>
        <b-col>
          <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Sales:</label>
          <b-select option-value="id" option-text="name" :list="availableSaleItems" v-model="saleItemFrom"></b-select>
        </b-col>
        <b-col cols=2>
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addSaleItem()">Add &#x25BC;</b-button>
        </b-col>
      </b-row>
			<b-row>
				<b-col>
					<b-table :items="saleItemTo.transfersTo" :fields="columns">
						<template v-slot:cell(item.number)="row">
							<b-link role="button">{{row.item.id}}</b-link>
						</template>
						<template v-slot:cell(action)="row">
							<b-button size="sm" @click="deleteSaleItemTransfer(row.item)">x</b-button>
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
  },
  data() {
    return {
			availableSaleItems: [], //kv
			saleItemFrom: {},
			visible: true,
			columns: [
				{ key: "labelFrom", label: "Sale", sortable: false },
				{ key: "unitsTransfered", label: "Transfered", sortable: false },
        { key: "action", label: "Action", sortable: false },
      ],
    };
  },
  computed: {
  },
  watch:{},
  methods: {
		addSaleItem(){
			this.getSaleItem().then(si => {
				var saleItemTransfer = {
					labelFrom: si.sale.number+" ("+si.sale.customer.name+")", 
					saleItemFrom: si,
					saleFromId: si.sale.id, 
					saleItemTo: {id: this.saleItemTo.id}, 
					unitsTransfered: 20
				}
				this.saleItemTo.transfersTo.push(saleItemTransfer);
				this.saleItemTo.unitsTransfered += saleItemTransfer.unitsTransfered;
			});
		},
		deleteSaleItemTransfer(saleItemTransfer){
      var idx = this.saleItemTo.transfersTo.findIndex(sit => sit.id == saleItemTransfer.id);
			this.saleItemTo.transfersTo.splice(idx, 1);
			this.saleItemTo.unitsTransfered -= saleItemTransfer.unitsTransfered;
		},
		getSaleItem(){
			return http.get("/saleItem/"+this.saleItemFrom.id).then(r => {
				return r.data;
			}).catch(e => {
				console.log("API error: " + e)
			})
		},
		getAvailableSaleItems(){
			http.get("/saleItem/kv/item/"+this.saleItemTo.item.id).then(r => {
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
			this.$emit("saveModal", this.saleItemTransferes);
    },
  },
  mounted() {
		this.getAvailableSaleItems();
  }
};
</script>

<style>
</style>
