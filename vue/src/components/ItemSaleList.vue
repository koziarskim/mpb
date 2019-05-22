<template>
  <b-container fluid>
    <div style="border: 0px" class="d-flex justify-content-between align-items-center">
      <h4 style="text-align: left;">Sales for Item: {{item.number}}</h4>
      <div style="text-align: right;">
        <b-button type="reset" variant="success" @click="close">Close</b-button>
      </div>
    </div>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="item.saleItems && item.saleItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="item.saleItems" :fields="columns">
          <template slot="sale" slot-scope="row">
            <b-button size="sm" @click.stop="goToSale(row.item.sale.id)" variant="link">{{row.item.sale.number}}</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      item: {},
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "sale", label: "Sale", sortable: false },
        { key: "sale.customer.name", label: "Customer", sortable: false },
        { key: "units", label: "Sold", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
      ]
    };
  },

  computed: {},
  watch: {},
  methods: {
    close() {
        window.history.back();
    },
    getItem(item_id) {
      http
        .get("/item/" + item_id)
        .then(response => {
          this.item = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
  },
  mounted() {
    var item_id = this.$route.params.item_id;
    this.getItem(item_id);
  }
};
</script>

<style>
</style>
