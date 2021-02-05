<template>
    <b-container fluid>
      <div class="mpb-page-info">Item > Packaging List</div>
      <b-row style="font-size: 12px">
        <b-col cols=2>
          <input class="form-control" style="font-size: 12px" v-model.lazy="packagingName" @keyup.enter="getPackagings()" placeholder="Name"/>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableTypes" v-model="typeKv" placeholder="Type"></b-select>
        </b-col>
        <b-col cols=1 offset=7>
          <b-button type="submit" variant="primary" size="sm" @click="goToPackagingEdit()">New</b-button>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-table :items="packagings" :fields="columns">
            <template v-slot:cell(name)="row">
              <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToPackagingEdit(row.item.id)">{{row.item.name}}</b-link></div>
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
          </b-table>
        </b-col>
      </b-row>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite"
import navigation from "../utils/navigation";

export default {
  name: "PackagingList",
  data() {
    return {
      packagings: [],
      packagingName: "",
      availableTypes: [
        {id: "RSC_MC", name: "RSC-MC"},
        {id: "PDQ_HSC", name: "PDQ-HSC"},
        {id: "PDQ_ROG", name: "PDQ-ROG"}
      ],
      typeKv: {},
      columns: [
        { key: "name", label: "Name", sortable: false },
        { key: "typeLabel", label: "Type", sortable: false },
        { key: "caseDimension", label: "Case Dimension", sortable: false },
        { key: "casePack", label: "C/P", sortable: false },
        { key: "caseWeight", label: "Case Weight", sortable: false },
        { key: "palletConfig", label: "TixHi", sortable: false },
        { key: "palletWeight", label: "Pallet Weight", sortable: false },
        { key: "warehouseCost", label: "Warehouse", sortable: false },
        { key: "packageCost", label: "Package", sortable: false },
      ],
    };
  },
  watch: {
    typeKv(new_value, old_value){
      this.getPackagings();      
    },
  },
  methods: {
    goToPackagingEdit(id){
      router.push('/packagingEdit/'+(id?id:''));
    },
    getPackagings(){
      http.get("/packaging/", {
        params: {
            packagingName: this.packagingName,
          }
      }).then(r => {
        if(this.packagingName){
          r.data = r.data.filter(p => p.name.includes(this.packagingName))
        }
        if(this.typeKv.id){
          r.data = r.data.filter(p => p.typeLabel.includes(this.typeKv.name))
        }
        this.packagings = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
  },
  mounted() {
  },
  activated(){
    this.getPackagings();
  }
};
</script>

<style>
</style>
