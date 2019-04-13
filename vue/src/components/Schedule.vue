<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span>
        </div>
        <b-table :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="schedules"
                :fields="fields">
                <template slot="date" slot-scope="row">
                    <span>{{formatDate(row.item.date)}}</span>
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
      schedules: [],
      scheduleDate: moment().utc().format("YYYY-MM-DD"),
      sortBy: 'date',
      sortDesc: false,
      fields: [
        { key: 'date', sortable: false, label: 'Date'},
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
    formatDate(date){
        return moment(date).utc().format("YYYY-MM-DD");
    }
  },
  mounted() {
     this.getSchedules();
  }
};
</script>

<style>
</style>
