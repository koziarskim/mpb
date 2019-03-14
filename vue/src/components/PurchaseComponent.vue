<template>
  <b-container fluid>
    <b-row>
      <b-col cols="5">
        <h4 style="text-align: left;">Purchase Order: {{purchase.number}}</h4>
      </b-col>
      <b-col>
        <div style="text-align: right;">
            <b-button style="margin: 2px;" type="submit" variant="info" @click="goToPurchaseEdit()">Back</b-button>
            <b-button style="margin: 2px;" type="submit" variant="info" @click="goToPurchaseItem()">Next</b-button>
            <b-button style="margin: 2px;" type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3">
        <label class="top-label">Supplier:</label>
        <b-select
          option-value="id"
          option-text="name"
          :list="availableSuppliers"
          v-model="supplier"
          placeholder="Supplier"
        ></b-select>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Available Components:</label>
        <b-table
          v-if="availableComponents.length>0"
          :sort-by.sync="sortBy"
          :sort-desc.sync="sortDesc"
          :items="availableComponents"
          :fields="columns"
        >
          <template slot="number" slot-scope="row">
            <b-button
              size="sm"
              @click.stop="goToComponent(row.item.id)"
              variant="link"
            >{{row.item.number}}</b-button>
          </template>
          <template slot="action" slot-scope="row">
            <b-form-checkbox
              v-model="row.item.selected"
              @input="rowSelect(row.item.id, row.item.selected)"
            ></b-form-checkbox>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

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
        { key: "number", label: "Component #", sortable: true },
        { key: "name", label: "Component Name", sortable: true },
        { key: "units", label: "Quantity", sortable: true },
        { key: "unitPrice", label: "Rate", sortable: true },
        { key: "totalPrice", label: "Amount", sortable: true },
        { key: "action", label: "Action", sortable: true }
      ]
    };
  },
  computed: {},
  watch: {
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
    getPurchaseData(id) {
      http
        .get("/purchase/" + id)
        .then(response => {
          this.purchase = response.data;
          this.supplier = response.data.supplier;
          this.getAvailableSuppliers(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
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
          "/component/purchase/" +
            this.purchase.id +
            "/supplier/" +
            this.purchase.supplier.id
        )
        .then(response => {
          this.availableComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    rowSelect(component_id, selected) {
      var pc = {};
      if (selected) {
        pc = {
          component: { id: component_id }
        };
        this.purchase.purchaseComponents.push(pc);
      } else {
        pc = this.purchase.purchaseComponents.find(
          pc => pc.component.id == component_id
        );
        this.purchase.purchaseComponents.splice(
          this.purchase.purchaseComponents.indexOf(pc),
          1
        );
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
