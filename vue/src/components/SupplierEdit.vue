<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">New/Edit Supplier</h4>
            <div style="text-align: right;">
                <!-- <b-button type="submit" variant="primary" @click="save">Save</b-button> -->
                <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Name:</label>
                <input class="form-control" type="text" v-model="supplier.name" placeholder="Name"/>
            </b-col>
            <b-col cols=3>
                <label class="top-label">Account #:</label>
                <input class="form-control" disabled type="text" v-model="supplier.account" placeholder="Account #"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Pay Terms:</label>
                <input class="form-control" type="text" v-model="supplier.paymentTerms" placeholder="Payment Terms"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=4>
                <label class="top-label">Address:</label>
                <input class="form-control" type="text" v-model="supplier.street" placeholder="Street and number"/>
            </b-col>
            <b-col cols=2 offset=3>
                <label class="top-label">Freight Terms:</label>
                <b-select option-value="code" option-text="name" :list="availableFreights" v-model="freightTerms" placeholder="Freight terms"></b-select>
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
            <b-col cols=3>
                <label class="top-label">Phone 2:</label>
                <input class="form-control" type="text" v-model="supplier.phone2" placeholder="Phone 2"/>
            </b-col>
        </b-row>
        <b-row>            
            <b-col cols=3>
                <label class="top-label">Email:</label>
                <input class="form-control" type="text" v-model="supplier.email" placeholder="Email"/>
            </b-col>
            <b-col cols=3>
                <label class="top-label">Email 2:</label>
                <input class="form-control" type="text" v-model="supplier.email2" placeholder="Email 2"/>
            </b-col>
        </b-row>
        <b-row>
             <b-col cols=4>
                <label class="top-label">Contact:</label>
                <input class="form-control" type="text" v-model="supplier.contactName" placeholder="Contact Name"/>
            </b-col>       </b-row>
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
        street: "",
        city: "",
        state: "",
        zip: "",
        phone: "",
        paymentTerms: "",
        freightTerms: "Collect"
      },
      freightTerms: {},
      availableStates: state.states,
      availableFreights: [{code: "Delivered", name: "Delivered"},{code: "Collect", name: "Collect"}]
    };
  },
  computed: {},
  watch: {
      freightTerms: function(newValue, oldValue) {
        this.supplier.freightTerms=newValue.code;
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
      return http
        .post("/supplier", this.supplier)
        .then(response => {})
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
        this.save().then(r=>{
            window.history.back();
        })
    },
    getFreightById(code) {
        var freight = {};
        this.availableFreights.filter(it =>{
            if(it.code == code){
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
