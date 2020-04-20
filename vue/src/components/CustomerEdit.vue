<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Name:</label>
        <input class="form-control" type="search" v-model="customer.name" />
      </b-col>
      <b-col cols=1>
        <label class="top-label">Account #:</label>
        <input class="form-control" disabled type="text" v-model="customer.account" />
      </b-col>
      <b-col cols=1>
        <label class="top-label">Vendor #:</label>
        <input class="form-control" type="text" v-model="customer.vendor" />
      </b-col>
      <b-col cols=2>
        <label class="top-label">Pay Terms:</label>
        <input class="form-control" type="text" v-model="customer.paymentTerms" />
      </b-col>
      <b-col cols=2>
        <label class="top-label">Freight Terms:</label>
        <b-select option-value="code" option-text="name" :list="availableFreights" v-model="freightTerms"></b-select>
      </b-col>
      <b-col cols=2 style="padding-right: 0px">
        <label class="top-label">Invoice Type:</label>
        <b-select option-value="id" option-text="name" :list="availableInvoiceTypes" v-model="invoiceTypeKv"></b-select>
      </b-col>
      <b-col cols=2>
        <div style="display:flex; padding-top: 20px; padding-left: 75px">
          <upload-file v-if="customer.id" v-on:close="closeUpload" :entity-id="customer.id" type="Customer" :attachments="customer.attachments"></upload-file>
          <b-button size="sm" style="margin-left: 5px" type="reset" variant="success" @click="saveAndClose">Save</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=3>
        <label class="top-label">Broker Contact:</label>
        <input class="form-control" type="text" v-model="customer.brokerContact" />
      </b-col>
      <b-col cols=3>
        <label class="top-label">Broker Email:</label>
        <input class="form-control" type="text" v-model="customer.brokerEmail" />
      </b-col>
      <b-col cols=6>
        <label class="top-label">Billing Address:</label>
        <input class="form-control" type="search" v-model="billingAddress.street" />
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=3>
        <label class="top-label">Broker Name:</label>
        <input class="form-control" type="text" v-model="customer.brokerName" />
      </b-col>
      <b-col cols=3>
        <label class="top-label">Broker Phone:</label>
        <input class="form-control" type="text" v-model="customer.brokerPhone" />
      </b-col>
        <b-col cols=2>
          <label class="top-label">City:</label>
          <input class="form-control" type="tel" v-model="billingAddress.city" placeholder="City" />
        </b-col>
        <b-col cols=2>
          <label class="top-label">State:</label>
          <input class="form-control" type="tel" v-model="billingAddress.state" placeholder />
        </b-col>
        <b-col cols=2>
          <label class="top-label">Zip Code:</label>
          <input class="form-control" type="tel" v-model="billingAddress.zip" placeholder="Zip" />
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6 offset=6>
          <label class="top-label">
            Shipping Addresses:
            <span style="cursor: pointer; color: blue" @click="openShipAddressModal()">(Edit/New)</span>
            <span v-if="shipAddress.id" style="cursor: pointer; color: blue" @click="deleteShipAddress()">(Delete)</span>
          </label>
          <b-select option-value="id" option-text="label" :list="customer.addresses" v-model="shipAddress" placeholder="Address"></b-select>
        </b-col>
      </b-row>
    <hr class="hr-text" data-content="General Compliane" />
    <b-row>
      <b-col cols=4>
        <label class="top-label">Vendor Portal:</label>
        <input class="form-control" v-model="customer.vendorPortal">
      </b-col>
      <b-col cols=5>
        <label class="top-label">Vendor Portal Credentials:</label>
        <label class="top-label" style="margin-left: 320px">EDI:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.edi">
        <input class="form-control" v-model="customer.vendorGuide">
      </b-col>
      <b-col cols=3>
        <label class="top-label">Ship To:</label>
        <b-select option-value="id" option-text="name" :list="availableShipTo" v-model="customer.shipTo"></b-select>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Operations" />
    <div style="text-align: left;"><b>&#8226; Pre-ticketing</b></div>
    <b-row>
      <b-col cols=2>
        <br/><label class="top-label">Price Ticket:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.priceTicket"><br/>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Pre Ticketing Ordering Source:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.ticketSource"></b-form-textarea>
      </b-col>
      <b-col cols=6>
        <label class="top-label">Pre Ticket Position:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.ticketPosition"></b-form-textarea>
      </b-col>
    </b-row>
    <div style="text-align: left;"><b>&#8226; Carton Labeling</b></div>
    <b-row>
      <b-col cols=2>
        <br/><label class="top-label">Seasonal Carton:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.seasonalCarton"><br/>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Seasonal Carton Requirements:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.cartonRequirements"></b-form-textarea>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Carton Label:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.cartonLabel"><br/>
        <label class="top-label" style="margin-top: 20px">Label Type:</label>
        <b-select option-value="id" option-text="name" :list="availableLabelTypes" v-model="customer.labelType"></b-select>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Carton Label Requirements:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.labelRequirements"></b-form-textarea>
      </b-col>
    </b-row>
    <div style="text-align: left;"><b>&#8226; Pallet Labeling</b></div>
    <b-row>
      <b-col cols=2>
        <br/><label class="top-label">Season Pallet Mark:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.seasonPalletMark">
      </b-col>
      <b-col cols=4>
        <label class="top-label">Seasonal Pallet Requirements:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.palletRequirements"></b-form-textarea>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Pallet Tag Size:</label>
        <b-select option-value="id" option-text="name" :list="availablePalletTagSizes" v-model="customer.palletTagSize"></b-select>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Pallet Tag Requirements:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.palletTagRequirements"></b-form-textarea>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Shipping" />
    <b-row>
      <b-col cols=2>
        <label class="top-label">Routing Process:</label>
        <input class="form-control" v-model="customer.routingProcess">
        <label class="top-label">Traffic Dep:</label>
        <input class="form-control" v-model="customer.trafficDepartment">
      </b-col>
      <b-col cols=1>
        <label class="top-label">Routing:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.routing"><br/>
        <label class="top-label">Delivery:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.delivery"><br/>
        <label class="top-label">ASN:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.asn"><br/>
        <label class="top-label">Claim:</label>
        <input type="checkbox" style="margin-left: 5px;" v-model="customer.claim">
      </b-col>
      <b-col cols=4>
        <label class="top-label">Routing Portal:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.routingPortal"></b-form-textarea>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Routing Guide:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.routingGuide"></b-form-textarea>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=4>
        <label class="top-label">BOL Pckaging:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.bolPackaging"></b-form-textarea>
      </b-col>
      <b-col cols=4>
        <label class="top-label">ASN Process:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.asnProcess"></b-form-textarea>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Claim Process:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.claimProcess"></b-form-textarea>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=4>
        <label class="top-label">Compliance Portal:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.compliancePortal"></b-form-textarea>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Shipment Notes:</label>
        <b-form-textarea type="text" :rows="3" v-model="customer.shipmentNotes"></b-form-textarea>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Accounting" />
    <b-row>
      <b-col cols="3">
        <label class="top-label">Portal:</label>
        <input class="form-control" type="text" v-model="customer.portal" />
      </b-col>
      <b-col cols="3">
        <label class="top-label">A-P Email:</label>
        <input class="form-control" type="text" v-model="customer.apEmail" />
      </b-col>
    </b-row>
    <div v-if="shipAddressModalVisible">
      <address-modal :address-id="shipAddress.id" address-type="SHP" v-on:closeModal="closeShipAddressModal"></address-modal>
    </div>
    <br />
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";

export default {
  components: {
    AddressModal: () => import("./AddressModal"),
    UploadFile: () => import("../directives/UploadFile"),
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
        freightTerms: "Collect",
        addresses: []
      },
      shipAddress: {},
      billingAddress: {},
      freightTerms: {},
      availableStates: state.states,
      availableFreights: [
        { code: "Delivered", name: "Delivered" },
        { code: "Collect", name: "Collect" }
      ],
      availablePayTerms: [
        { id: "TPB", name: "TP Bill" },
        { id: "PRP", name: "Pre Paid" },
        { id: "TPO", name: "TP Bill Other" },
        { id: "COL", name: "Collect" },
        { id: "CPU", name: "Customer Pickup" }
      ],
      availableInvoiceTypes: [
        { id: "PER_SHIPMENT_ITEM", name: "Per Shipment BOL" },
        { id: "PER_SHIPMENT_SALE", name: "Per Shipment Sale" },
        { id: "PER_FIRST_SHIPMENT", name: "Per First Shipment" },
        { id: "PER_LAST_SHIPMENT", name: "Per Last Shipment" },
        { id: "NO_INVOICE", name: "No Invoice" }
      ],
      availableShipTo: [
        { id: "CROSS_DOCK", name: "Cross Dock" },
        { id: "FLOW_THROUGH", name: "Flow Through" },
        { id: "SHIP_DC", name: "Ship to DC" },
        { id: "DIRECT_STORE", name: "Direct to store" },
        { id: "DIRECT_CUSTOMER", name: "Direct to customer" },
        { id: "CC_POOL", name: "CC/Pool" }
      ],
      availableLabelTypes: [
        { id: "UCC_128", name: "UCC-128" },
        { id: "SELF_GEN", name: "Self Generated" },
      ],
      availablePalletTagSizes: [
        { id: "4_6", name: "4 X 6" },
        { id: "STANDARD", name: "Standard" },
      ],
      invoiceTypeKv: {}
    };
  },
  computed: {},
  watch: {
    freightTerms(newValue, oldValue) {
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
    closeUpload(attachments){
      this.customer.attachments = attachments;
      this.save();
    },
    getCustomer(id) {
      http
        .get("/customer/" + id)
        .then(response => {
          this.customer = response.data;
          this.invoiceTypeKv = { id: response.data.invoiceType };
          this.freightTerms = this.getFreightById(response.data.freightTerms);
          if (response.data.billingAddress) {
            this.billingAddress = response.data.billingAddress;
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    validate() {
      if (this.customer.addresses.length < 1) {
        alert("At least one shipping address is required");
        return false;
      }
      return true;
    },
    save() {
      if (!this.validate()) {
        return Promise.reject();
      }
      this.customer.invoiceType = this.invoiceTypeKv.id;
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
      this.save().then(r => {
        window.history.back();
      });
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
    openShipAddressModal() {
      this.shipAddressModalVisible = true;
    },
    closeShipAddressModal(address) {
      if (address && address.id) {
        var idx = this.customer.addresses.findIndex(a => a.id == address.id);
        if (idx > -1) {
          this.customer.addresses.splice(idx, 1);
        }
        this.customer.addresses.push(address);
      }
      this.shipAddressModalVisible = false;
      this.shipAddress = {};
    },
    deleteShipAddress() {
      var idx = this.customer.addresses.findIndex(
        a => a.id == this.shipAddress.id
      );
      if (idx > -1) {
        this.customer.addresses.splice(idx, 1);
      }
      this.shipAddress = {};
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
