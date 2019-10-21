<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Order: {{purchase.number}}</span>
      </b-col>
      <b-col cols="3">
        <b-select v-if="!disabled()" option-value="id" option-text="name" :list="availableSuppliers" v-model="supplier" placeholder="Supplier" :disabled="disabled()"></b-select>
        <input v-if="disabled()" class="form-control" v-model="supplier.name" :disabled="disabled()">      
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="date" v-model="expectedDate" placeholder="Expected Date" :disabled="disabled()">
      </b-col>
      <b-col cols="2" style="margin-top: -6px">
        <label class="top-label">Pay Term:{{purchase.supplier?purchase.supplier.paymentTerms:''}}</label>
        <br>
        <label class="top-label">Freight Term:{{purchase.supplier?purchase.supplier.freightTerms:''}}</label>
      </b-col>
      <b-col cols="3">
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="submit" variant="info" @click="goToPurchaseEdit()">Back</b-button>
          <b-button style="margin: 2px;" type="submit" variant="info" @click="goToPurchaseItem()">Next</b-button>
          <b-button style="margin: 2px;" type="reset" variant="success" @click="saveAndClose" :disabled="disabled()">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Available Components:</label>
        <b-table v-if="availableComponents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="availableComponents" :fields="columns">
          <template v-slot:cell(number)="row">
            <b-button size="sm" @click.stop="goToComponent(row.item.id)" variant="link">{{row.item.number}}</b-button>
          </template>
          <template v-slot:cell(units)="row">
            <input v-if="!purchase.submitted" style="width: 100px" v-model="row.item.units" :disabled="!row.item.selected">
            <span v-if="purchase.submitted">{{row.item.units}}</span>
          </template>
          <template v-slot:cell(totalPrice)="row">
            <span>${{row.item.totalPrice = (+row.item.units * +row.item.unitPrice).toFixed(2)}}</span>
          </template>
          <template v-slot:cell(action)="row">
            <b-form-checkbox v-model="row.item.selected" @input="rowSelect(row.item, row.item.id, row.item.selected)" :disabled="disabled()"></b-form-checkbox>
          </template>
        </b-table>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3" offset="9" style="text-align: right;">
        <span>Total: ${{totalPrice}}</span>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      purchase: {},
      supplier: {},
      availableSuppliers: [],
      availableComponents: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "number", label: "Component #", sortable: false },
        { key: "name", label: "Name", sortable: false },
        { key: "units", label: "Ordered", sortable: false },
        { key: "unitsInTransit", label: "Transit", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        { key: "unitPrice", label: "Unit Cost", sortable: false },
        { key: "totalPrice", label: "Amount", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      expectedDate: ""
    };
  },
  computed: {
    totalPrice() {
      var total = 0;
      this.availableComponents.forEach(component => {
        if (component.selected) {
          total += +component.totalPrice;
        }
      });
      return total.toFixed(2);
    }
  },
  watch: {
    expectedDate(new_date, old_date) {
      this.purchase.expectedDate = new_date;
    },
    supplier: function(new_supplier, old_supplier) {
      if (!new_supplier) {
        return;
      }
      if (typeof new_supplier == "number") {
        new_supplier = this.availableSuppliers.find(s => s.id == new_supplier);
      }
      if (old_supplier && new_supplier.id == old_supplier.id) {
        return;
      }
      if (
        this.purchase.supplier &&
        new_supplier.id == this.purchase.supplier.id
      ) {
        return;
      }
      this.purchase.supplier = new_supplier;
      this.purchase.purchaseComponents = [];
      this.savePurchase();
    }
  },
  methods: {
    disabled() {
      return this.purchase.submitted;
    },
    getPurchaseData(id) {
      http
        .get("/purchase/" + id)
        .then(response => {
          this.purchase = response.data;
          this.supplier = response.data.supplier;
          this.expectedDate = response.data.expectedDate
            ? moment(response.data.expectedDate)
                .utc()
                .format("YYYY-MM-DD")
            : "";
          this.getAvailableSuppliers(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
      if (this.purchase.submitted && this.purchase.attachment){
          return Promise.resolve();
      }
      this.purchase.totalPrice = this.totalPrice;
      this.purchase.purchaseComponents.forEach(pc => {
          var componentDto = this.availableComponents.find(it => it.id == pc.component.id);
          pc.units = componentDto.units;
      })
      return http
        .post("/purchase", this.purchase)
        .then(response => {
          this.getPurchaseData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.savePurchase().then(r => {
        router.push("/purchaseList");
      });
    },
    getAvailableSuppliers(purchase_id) {
      return http
        .get("/supplier/purchase/" + purchase_id)
        .then(response => {
          this.availableSuppliers = response.data;
          this.getAvailableComponents();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableComponents() {
      if (!this.purchase.supplier) {
        return;
      }
      return http
        .get(
          "/component/purchase/" + this.purchase.id + "/supplier/" + this.purchase.supplier.id
        )
        .then(response => {
          this.availableComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    rowSelect(dto, component_id, selected) {
      var pc = {};
      if (selected) {
        pc = {
          component: { id: component_id }
        };
        this.purchase.purchaseComponents.push(pc);
        dto.units = dto.unitsNeeded - +dto.unitsOnStock;
      } else {
        pc = this.purchase.purchaseComponents.find(
          pc => pc.component.id == component_id
        );
        var idx = this.purchase.purchaseComponents.findIndex(pc.component.id == component_id)
        this.purchase.purchaseComponents.splice(idx, 1);
        dto.units = 0;
      }
    },
    goToComponent(component_id) {
      this.savePurchase().then(r => {
        router.push("/componentEdit/" + component_id);
      });
    },
    goToPurchaseEdit() {
      this.savePurchase().then(r => {
        router.push("/purchaseEdit/" + this.purchase.id);
      });
    },
    goToPurchaseItem(step_id) {
      this.savePurchase().then(r => {
        router.push("/purchaseItem/" + this.purchase.id);
      });
    }
  },
  mounted() {
    var id = this.$route.params.purchase_id;
    if (id) {
      this.getPurchaseData(id);
    }
  }
};
</script>

<style>
</style>
