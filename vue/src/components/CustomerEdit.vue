<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">New/Edit Customer</h4>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="save">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancel">Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=8>
                <b-row>
                    <b-col cols=4>
                        <label class="top-label">Name:</label>
                        <input class="form-control" type="search" v-model="customer.name" placeholder="Name"/>
                    </b-col>
                    <b-col cols=3>
                        <label class="top-label">Account #:</label>
                        <input class="form-control" type="text" v-model="customer.account" placeholder="Account #"/>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=2>
                        <label class="top-label">Pay Terms:</label>
                        <input class="form-control" type="text" v-model="customer.paymentTerms" placeholder="Payment Terms"/>
                    </b-col>
                    <b-col cols=2>
                        <label class="top-label">Freight Terms:</label>
                        <b-select option-value="id" option-text="name" :list="availableFreights" v-model="freightTerms" placeholder="Freight terms"></b-select>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=3>
                        <label class="top-label">Phone:</label>
                        <input class="form-control" type="text" v-model="customer.phone" placeholder="Phone"/>
                    </b-col>
                    <b-col cols=3>
                        <label class="top-label">Phone 2:</label>
                        <input class="form-control" type="text" v-model="customer.phone2" placeholder="Phone 2"/>
                    </b-col>
                </b-row>
                <b-row>            
                    <b-col cols=3>
                        <label class="top-label">Email:</label>
                        <input class="form-control" type="text" v-model="customer.email" placeholder="Email"/>
                    </b-col>
                    <b-col cols=3>
                        <label class="top-label">Email 2:</label>
                        <input class="form-control" type="text" v-model="customer.email2" placeholder="Email 2"/>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=4>
                        <label class="top-label">Contact:</label>
                        <input class="form-control" type="text" v-model="customer.contactName" placeholder="Contact Name"/>
                    </b-col>       
                </b-row>
            </b-col>
            <b-col>
                <b-row>
                    <b-col cols=8>
                        <label v-if="customer.addresses.length > 0 && !addressEditFlag" class="top-label">Default Address:</label>
                        <b-select v-if="customer.addresses.length > 0 && !addressEditFlag" option-value="id" option-text="street" :list="customer.addresses" v-model="address" placeholder="Address"></b-select>
                        <label v-if="customer.addresses.length == 0 ||  addressEditFlag" class="top-label">Street:</label>
                        <input v-if="customer.addresses.length == 0 ||  addressEditFlag" class="form-control" type="search" v-model="address.street" placeholder="Street"/>
                    </b-col>
                    <b-col cols=4>
                        <b-button v-if="!addressEditFlag" variant="link" @click="addAddress()">add</b-button>
                        <b-button v-if="addressEditFlag" variant="link" @click="saveAddress()">save</b-button>
                    </b-col>
                </b-row>
                <b-row>
                    <!-- <b-row v-if="addressEditFlag || address"> -->
                        <!-- <b-col>
                            <b-form-checkbox :disabled="address.defaultFlag" v-model="address.defaultFlag" @change="unsetDefaultAddress"><label class="top-label">Default Address:</label></b-form-checkbox>
                        </b-col> -->
                </b-row>
                <b-row>
                        <b-col>
                            <label class="top-label">City:</label>
                            <input class="form-control" type="search" v-model="address.city" placeholder="City"/>
                        </b-col>
                    <!-- </div> -->
                </b-row>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";

export default {
  data() {
    return {
      customer: {
        name: "",
        account: "",
        address: "",
        city: "",
        state: "",
        zip: "",
        phone: "",
        paymentTerms: "",
        freightTerms: 1,
        addresses: []
      },
      addressEditFlag: false,
      address: {defaultFlag: true},
      freightTerms: {},
      availableStates: state.states,
      availableFreights: [
        { id: 1, name: "Delivered" },
        { id: 2, name: "Collect" }
      ]
    };
  },
  computed: {},
  watch: {
    freightTerms: function(newValue, oldValue) {
      this.customer.freightTerms = newValue.id;
    }
  },
  methods: {
    getDefaultAddress: function(action) {
        var address = {defaultFlag: true};
        if(action=="add"){
            return {};
        }
        this.customer.addresses.filter(it =>{
            if(it.defaultFlag){
                address = it;
            }
        })
        return address;
    },
    getCustomer(id, action) {
      http
        .get("/customer/" + id)
        .then(response => {
          this.customer = response.data;
          this.freightTerms = this.getFreightById(response.data.freightTerms);
          this.address = this.getDefaultAddress(action);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save(action) {
        if(this.customer.addresses.length == 0){
            this.customer.addresses.push(this.address);
        }
      http
        .post("/customer", this.customer)
        .then(response => {
            this.getCustomer(this.customer.id, action);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    cancel() {
      window.history.back();
    },
    getFreightById(id) {
      var freight = {};
      this.availableFreights.filter(it => {
        if (it.id == id) {
          freight = it;
        }
      });
      return freight;
    },
    addAddress() {
        this.addressEditFlag = true;
        this.save("add")
    },
    setAddress(){
        this.address = {};
    },
    saveAddress() {
      http
        .post("/address", this.address)
        .then(response => {
          this.customer.addresses.push(response.data);
          this.address = this.getDefaultAddress();
          this.addressEditFlag = false;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    unsetDefaultAddress(value) {
        if(value) {
            this.customer.addresses.forEach(address => {
                if(address.id == this.address.id){
                    return;
                }
                address.defaultFlag = false;
                address.label = address.street;
            })            
        }
    }
  },
  mounted() {
    var customer_id = this.$route.params.customer_id;
    if (customer_id) {
      this.getCustomer(customer_id);
    }
  }
};
</script>

<style>
</style>
