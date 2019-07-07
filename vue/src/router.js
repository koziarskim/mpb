import Vue from "vue";
import store from "./store.js";
import Router from "vue-router";
import Home from "./components/Home.vue";
import ItemEdit from "./components/ItemEdit.vue";
import ComponentEdit from "./components/ComponentEdit.vue";
import ItemList from "./components/ItemList";
import ItemComponentList from "./components/ItemComponentList";
import ComponentList from "./components/ComponentList";
import ComponentPurchaseList from "./components/ComponentPurchaseList";
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
import Schedule from "./components/Schedule";
import ItemSaleList from "./components/ItemSaleList";
import AccessDenied from "./components/AccessDenied";
import ShipmentEdit from "./components/ShipmentEdit";
import ShipmentList from "./components/ShipmentList";
import ScheduleEventList from "./components/ScheduleEventList";
import ProductionLine from "./components/ProductionLine";
import ProductionLineList from "./components/ProductionLineList";

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
      path: "/itemComponentList/:item_id",
      name: "itemComponentList",
      component: ItemComponentList
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
      path: "/ComponentPurchaseList/:component_id",
      name: "ComponentPurchaseList",
      component: ComponentPurchaseList
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
      path: "/itemSaleList/:item_id",
      name: "ItemSaleList",
      component: ItemSaleList
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
      component: ReceivingList,
      meta: {
        roles: ["INVENTORY"]
      }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "ReceivingEdit",
      component: ReceivingEdit
    },
    {
      path: "/Users",
      name: "Users",
      component: Users,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/Login",
      name: "Login",
      component: Login
    },
    {
      path: "/Schedule",
      name: "Schedule",
      component: Schedule
    },
    {
      path: "/AccessDenied",
      name: "AccessDenied",
      component: AccessDenied
    },
    {
      path: "/ShipmentEdit/:shipment_id?",
      name: "ShipmentEdit",
      component: ShipmentEdit
    },
    {
      path: "/ShipmentList",
      name: "ShipmentList",
      component: ShipmentList
    },
    {
      path: "/ScheduleEventList/:item_id?",
      name: "ScheduleEventList",
      component: ScheduleEventList
    },
    {
      path: "/ProductionLine/:production_line_id?",
      name: "ProductionLine",
      component: ProductionLine
    },
    {
      path: "/ProductionLineList",
      name: "ProductionLineList",
      component: ProductionLineList
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
    next({
      path: "/AccessDenied"
    });
  }
});

export default router;
