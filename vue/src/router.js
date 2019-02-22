import Vue from "vue";
import Router from "vue-router";
import Home from "./components/Home.vue";
import ItemEdit from "./components/ItemEdit.vue";
import ComponentEdit from "./components/ComponentEdit.vue";
import ItemList from "./components/ItemList";
import ComponentList from "./components/ComponentList";
import PurchaseList from "./components/PurchaseList";
import SupplierList from "./components/SupplierList";
import SupplierEdit from "./components/SupplierEdit";

Vue.use(Router);

export default new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "root",
      component: Home
    },
    {
      path: "/home",
      name: "home",
      component: Home
    },
    {
      path: "/itemList",
      name: "itemList",
      component: ItemList
    },
    {
      path: "/componentList",
      name: "componentList",
      component: ComponentList
    },
    {
      path: "/supplierList",
      name: "supplierList",
      component: SupplierList
    },
    {
      path: "/purchaseList",
      name: "purchaseList",
      component: PurchaseList
    },
    {
      path: "/componentEdit/:component_id?",
      name: "componentEdit",
      component: ComponentEdit
    },
    {
      path: "/itemEdit/:item_id?",
      name: "itemEdit",
      component: ItemEdit
    },
    {
      path: "/supplierEdit/:supplier_id?",
      name: "SupplierEdit",
      component: SupplierEdit
    }
  ]
});
