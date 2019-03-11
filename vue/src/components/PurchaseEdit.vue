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
                        <b-button size="sm" @click.stop="goToSale(row.item.id)" variant="link">{{row.item.id}}</b-button>
                    </template>
                    <template slot="action" slot-scope="row">
                        <b-form-checkbox v-model="row.item.selected" @input="saleRowSelect(row.item.id, row.item.selected)"></b-form-checkbox>
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
                    <label class="top-label">Available Components:</label>
                    <b-table v-if="availableComponents.length>0"
                        :sort-by.sync="sortBy"
                        :sort-desc.sync="sortDesc"
                        :items="availableComponents"
                        :fields="comColumns">
                        <template slot="account" slot-scope="row">
                            <b-button size="sm" @click.stop="goToComponent(row.item.id)" variant="link">{{row.item.id}}</b-button>
                        </template>
                        <template slot="action" slot-scope="row">
                            <b-form-checkbox v-model="row.item.selected" @input="comRowSelect(row.item.id, row.item.selected)"></b-form-checkbox>
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
      supplier: {},
      availableCustomers: [],
      availableSales: [],
      availableSuppliers: [],
      availableComponents: [],
      sortBy: "id",
      sortDesc: false,
      spColumns: [
        { key: "number", label: "S.O.", sortable: true },
        { key: "customerName", label: "Customer", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "dc", label: "Distribution", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      comColumns: [
        { key: "number", label: "Component #", sortable: true },
        { key: "name", label: "Component Name", sortable: true },
        { key: "itemNumber", label: "Item #", sortable: true },
        { key: "itemName", label: "Item Name", sortable: true },
        { key: "saleNumber", label: "Sale Number", sortable: true },
        { key: "saleDate", label: "Sale Date", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      visibleStep: 1
    };
  },
  computed: {},
  watch: {
      supplier: function(new_supplier, old_supplier) {
          if(new_supplier.id == old_supplier.id){
              return;
          }
          if(this.supplier && this.availableSuppliers.length>0){
            this.purchase.supplier = this.availableSuppliers.find(s => s.id == this.supplier.id);
          }
          this.purchase.purchaseComponents = [];
          if(this.supplier.id){
              this.getAvailableComponents(this.purchase.id, this.supplier.id);
          }
          this.savePurchase();
      }
  },
  methods: {
    getPurchaseData(id) {
      http
        .get("/purchase/" + id)
        .then(response => {
          this.purchase = response.data;
          if(response.data.supplier){
            this.supplier = response.data.supplier;
          }
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
    getAvailableSales(purchase_id) {
      http
        .get("/sale/purchase/" + purchase_id)
        .then(response => {
          this.availableSales = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSuppliers(purchase_id) {
      http
        .get("/supplier/purchase/" + purchase_id)
        .then(response => {
          this.availableSuppliers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableComponents(purchase_id, supplier_id) {
      http
        .get("/component/purchase/" + purchase_id + "/supplier/" + supplier_id)
        .then(response => {
          this.availableComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    next() {
      if (this.visibleStep >= 2) {
        return;
      }
      this.savePurchase();
      this.visibleStep++;
      this.getAvailableSuppliers(this.purchase.id);
    },
    back() {
      if (this.visibleStep <= 1) {
        return;
      }
      this.savePurchase();
      this.visibleStep--;
    },
    saleRowSelect(sale_id, value) {
      var ps = {};
      if (value) {
        ps = {
          sale: { id: sale_id }
        };
        this.purchase.purchaseSales.push(ps);
      } else {
        ps = this.purchase.purchaseSales.find(ps => ps.sale.id == sale_id);
        this.purchase.purchaseSales.splice(
          this.purchase.purchaseSales.indexOf(ps),
          1
        );
      }
    },
    comRowSelect(com_id, value) {
      var pc = {};
      if (value) {
        pc = {
          component: { id: com_id }
        };
        this.purchase.purchaseComponents.push(pc);
      } else {
        pc = this.purchase.purchaseComponents.find(pc => pc.component.id == com_id);
        this.purchase.purchaseComponents.splice(
          this.purchase.purchaseComponents.indexOf(pc), 1);
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
