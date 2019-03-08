<template>
    <b-container fluid>
        <b-row>
            <b-col cols=2>
                <h4 style="text-align: left;">Purchase Order: </h4>
            </b-col>
            <b-col cols=3>
                <input class="form-control" type="text" v-model="purchase.number" placeholder="Item name"/>
            </b-col>
            <b-col cols=1>
                <b-button type="submit" variant="primary" @click="back()">Back</b-button>
            </b-col>
            <b-col cols=1>
                <b-button type="submit" variant="primary" @click="next()">Next</b-button>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                    <b-button type="submit" variant="primary" @click="savePurchase">Save</b-button>
                    <b-button type="reset" variant="danger" @click="cancelPurchase">Close</b-button>
                </div>
            </b-col>
        </b-row>
        <div v-if="visibleStep==1">
        <b-row>
            <b-col>
                <label class="top-label">Available Sale Orders:</label>
                <b-table v-if="availableSales.length>0"
                    :sort-by.sync="sortBy"
                    :sort-desc.sync="sortDesc"
                    :items="availableSales"
                    :fields="spColumns">
                    <template slot="account" slot-scope="row">
                        <b-button size="sm" @click.stop="goTo(row.item.id)" variant="link">{{row.item.id}}</b-button>
                    </template>
                    <template slot="action" slot-scope="row">
                        <b-form-checkbox v-model="row.item.selected" @input="rowSelect(row.item.id, row.item.selected)"></b-form-checkbox>
                    </template>
                </b-table>
            </b-col>
        </b-row>
        </div>
        <div v-if="visibleStep==2">
            <b-row>
                <b-col cols=3>
                    <label class="top-label">Supplier:</label>
                    <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplier" placeholder="Supplier"></b-select>
                </b-col>
            </b-row>
            <b-row>
                <b-col>
                    <label class="top-label">Available Sale Orders:</label>
                    <b-table v-if="availableSales.length>0"
                        :sort-by.sync="sortBy"
                        :sort-desc.sync="sortDesc"
                        :items="availableSales"
                        :fields="spColumns">
                        <template slot="account" slot-scope="row">
                            <b-button size="sm" @click.stop="goTo(row.item.id)" variant="link">{{row.item.id}}</b-button>
                        </template>
                        <template slot="action" slot-scope="row">
                            <b-form-checkbox v-model="row.item.selected" @input="rowSelect(row.item.id, row.item.selected)"></b-form-checkbox>
                        </template>
                    </b-table>
                </b-col>
            </b-row>
        </div>
    </b-container>
</template>

<script>
import http from "../http-common";

export default {
  data() {
    return {
      purchase: {},
      customer: {},
      availableCustomers: [],
      availableSales: [],
      availableSuppliers: [],
      sortBy: "id",
      sortDesc: false,
      spColumns: [
        { key: "number", label: "S.O.", sortable: true },
        { key: "customerName", label: "Customer", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "dc", label: "Distribution", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      visibleStep: 1
    };
  },
  computed: {},
  watch: {},
  methods: {
    getPurchaseData(id) {
      http
        .get("/purchase/" + id)
        .then(response => {
          this.purchase = response.data;
          this.getAvailableSales(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
      http
        .post("/purchase", this.purchase)
        .then(response => {
          this.getPurchaseData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    cancelPurchase() {
      window.history.back();
    },
    getAvailableSales(purchase_id){
        http
        .get("/sale/purchase/"+purchase_id)
        .then(response => {
          this.availableSales = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSuppliers(supplier_id){
        http
        .get("/supplier/"+supplier_id+"/component")
        .then(response => {
          this.availableSales = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    next(){
        if(this.visibleStep>=2){
            return;
        }
        this.visibleStep++;
    },
    back(){
        if(this.visibleStep<=1){
            return;
        }
        this.visibleStep--;
    },
    rowSelect(sale_id, value){
        var ps = {}
        if(value){
            ps = {
                sale: {id: sale_id},
            }
            this.purchase.purchaseSales.push(ps)
        }else{
            ps = this.purchase.purchaseSales.find(ps => ps.sale.id == sale_id);
            this.purchase.purchaseSales.splice(this.purchase.purchaseSales.indexOf(ps), 1);
        }
    }
  },
  mounted() {
    var id = this.$route.params.purchase_id;
    if (id) {
      this.getPurchaseData(id);
    }
  }
};
</script>

<style>
</style>
