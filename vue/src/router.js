import Vue from "vue";
import Router from "vue-router";
import Home from "./components/Home.vue";
import ItemEdit from "./components/ItemEdit.vue";
import ComponentEdit from "./components/ComponentEdit.vue";
import ItemList from "./components/ItemList";
import ComponentList from "./components/ComponentList";
import SupplierList from "./components/SupplierList";
import SupplierEdit from "./components/SupplierEdit";
import CustomerList from "./components/CustomerList";
import CustomerEdit from "./components/CustomerEdit";
import SaleList from "./components/SaleList";
import SaleEdit from "./components/SaleEdit";
import PurchaseList from "./components/PurchaseList";
import PurchaseEdit from "./components/PurchaseEdit";
import PurchaseItem from "./components/PurchaseItem";
import PurchaseComponent from "./components/PurchaseComponent";
import Inventory from "./components/Inventory";
import ComponentPurchaseList from "./components/ComponentPurchaseList";
import ShipmentList from "./components/ShipmentList";
import Users from "./components/Users";
import Login from "./components/Login";

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
      path: "/customerList",
      name: "customerList",
      component: CustomerList
    },
    {
      path: "/saleList",
      name: "saleList",
      component: SaleList
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
    },
    {
      path: "/customerEdit/:customer_id?",
      name: "CustomerEdit",
      component: CustomerEdit
    },
    {
      path: "/saleEdit/:sale_id?",
      name: "SaleEdit",
      component: SaleEdit
    },
    {
      path: "/purchaseEdit/:purchase_id",
      name: "PurchaseEdit",
      component: PurchaseEdit
    },
    {
      path: "/PurchaseComponent/:purchase_id",
      name: "PurchaseComponent",
      component: PurchaseComponent
    },
    {
      path: "/PurchaseItem/:purchase_id",
      name: "PurchaseItem",
      component: PurchaseItem
    },
    {
      path: "/Inventory",
      name: "Inventory",
      component: Inventory
    },
    {
      path: "/ComponentPurchaseList/:component_id",
      name: "ComponentPurchaseList",
      component: ComponentPurchaseList
    },
    {
      path: "/ShipmentList/:purchase_id",
      name: "ShipmentList",
      component: ShipmentList
    },
    {
      path: "/Users",
      name: "Users",
      component: Users
    },
    {
      path: "/Login",
      name: "Login",
      component: Login
    }
  ]
});
