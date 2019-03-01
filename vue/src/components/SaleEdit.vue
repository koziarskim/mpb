<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">Sale Order:</h4>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="saveSale">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelSale">Close</b-button>
            </div>
        </div>
    </b-container>
</template>

<script>
import http from "../http-common";

export default {
  data() {
    return {
      sale: {
      },
    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
    getSaleData(id) {
      http
        .get("/sale/" + id)
        .then(response => {
          this.component = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveSale() {
      http
        .post("/sale", this.sale)
        .then(response => {
            this.getSaleData(response.data.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    cancelSale() {
      window.history.back();
    }
  },
  mounted() {
    var id = this.$route.params.sale_id;
    if (id) {
      this.getSaleData(id);
    }
  },
};
</script>

<style>
</style>
