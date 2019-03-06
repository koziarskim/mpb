<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">Sale Order:</h4>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="saveSale">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelSale">Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Customer:</label>
                <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Customer"></b-select>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Date:</label>
                <input class="form-control" type="tel" v-model="sale.date" placeholder="City"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Number:</label>
                <input class="form-control" type="tel" v-model="sale.number" placeholder="City"/>
            </b-col>
            <b-col cols=2 offset=2>
                <label class="top-label">Pay Terms:</label>
                <input class="form-control" type="tel" v-model="sale.number" placeholder="City"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4 style="border: ">
                <label class="top-label">Vendor:</label><br/>
                <label class="top-label">Marketplace Brands LLC</label>
            </b-col>
            <b-col cols=4>
                <label class="top-label">Shipping Address:</label>
                <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Customer"></b-select>
            </b-col>
            <b-col cols=2 offset=2>
                <label class="top-label">Expected Date:</label>
                <input class="form-control" type="tel" v-model="sale.number" placeholder="City"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2 offset=4>
                <label class="top-label">City:</label>
                <input class="form-control" type="tel" v-model="sale.city" placeholder="City"/>
            </b-col>
            <b-col cols=1>
                <label class="top-label">State:</label>
                <input class="form-control" type="tel" v-model="sale.state" placeholder=""/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Zip Code:</label>
                <input class="form-control" type="tel" v-model="sale.zip" placeholder="Zip"/>
            </b-col>
            <b-col cols=2 offset=1>
                <label class="top-label">FOB:</label>
                <input class="form-control" type="tel" v-model="sale.number" placeholder="City"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Available Items:</label>
                <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Customer"></b-select>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <label class="top-label"></label>
                <b-table v-if="saleItems.length>0"
                    :sort-by.sync="sortBy"
                    :sort-desc.sync="sortDesc"
                    :items="saleItems"
                    :fields="columns">
                    <template slot="account" slot-scope="row">
                        <b-button size="sm" @click.stop="goTo(row.item.id)" variant="link">{{row.item.id}}</b-button>
                    </template>
                    <template slot="action" slot-scope="row">
                        <b-button size="sm" @click.stop="remove(row.item.id)">x</b-button>
                    </template>
                </b-table>
            </b-col>
        </b-row>
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
      saleItems: [{}],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "number", label: "Item", sortable: true },
        { key: "description", label: "Description", sortable: true },
        { key: "quantity", label: "Qty", sortable: true },
        { key: "rate", label: "Rate", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ]
    };
  },

  computed: {},
  watch: {},
  methods: {
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
