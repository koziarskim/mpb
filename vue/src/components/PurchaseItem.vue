<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Order: {{purchase.number}}</span>
      </b-col>
      <b-col cols="2" style="margin-top: -6px">
        <label class="top-label">Supplier: {{purchase.supplier.name}}</label>
        <br>
        <label class="top-label">Exp. Date: {{purchase.expectedDate | formatDate}}</label>
      </b-col>
      <b-col cols="2" style="margin-top: -6px">
        <label class="top-label">Pay Term: {{purchase.supplier?purchase.supplier.paymentTerms:''}}</label>
        <br>
        <label class="top-label">Freight Term: {{purchase.supplier?purchase.supplier.freightTerms:''}}</label>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="submit" variant="info" @click="back()">Back</b-button>
          <b-button :disabled="purchase.submitted" style="margin: 2px;" type="reset" variant="success" @click="submitPurchase()">Submit</b-button>
          <a :href="pdfUrl()" target="_blank">
            <img style="margin: 2px;" src="../assets/pdf-download.png" width="25px">
          </a>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">P.O. Items:</label>
        <b-table v-if="availableItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="availableItems" :fields="columns">
          <template v-slot:cell(number)="row">
            <b-button size="sm" @click.stop="goToItem(row.item.id)" variant="link">{{row.item.number}}</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";

export default {
  data() {
    return {
      purchase: {
        supplier: {}
      },
      availableItems: [{ id: 1, number: "223" }, { id: 2, number: "333" }],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "number", label: "Item", sortable: false },
        { key: "name", label: "Name", sortable: false },
        { key: "brand", label: "Brand", sortable: false },
        { key: "category", label: "Category", sortable: false }
      ]
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
          this.getAvailableItems(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    submitPurchase() {
      if (this.purchase.purchaseComponents.length == 0) {
        alert("No Component selected!!!");
        return Promise.resolve();
      }
      this.purchase.submitted = true;
      return http
        .post("/purchase", this.purchase)
        .then(response => {
          this.getPurchaseData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    next() {
      window.history.back();
    },
    back() {
      window.history.back();
    },
    getAvailableItems(purchase_id) {
      http
        .get("/item/purchase/" + purchase_id)
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    pdfUrl: function() {
      return httpUtils.baseUrl + "/purchase/" + this.purchase.id + "/pdf";
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
