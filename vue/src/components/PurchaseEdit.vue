<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Order:</span>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="text" v-model="purchase.number" placeholder="Item name" :disabled="disabled()">
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="date" v-model="date" placeholder="Date" :disabled="disabled()">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="submit" variant="info" @click="goToPurchaseComponent()">Next</b-button>
          <b-button style="margin: 2px;" type="reset" variant="success" @click="saveAndClose" :disabled="disabled()">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Available Sale Orders:</label>
        <b-table v-if="availableSales.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="availableSales" :fields="columns">
          <template slot="number" slot-scope="row">
            <b-button size="sm" @click.stop="goToSale(row.item.id)" variant="link">{{row.item.number}}</b-button>
          </template>
          <template slot="action" slot-scope="row">
            <b-form-checkbox v-model="row.item.selected" @input="rowSelect(row.item.id, row.item.selected)" :disabled="disabled()"></b-form-checkbox>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      purchase: {},
      customer: {},
      supplier: {},
      availableSales: [],
      availableSuppliers: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "number", label: "S.O.", sortable: true },
        { key: "customerName", label: "Customer", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "dc", label: "Distribution", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      date: "",
    };
  },
  computed: {},
  watch: {
      date(new_date, old_date){
          this.purchase.date = new_date;
      }
  },
  methods: {
    disabled(){
        return this.purchase.submitted;
    },
    getPurchaseData(id) {
      http
        .get("/purchase/" + id)
        .then(response => {
          this.purchase = response.data;
          this.date = moment(response.data.date?response.data.date:moment()).utc().format("YYYY-MM-DD");
          this.getAvailableSales(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
      if (this.purchase.supplier) {
        var supplier = this.availableSuppliers.find(
          s => s.id == this.purchase.supplier.id
        );
        if (!supplier) {
          this.purchase.supplier = null;
          this.purchase.purchaseComponents = [];
        }
      }
      return http
        .post("/purchase", this.purchase)
        .then(response => {
          this.getPurchaseData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.savePurchase().then(r => {
        router.push("/purchaseList");
      });
    },
    getAvailableSales(purchase_id) {
      http
        .get("/sale/purchase/" + purchase_id)
        .then(response => {
          this.availableSales = response.data;
          this.getAvailableSuppliers(purchase_id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSuppliers(purchase_id) {
      return http
        .get("/supplier/purchase/" + purchase_id)
        .then(response => {
          this.availableSuppliers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    rowSelect(sale_id, selected) {
      var ps = {};
      if (selected) {
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
        var component = null;
        ps.sale.saleItems.forEach(si => {
          si.item.itemComponents.forEach(ic => {
            component = this.purchase.purchaseComponents.find(
              pc => pc.component.id == ic.component.id
            );
            if (component) {
              return;
            }
          });
        });
        if (component) {
          this.purchase.purchaseComponents.splice(
            this.purchase.purchaseComponents.indexOf(component),
            1
          );
        }
      }
      this.savePurchase();
    },
    comRowSelect(com_id, value) {
      var pc = {};
      if (value) {
        pc = {
          component: { id: com_id }
        };
        this.purchase.purchaseComponents.push(pc);
      } else {
        pc = this.purchase.purchaseComponents.find(
          pc => pc.component.id == com_id
        );
        this.purchase.purchaseComponents.splice(
          this.purchase.purchaseComponents.indexOf(pc),
          1
        );
      }
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
    goToPurchaseComponent() {
      this.savePurchase().then(r => {
        router.push("/purchaseComponent/" + this.purchase.id);
      });
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
