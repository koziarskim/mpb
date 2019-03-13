<template>
    <b-container fluid>
                <b-row>
            <b-col cols=5>
                <h4 style="text-align: left;">Purchase Order: {{purchase.number}}</h4>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                    <b-button style="margin: 2px;" type="submit" variant="primary" @click="back()">Back</b-button>
                    <b-button style="margin: 2px;" type="reset" variant="danger" @click="saveAndClose">Save & Close</b-button>
                </div>
            </b-col>
        </b-row>
        <b-row>
            <b-col>
                <label class="top-label">P.O. Items:</label>
                <b-table v-if="availableItems.length>0"
                    :sort-by.sync="sortBy"
                    :sort-desc.sync="sortDesc"
                    :items="availableItems"
                    :fields="columns">
                    <template slot="number" slot-scope="row">
                        <b-button size="sm" @click.stop="goToItem(row.item.id)" variant="link">{{row.item.number}}</b-button>
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
      availableItems: [{id: 1, number: "223"},{id: 2, number: "333"}],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "number", label: "Item", sortable: true },
        { key: "name", label: "Name", sortable: true },
        { key: "brand", label: "Brand", sortable: true },
        { key: "category", label: "Category", sortable: true },

      ]
    };
  },
  computed: {},
  watch: {},
  methods: {
    getPurchaseData(id) {
      http
        .get("/purchase/" + id)
        .then(response => {
          this.purchase = response.data;
          this.getAvailableItems(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
        router.push("/purchaseList");
    },
    next(){
        window.history.back();
    },
    back(){
        window.history.back();
    },
    getAvailableItems(purchase_id) {
      http
        .get("/item/purchase/" + purchase_id)
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id)
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
