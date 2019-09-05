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
import DailyStatus from "./components/public/DailyStatus";

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
      component: ItemList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/itemComponentList/:item_id",
      name: "itemComponentList",
      component: ItemComponentList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/componentList",
      name: "componentList",
      component: ComponentList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/supplierList",
      name: "supplierList",
      component: SupplierList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/customerList",
      name: "customerList",
      component: CustomerList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/saleList",
      name: "saleList",
      component: SaleList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/purchaseList",
      name: "purchaseList",
      component: PurchaseList,
      meta: {
        roles: ["ADMIN", "POADMIN"]
      }
    },
    {
      path: "/componentEdit/:component_id?",
      name: "componentEdit",
      component: ComponentEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/ComponentPurchaseList/:component_id",
      name: "ComponentPurchaseList",
      component: ComponentPurchaseList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/itemEdit/:item_id?",
      name: "itemEdit",
      component: ItemEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/supplierEdit/:supplier_id?",
      name: "SupplierEdit",
      component: SupplierEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/customerEdit/:customer_id?",
      name: "CustomerEdit",
      component: CustomerEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/saleEdit/:sale_id?",
      name: "SaleEdit",
      component: SaleEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/purchaseEdit/:purchase_id",
      name: "PurchaseEdit",
      component: PurchaseEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/itemSaleList/:item_id",
      name: "ItemSaleList",
      component: ItemSaleList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/PurchaseComponent/:purchase_id",
      name: "PurchaseComponent",
      component: PurchaseComponent,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/PurchaseItem/:purchase_id",
      name: "PurchaseItem",
      component: PurchaseItem,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/ReceivingList",
      name: "ReceivingList",
      component: ReceivingList,
      meta: {
        roles: ["ADMIN", "INVENTORY"]
      }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "ReceivingEdit",
      component: ReceivingEdit,
      meta: {
        roles: ["ADMIN"]
      }
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
      component: Schedule,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/AccessDenied",
      name: "AccessDenied",
      component: AccessDenied
    },
    {
      path: "/ShipmentEdit/:shipment_id?",
      name: "ShipmentEdit",
      component: ShipmentEdit,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/ShipmentList",
      name: "ShipmentList",
      component: ShipmentList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/ScheduleEventList/:item_id?",
      name: "ScheduleEventList",
      component: ScheduleEventList,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/ProductionLine/:schedule_event_id?",
      name: "ProductionLine",
      component: ProductionLine,
      meta: {
        roles: ["ADMIN", "PRODUCTION_LEADER", "PRODUCTION_ADMIN"]
      }
    },
    {
      path: "/ProductionLineList",
      name: "ProductionLineList",
      component: ProductionLineList,
      meta: {
        roles: ["ADMIN", "PRODUCTION_LEADER", "PRODUCTION_ADMIN"]
      }
    },
    //public
    {
      path: "/DailyStatus",
      name: "DailyStatus",
      component: DailyStatus
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
  if (roles) {
    roles.forEach(role => {
      to.meta.roles.forEach(metaRole => {
        if (role.code === metaRole) {
          foundRole = true;
        }
      });
    });
  }
  if (foundRole) {
    next();
  } else {
    next({
      path: "/AccessDenied"
    });
  }
});

export default router;
