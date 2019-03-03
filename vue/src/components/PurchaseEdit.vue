<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">Create Purchase Order:</h4>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="saveSale">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelSale">Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=1>
                <b-button type="submit" variant="primary" @click="back()">Back</b-button>
            </b-col>
            <b-col cols=1>
                <b-button type="submit" variant="primary" @click="next()">Next</b-button>
            </b-col>
        </b-row>
        <div v-if="visibleStep1">
        <b-row>
            <b-col>
                <label class="top-label">Available Sale Orders:</label>
                <b-table v-if="salePurchases.length>0"
                    :sort-by.sync="sortBy"
                    :sort-desc.sync="sortDesc"
                    :items="salePurchases"
                    :fields="spColumns">
                    <template slot="account" slot-scope="row">
                        <b-button size="sm" @click.stop="goTo(row.item.id)" variant="link">{{row.item.id}}</b-button>
                    </template>
                    <template slot="action" slot-scope="row">
                        <b-form-checkbox v-model="status" @change="rowSelect(row.item.id)"></b-form-checkbox>
                    </template>
                </b-table>
            </b-col>
        </b-row>
        </div>
        <div v-if="visibleStep2">
        </div>
    </b-container>
</template>

<script>
import http from "../http-common";

export default {
  data() {
    return {
      sale: {},
      customer: {},
      availableCustomers: [],
      salePurchases: [{id: 1, number: 1, description: "Item for testing", quantity: 3, rate: 0.50}],
      sortBy: "id",
      sortDesc: false,
      spColumns: [
        { key: "number", label: "Item", sortable: true },
        { key: "description", label: "Description", sortable: true },
        { key: "quantity", label: "Qty", sortable: true },
        { key: "rate", label: "Rate", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      visibleStep1: true,
      visibleStep2: false,
    };
  },

  computed: {},
  watch: {},
  methods: {
    next(){
        this.visibleStep1 = false;
        this.visibleStep2 = true;
    },
    back(){
        this.visibleStep2 = false;
        this.visibleStep1 = true;
    },
    rowSelect(event, id){
        console.log(event, id)
    },
    getSaleData(id) {
      http
        .get("/sale/" + id)
        .then(response => {
          this.component = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveSale() {
      http
        .post("/sale", this.sale)
        .then(response => {
          this.getSaleData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    cancelSale() {
      window.history.back();
    }
  },
  mounted() {
    var id = this.$route.params.sale_id;
    if (id) {
      this.getSaleData(id);
    }
  }
};
</script>

<style>
</style>
