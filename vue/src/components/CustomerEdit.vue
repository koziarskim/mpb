<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">New/Edit Customer</h4>
            <div style="text-align: right;">
                <!-- <b-button type="submit" variant="primary" @click="save">Save</b-button> -->
                <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=6>
                <b-row>
                    <b-col cols=7>
                        <label class="top-label">Name:</label>
                        <input class="form-control" type="search" v-model="customer.name" placeholder="Name"/>
                    </b-col>
                    <b-col cols=5>
                        <label class="top-label">Account #:</label>
                        <input class="form-control" disabled type="text" v-model="customer.account" placeholder="Account #"/>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=4>
                        <label class="top-label">Pay Terms:</label>
                        <input class="form-control" type="text" v-model="customer.paymentTerms" placeholder="Payment Terms"/>
                    </b-col>
                    <b-col cols=4>
                        <label class="top-label">Freight Terms:</label>
                        <b-select option-value="code" option-text="name" :list="availableFreights" v-model="freightTerms" placeholder="Freight terms"></b-select>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=6>
                        <label class="top-label">Phone:</label>
                        <input class="form-control" type="text" v-model="customer.phone" placeholder="Phone"/>
                    </b-col>
                    <b-col cols=6>
                        <label class="top-label">Phone 2:</label>
                        <input class="form-control" type="text" v-model="customer.phone2" placeholder="Phone 2"/>
                    </b-col>
                </b-row>
                <b-row>            
                    <b-col cols=6>
                        <label class="top-label">Email:</label>
                        <input class="form-control" type="text" v-model="customer.email" placeholder="Email"/>
                    </b-col>
                    <b-col cols=6>
                        <label class="top-label">Email 2:</label>
                        <input class="form-control" type="text" v-model="customer.email2" placeholder="Email 2"/>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=6>
                        <label class="top-label">Contact:</label>
                        <input class="form-control" type="text" v-model="customer.contactName" placeholder="Contact Name"/>
                    </b-col>       
                </b-row>
            </b-col>
            <b-col cols=5>
                <b-row>
                    <b-col cols=10>
                        <label class="top-label">Billing Address:</label>
                        <input class="form-control" type="search" v-model="billingAddress.street" placeholder="Street"/>
                    </b-col>
                </b-row>
                <b-row>
                </b-row>
                <b-row>
                    <b-col cols=7>
                        <label class="top-label">City:</label>
                        <input class="form-control" type="tel" v-model="billingAddress.city" placeholder="City"/>
                    </b-col>
                    <b-col cols=2>
                        <label class="top-label">State:</label>
                        <input class="form-control" type="tel" v-model="billingAddress.state" placeholder=""/>
                    </b-col>
                    <b-col cols=3>
                        <label class="top-label">Zip Code:</label>
                        <input class="form-control" type="tel" v-model="billingAddress.zip" placeholder="Zip"/>
                    </b-col>
                </b-row>
                <div style="padding-top: 43px"></div>
                <hr class="hr-text" data-content="Shipping Address(es)">
                <b-row>
                    <b-col cols=10>
                        <label class="top-label">Ship to Address:</label>
                        <b-select option-value="id" option-text="label" :list="customer.addresses" v-model="address" placeholder="Address"></b-select>
                    </b-col>
                    <b-col cols=1 style="padding-top: 22px">
                        <b-button v-if="!addressEditFlag" variant="link" @click="addAddress()">(Add)</b-button>
                    </b-col>
                    <b-col cols=1 style="padding-top: 22px">
                        <b-button v-if="addressEditFlag" variant="link" @click="saveAddress()">(Save)</b-button>
                        <b-button v-if="!addressEditFlag" variant="link" @click="editAddress()">(Edit)</b-button>
                    </b-col>
                </b-row>
                <b-row v-if="addressEditFlag" >
                    <b-col cols=8>
                        <label class="top-label">Street:</label>
                        <input class="form-control" type="search" v-model="newAddress.street" placeholder="Street"/>
                    </b-col>
                    <b-col cols=4>
                        <label class="top-label">DC:</label>
                        <input class="form-control" type="search" v-model="newAddress.dc" placeholder="DC"/>
                    </b-col>
                </b-row>
                <b-row>
                </b-row>
                <b-row v-if="addressEditFlag">
                    <b-col cols=7>
                        <label class="top-label">City:</label>
                        <input class="form-control" type="tel" v-model="newAddress.city" placeholder="City"/>
                    </b-col>
                    <b-col cols=2>
                        <label class="top-label">State:</label>
                        <input class="form-control" type="tel" v-model="newAddress.state" placeholder=""/>
                    </b-col>
                    <b-col cols=3>
                        <label class="top-label">Zip Code:</label>
                        <input class="form-control" type="tel" v-model="newAddress.zip" placeholder="Zip"/>
                    </b-col>
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
        billingAddress: {},
        phone: "",
        paymentTerms: "",
        freightTerms: 'Collect',
        addresses: []
      },
      addressEditFlag: false,
      address: {},
      newAddress: {},
      billingAddress: {},
      freightTerms: {},
      availableStates: state.states,
      availableFreights: [
        { code: "Delivered", name: "Delivered" },
        { code: "Collect", name: "Collect" }
      ]
    };
  },
  computed: {},
  watch: {
    freightTerms: function(newValue, oldValue) {
      this.customer.freightTerms = newValue.code;
    },
    billingAddress: {
      handler: function(newValue, oldValue) {
        this.customer.billingAddress = this.billingAddress;
      },
      deep: true
    }
  },
  methods: {
    getCustomer(id) {
      http
        .get("/customer/" + id)
        .then(response => {
          this.customer = response.data;
          this.freightTerms = this.getFreightById(response.data.freightTerms);
          if (response.data.billingAddress) {
            this.billingAddress = response.data.billingAddress;
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      return http
        .post("/customer", this.customer)
        .then(response => {
          this.getCustomer(this.customer.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
    	this.save().then(r=>{
    		window.history.back();
    	})
    },
    updateAddress: function(address) {
      var idx = this.customer.addresses.findIndex(item => item.id == address.id);
      if (idx>-1) {
        this.customer.addresses.splice(idx,1);
      }
      this.customer.addresses.push(address);
    },
    getFreightById(code) {
      var freight = {};
      this.availableFreights.filter(it => {
        if (it.code == code) {
          freight = it;
        }
      });
      return freight;
    },
    addAddress() {
      this.addressEditFlag = true;
      this.newAddress = {};
    },
    editAddress() {
      this.addressEditFlag = true;
      this.newAddress = this.address;
    },
    saveAddress() {
      http
        .post("/address", this.newAddress)
        .then(response => {
          this.updateAddress(response.data);
          this.addressEditFlag = false;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
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
