<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">New/Edit Customer</h4>
            <div style="text-align: right;">
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
                <hr class="hr-text" data-content="Shipping Address(es)">
                <b-row>
                  <b-col cols=9>
                      <label class="top-label">Ship to Address:
                        <span style="cursor: pointer; color: blue" @click="openShipAddressModal()"> (Edit/New) </span>
                        <span v-if="shipAddress.id" style="cursor: pointer; color: blue" @click="deleteShipAddress()"> (Delete) </span>
                      </label>
                      <b-select option-value="id" option-text="label" :list="customer.addresses" v-model="shipAddress" placeholder="Address"></b-select>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols=12>
                    <label class="top-label">Shipment Notes:</label>
                    <b-form-textarea type="text" :rows="4" v-model="customer.shipmentNotes"></b-form-textarea>
                  </b-col>
                </b-row>
            </b-col>
        </b-row>
        <div v-if="shipAddressModalVisible">
  		  	<address-modal :address-id="shipAddress.id" address-type="SHP" v-on:closeModal="closeShipAddressModal"></address-modal>
	    	</div>
        <br/>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";

export default {
  components: {
    AddressModal: () => import("./AddressModal")
  },
  data() {
    return {
      shipAddressModalVisible: false,
      customer: {
        name: "",
        account: "",
        billingAddress: {},
        phone: "",
        paymentTerms: "",
        freightTerms: 'Collect',
        addresses: []
      },
      shipAddress: {},
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
    getFreightById(code) {
      var freight = {};
      this.availableFreights.filter(it => {
        if (it.code == code) {
          freight = it;
        }
      });
      return freight;
    },
    openShipAddressModal(){
      this.shipAddressModalVisible = true;
    },
    closeShipAddressModal(address){
      if(address && address.id){
        var idx = this.customer.addresses.findIndex(a => a.id == address.id);
        if (idx>-1) {
          this.customer.addresses.splice(idx,1);
        }
        this.customer.addresses.push(address)
      }
      this.shipAddressModalVisible=false;
      this.shipAddress = {};
		},
    deleteShipAddress() {
      var idx = this.customer.addresses.findIndex(a => a.id == this.shipAddress.id);
      if (idx>-1) {
        this.customer.addresses.splice(idx,1);
      }
      this.shipAddress = {};
    },
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
