<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">New/Edit Supplier</h4>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="save">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancel">Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Name:</label>
                <input class="form-control" type="text" v-model="supplier.name" placeholder="Name"/>
            </b-col>
            <b-col cols=3>
                <label class="top-label">Account #:</label>
                <input class="form-control" type="text" v-model="supplier.account" placeholder="Account #"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Pay Terms:</label>
                <input class="form-control" type="text" v-model="supplier.paymentTerms" placeholder="Payment Terms"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Address:</label>
                <input class="form-control" type="text" v-model="supplier.address" placeholder="Street and number"/>
            </b-col>
            <b-col cols=2 offset=3>
                <label class="top-label">Freight Terms:</label>
                <b-select option-value="id" option-text="name" :list="availableFreights" v-model="freightTerms" placeholder="Freight terms"></b-select>
                <!-- <input class="form-control" type="text" v-model="supplier.freightTerms" placeholder="Freight Terms"/> -->
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label class="top-label">City:</label>
                <input class="form-control" type="text" v-model="supplier.city" placeholder="City"/>
            </b-col>
            <b-col cols=1>
                <label class="top-label">State:</label>
                <input class="form-control" type="text" v-model="supplier.state"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Zip Code:</label>
                <input class="form-control" type="text" v-model="supplier.zip" placeholder="Zip code"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=3>
                <label class="top-label">Phone:</label>
                <input class="form-control" type="text" v-model="supplier.phone" placeholder="Phone"/>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";

export default {
  name: "add-component",
  data() {
    return {
      supplier: {
        name: "",
        account: "",
        address: "",
        city: "",
        state: "",
        zip: "",
        phone: "",
        paymentTerms: "",
        freightTerms: 1
      },
      freightTerms: {},
      availableStates: state.states,
      availableFreights: [{id: 1, name: "Delivered"},{id: 2, name: "Collect"}]
    };
  },
  computed: {},
  watch: {
      freightTerms: function(newValue, oldValue) {
        this.supplier.freightTerms=newValue.id;
    },
  },
  methods: {
    getSupplier(id) {
      http
        .get("/supplier/" + id)
        .then(response => {
          this.supplier = response.data;
          this.freightTerms = this.getFreightById(response.data.freightTerms);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      http
        .post("/supplier", this.supplier)
        .then(response => {})
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    cancel() {
      window.history.back();
    },
    getFreightById(id) {
        var freight = {};
        this.availableFreights.filter(it =>{
            if(it.id == id){
                freight = it;
            }
        })
        return freight;
    }
  },
  mounted() {
    var supplier_id = this.$route.params.supplier_id;
    if (supplier_id) {
      this.getSupplier(supplier_id);
    }
  }
};
</script>

<style>
</style>
