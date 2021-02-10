<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=9>
          <div>Item: {{itemName}}</div>
        </b-col>
        <b-col cols=3>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="save()">Update</b-button>
            <b-button size="sm" style="margin-left: 3px" type="reset" variant="secondary" @click="close()">Cancel</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-table sticky-header="400px" :items="packagings" :fields="columns">
            <template v-slot:cell(name)="row">
              <div style="width:200px; overflow: wrap; font-size: 14px">{{row.item.name}}</div>
            </template>
            <template v-slot:cell(caseDimension)="row">
              {{row.item.caseLength}}x{{row.item.caseWidth}}x{{row.item.caseHeight}}
            </template>
            <template v-slot:cell(palletConfig)="row">
              {{row.item.ti}}x{{row.item.hi}}
            </template>
            <template v-slot:cell(warehouseCost)="row">
              ${{row.item.warehouseCost}}
            </template>
            <template v-slot:cell(packageCost)="row">
              ${{row.item.packageCost}}
            </template>
            <template v-slot:cell(action)="row">
              <input type="checkbox" v-model="packagingIds" :value="row.item.id">
            </template>
          </b-table>
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
import state from "../../data/state";
import moment from "moment";

export default {
  props: {
    itemPackagings: {type: Array, required: true},
    itemName: {type: String, required: true},
  },
  data() {
    return {
      visible: true,
      columns: [
        { key: "name", label: "Name", sortable: false },
        { key: "type", label: "Type", sortable: false },
        { key: "caseDimension", label: "Case Dimension", sortable: false },
        { key: "casePack", label: "C/P", sortable: false },
        { key: "palletConfig", label: "TixHi", sortable: false },
        { key: "palletWeight", label: "Weight", sortable: false },
        { key: "warehouseCost", label: "Warehouse", sortable: false },
        { key: "packageCost", label: "Package", sortable: false },
        { key: "action", label: "Action", sortable: false },
      ],
      availableTypes: [
        {id: "RSC_MC", name: "RSC-MC"},
        {id: "PDQ_HSC", name: "PDQ-HSC"},
        {id: "HSC_DRC", name: "HSC-DRC"}
      ],
      packagingIds: [],
      packagings: [],
      selectedPackagings: [],
    };
  },
  computed: {},
  watch: {},
  methods: {
    getPackagings(){
      http.get("/packaging/").then(r => {
        r.data.forEach(p => {
          if(this.packagingIds.includes(p.id)){
            r.data.splice(r.data.indexOf(p), 1);
            r.data.unshift(p);
          }
        })
        this.packagings = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    save(){
      var packagings = [];
      this.packagings.forEach(p => {
        if(this.packagingIds.includes(p.id)){
          packagings.push(p);
        }
      })
      this.$emit("close", packagings);
    },
    close(){
      this.$emit("close");
    },
  },
  mounted() {
    this.getPackagings();
    this.itemPackagings.forEach(ip => {
      this.packagingIds.push(ip.packaging.id);
    })
  }
};
</script>

<style>
.modal-lg{
  max-width: 80%;
}
</style>
