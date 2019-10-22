<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Receivings</span>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Purchase:</label>
        <b-select option-value="id" option-text="name" :list="availablePurchases" v-model="purchase"></b-select>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Component:</label>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="component"></b-select>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goToReceiving('')">New Receiving</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table :items="receivings" :fields="fields" no-local-sorting>
      <template v-slot:cell(name)="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.number}} ({{row.item.name}})</b-button>
      </template>
      <template v-slot:cell(purchase)="row">
        <b-button
          size="sm"
          @click.stop="goToPurchase(row.item.purchaseComponent.purchase.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.purchase.number:''}}</b-button>
      </template>
      <template v-slot:cell(component)="row">
        <b-button
          size="sm"
          @click.stop="goToComponent(row.item.purchaseComponent.component.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.component.name:''}}</b-button>
      </template>
      <template v-slot:cell(shippedDate)="row">
        <span>{{formatDate(row.item.shippedDate)}}</span>
      </template>
      <template v-slot:cell(etaDate)="row">
        <span>{{formatDate(row.item.etaDate)}}</span>
      </template>
      <template v-slot:cell(receivedDate)="row">
        <span>{{formatDate(row.item.receivedDate)}}</span>
      </template>
      <template v-slot:cell(action)="row">
        <b-button size="sm" @click.stop="deleteReceiving(row.item.id)">x</b-button>
      </template>
    </b-table>
    <b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";

export default {
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'number', sortDesc: false},
      fields: [
        { key: "name", label: "Receiving # (Name)", sortable: false },
        { key: "purchase", label: "Purchase", sortable: false },
        { key: "component", label: "Component", sortable: false },
        { key: "containerNumber", label: "Container", sortable: false },
        { key: "shippingDate", label: "Shipped", sortable: false },
        { key: "receivingDate", label: "Received", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      availablePurchases: [],
      purchase: {},
      availableComponents: [],
      component: {},
      receivings: [],
    };
  },
  computed: {},
  watch: {
    purchase(new_value, old_value) {
      if(new_value.id == old_value.id){
        return;
      }
      this.getAvailableComponents().then(r => {
        this.getReceivings(new_value.id, null);
      });
    },
    component(new_value, old_value) {
      if(new_value.id == old_value.id){
        return;
      }
      this.getAvailablePurchases().then(r => {
        this.getReceivings(this.purchase.id, new_value.id);
      })
    }
  },
  methods: {
    paginationChange(page){
      this.pageable.currentPage = page;
      this.getReceivings(this.purchase.id, this.component.id);
    },
    formatDate(date){
        return date
            ? moment(date)
                .utc()
                .format("YYYY-MM-DD")
            : "";
    },
    getReceivings(purchase_id, component_id) {
      http.get("/receiving/pageable", {params: {pageable: this.pageable, purchase_id: purchase_id, component_id: component_id}})
        .then(r => {
          this.receivings = r.data.content;
          this.pageable.totalElements = r.data.totalElements;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailablePurchases(purchase_id) {
      return http.get("/purchase/kv", {params: {component_id: this.component.id}}).then(response => {
        this.availablePurchases = response.data;
        if (purchase_id) {
          this.purchase = {id: purchase_id}
        }
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableComponents(component_id) {
      return http.get("/component/kv", {params: {purchase_id: this.purchase.id}}).then(response => {
        this.availableComponents = response.data;
        if (component_id) {
          this.component = {id: component_id};
        }
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getPurchaseComponent(purchase_id, component_id) {
      return http.get("/purchaseComponent/purchase/"+purchase_id+"/component/"+component_id).then(r => {
       return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    goToReceiving(receiving_id) {
      if (!receiving_id) {
        if(!this.purchase.id || !this.component.id){
          alert("Please pick Purchase and Component first!")
          return Promise.resolve();
        }
        this.getPurchaseComponent(this.purchase.id, this.component.id).then(pc => {
          router.push("/receivingEdit/pc/" + pc.id);
        })
      } else {
        router.push("/receivingEdit/" + receiving_id);
      }
    },
    goToPurchase(purchase_id) {
      router.push("/purchaseEdit/" + purchase_id);
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    deleteReceiving(receiving_id) {
      http
        .delete("/receiving/" + receiving_id)
        .then(response => {
          this.getReceivings();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    }
  },
  mounted() {
    var purchase_id = this.$route.query.purchase_id;
    var component_id = this.$route.query.component_id;
    this.getAvailablePurchases(purchase_id).then(r => {
      this.getAvailableComponents(component_id).then(r => {
        this.getReceivings(purchase_id, component_id);
      });
    });
    window.history.replaceState({}, document.title, window.location.pathname);
  }
};
</script>

<style>
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
