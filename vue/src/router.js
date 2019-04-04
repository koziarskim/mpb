import Vue from "vue";
import store from "./store.js";
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
import ReceivingList from "./components/ReceivingList";
import ReceivingEdit from "./components/ReceivingEdit";
import Users from "./components/Users";
import Login from "./components/Login";
import AccessDenied from "./components/AccessDenied";

Vue.use(Router);

const router = new Router({
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
      component: PurchaseList,
      meta: {
        roles: ["POADMIN"]
      }
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
      path: "/ReceivingList",
      name: "ReceivingList",
      component: ReceivingList
      //   meta: { roles: ["INVENTORY"] }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "ReceivingEdit",
      component: ReceivingEdit
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
    },
    {
      path: "/AccessDenied",
      name: "AccessDenied",
      component: AccessDenied
    }
  ]
});

router.beforeEach((to, from, next) => {
  if (!to.meta.roles) {
    next();
    return;
  }
  var foundRole = false;
  var roles = store.getters.userContext.user.roles;
  roles.forEach(role => {
    to.meta.roles.forEach(metaRole => {
      if (role.code === metaRole) {
        foundRole = true;
      }
    });
  });
  if (foundRole) {
    next();
  } else {
    next({ path: "/AccessDenied" });
  }
});

export default router;
