<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <h2>Items</h2>
            <div>
                <b-button type="submit" variant="primary" @click="createNewItem">New Item</b-button>
            </div>
        </div>
        <div v-if="items.length==0">Not found any components...</div>
        <b-table v-if="items.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="items"
                :fields="fields">
                <template slot="number" slot-scope="row">
                    <b-button size="sm" variant="link" @click.stop="updateItem(row.item.id)">{{row.item.number}}</b-button>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deleteItem(row.item.id)">x</b-button>
                </template>
        </b-table>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "edit-component",
  data() {
    return {
      sortBy: 'age',
      sortDesc: false,
      fields: [
        { key: 'number', sortable: true, label: 'Item #'},
        { key: 'name', sortable: true, label: 'Name'},
        { key: 'brand.name', sortable: true, label: 'Brand'},
        { key: 'action', sortable: false}
      ],
      items: []
    };
  },
  methods: {
    getComponentsData() {
      http
        .get("/item")
        .then(response => {
          this.items = response.data;
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    deleteItem(item_id) {
      http
        .delete("/item/"+item_id)
        .then(response => {
          this.getComponentsData();
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    createNewItem(){
        http
        .post("/item")
        .then(response => {
            router.push('/itemEdit/'+response.data.id);
        })
        .catch(e =>{
            console.log("Error post" + e)
        })
    },
    updateItem(item_id){
        router.push('./itemEdit/'+item_id);
    }
  },
  mounted() {
     this.getComponentsData();
  }
};
</script>

<style>
</style>
