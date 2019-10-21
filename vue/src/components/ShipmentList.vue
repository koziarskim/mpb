<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <span style="text-align: left; font-size: 18px; font-weight: bold">Shipments</span>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToShipment('')">New Shipment</b-button>
            </div>
        </div>
        <div v-if="shipments.length==0">Not found any data...</div>
        <b-table v-if="shipments.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="shipments"
                :fields="fields">
                <template v-slot:cell(number)="row">
                    <b-button size="sm" @click.stop="goToShipment(row.item.id)" variant="link">{{row.item.number}}</b-button>
                </template>
                <template v-slot:cell(action)="row">
                    <b-button size="sm" @click.stop="deleteShipment(row.item.id)">x</b-button>
                </template>
        </b-table>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      sortBy: "number",
      sortDesc: false,
      fields: [
        { key: "number", label: "Shipment #", sortable: false },
        { key: "customer.name", label: "Customer", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      shipments: []
    };
  },
  methods: {
    getShipments() {
      http
        .get("/shipment")
        .then(response => {
          this.shipments = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteShipment(id) {
      http
        .delete("/shipment/" + id)
        .then(() => {
          this.getShipments();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    },
    goToShipment(id) {
      if (id) {
        router.push("/shipmentEdit/" + id);
      } else {
        http
          .post("/shipment")
          .then(response => {
            router.push("/shipmentEdit/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      }
    }
  },
  mounted() {
    this.getShipments();
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
