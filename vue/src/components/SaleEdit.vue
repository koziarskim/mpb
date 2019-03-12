<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">Sale Order:</h4>
            <div style="text-align: right;">
                <!-- <b-button type="submit" variant="primary" @click="saveSale">Save</b-button> -->
                <b-button type="reset" variant="danger" @click="saveAndClose">Save & Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Customer:</label>
                <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Customer"></b-select>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Date:</label>
                <input class="form-control" type="tel" v-model="sale.date" placeholder="Date"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Number:</label>
                <input class="form-control" type="tel" v-model="sale.number" placeholder="Number"/>
            </b-col>
            <b-col cols=2 offset=2>
                <label class="top-label">Pay Terms:</label>
                <input class="form-control" type="tel" v-model="sale.paymentTerms" placeholder="Pay Terms"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4 style="border: ">
                <label class="top-label">Vendor:</label><br/>
                <label class="top-label">Marketplace Brands LLC</label>
            </b-col>
            <b-col cols=4>
                <label class="top-label">shipping to Address:</label>
                <b-select option-value="id" option-text="dc" :list="customer.addresses" v-model="shippingAddress" placeholder="shipping to address"></b-select>
            </b-col>
            <b-col cols=2 offset=2>
                <label class="top-label">Expected Date:</label>
                <input class="form-control" type="tel" v-model="sale.expectedDate" placeholder="Date"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4 offset=4>
                <label class="top-label">Street:</label>
                <input class="form-control" type="tel" readOnly :value="shippingAddress.street" placeholder="City"/>
            </b-col>
            <b-col cols=2 offset=2>
                <label class="top-label">FOB:</label>
                <input class="form-control" type="tel" v-model="sale.freightTerms" placeholder="Flight Terms"/>
            </b-col>        
        <b-row>
        </b-row>
            <b-col cols=2 offset=4>
                <label class="top-label">City:</label>
                <input class="form-control" type="tel" readOnly :value="shippingAddress.city" placeholder="City"/>
            </b-col>
            <b-col cols=1>
                <label class="top-label">State:</label>
                <input class="form-control" type="tel" readOnly :value="shippingAddress.state" placeholder=""/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Zip Code:</label>
                <input class="form-control" type="tel" readOnly :value="shippingAddress.zip" placeholder="Zip"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Available Items:</label>
                <b-select option-value="id" option-text="label" :list="availableItems" v-model="item" placeholder="Customer"></b-select>
            </b-col>
            <b-col style="padding-top: 30px" cols=1>
                <b-button variant="link" @click="addItem()">(+)</b-button>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <label class="top-label"></label>
                <b-table v-if="sale.saleItems.length>0"
                    :sort-by.sync="sortBy"
                    :sort-desc.sync="sortDesc"
                    :items="sale.saleItems"
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
      sale: {
        saleItems: [],
        customer: {},
        shippingAddress: {}
      },
      customer: {
        addresses: []
      },
      item: {},
      shippingAddress: {},
      availableCustomers: [],
      availableItems: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item.number", label: "Item", sortable: true },
        { key: "item.description", label: "Description", sortable: true },
        { key: "item.quantity", label: "Qty", sortable: true },
        { key: "item.rate", label: "Rate", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ]
    };
  },

  computed: {},
  watch: {
    shippingAddress: function(new_value, old_value) {
      this.sale.shippingAddress = new_value;
    },
    customer: function(new_value, old_value) {
      this.sale.customer = new_value;
      if(old_value.id && new_value.id != old_value.id){
        this.shippingAddress = {};
      }
    }
  },
  methods: {
    getSaleData(id) {
      http
        .get("/sale/" + id)
        .then(response => {
          this.sale = response.data;
          if (response.data.customer) {
            this.customer = response.data.customer;
          }
          if (response.data.shippingAddress) {
            this.shippingAddress = response.data.shippingAddress;
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveSale() {
      return http
        .post("/sale", this.sale)
        .then(response => {
          this.getSaleData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.saveSale().then(r=>{
        window.history.back();
      })
    },
    getAvailableCustomers() {
      http
        .get("/customer")
        .then(response => {
          this.availableCustomers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableItems() {
      http
        .get("/item")
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    addItem() {
      if (!this.item.id) {
        return;
      }
      var item = this.sale.saleItems.find(it => it.item.id == this.item.id);
      if (item) {
        return;
      }
      this.sale.saleItems.push({ item: this.item});
    }
  },
  mounted() {
    var id = this.$route.params.sale_id;
    if (id) {
      this.getSaleData(id);
      this.getAvailableCustomers();
      this.getAvailableItems();
    }
  }
};
</script>

<style>
</style>
