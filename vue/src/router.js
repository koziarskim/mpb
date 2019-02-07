import Vue from "vue";
import Router from "vue-router";
import Home from "./components/Home.vue";
import EditItem from "./components/EditItem.vue";
import EditComponent from "./components/EditComponent.vue";
import ItemList from "./components/ItemList";
import ComponentList from "./components/ComponentList";
import PurchaseList from "./components/PurchaseList";

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
      path: "/purchaseList",
      name: "purchaseList",
      component: PurchaseList
    },
    {
      path: "/editComponent",
      name: "editComponent",
      component: EditComponent
    },
    {
      path: "/editItem/:item_id?",
      name: "editItem",
      component: EditItem
    }
  ]
});
