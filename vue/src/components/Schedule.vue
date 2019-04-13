<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span>
        </div>
        <b-table :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="days"
                :fields="fields">
                <template slot="number" slot-scope="row">
                    <b-button size="sm" variant="link" @click.stop="updateItem(row.item.id)">{{row.item.number}}</b-button>
                </template>
        </b-table>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment"

export default {
  data() {
    return {
      days: [1,2,3,4,5,6,7],
      schedules: [],
      scheduleDate: moment().utc().format("YYYY-MM-DD"),
      sortBy: 'date',
      sortDesc: false,
      fields: [
        { key: 'line1', sortable: false, label: 'Line 1'},
        { key: 'line2', sortable: false, label: 'Line 2'},
        { key: 'line3', sortable: false, label: 'Line 3'},
        { key: 'line4', sortable: false, label: 'Line 4'},
        { key: 'line5', sortable: false, label: 'Line 5'},
        { key: 'line6', sortable: false, label: 'Line 6'},
        { key: 'line7', sortable: false, label: 'Line 7'},
        { key: 'line8', sortable: false, label: 'Line 8'}
      ],
    };
  },
  methods: {
    getSchedules() {
      http
        .get("/schedule/date/"+this.scheduleDate)
        .then(response => {
          this.schedules = response.data;
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
  },
  mounted() {
     this.getSchedules();
  }
};
</script>

<style>
</style>
