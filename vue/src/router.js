import Vue from "vue";
import securite from "./securite";
import Router from "vue-router";
import Home from "./components/Home.vue";
import ItemEdit from "./components/ItemEdit.vue";
import ComponentEdit from "./components/ComponentEdit.vue";
import ItemList from "./components/ItemList";
import ItemComponentList from "./components/ItemComponentList";
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
import Schedule from "./components/Schedule";
import ItemSaleList from "./components/ItemSaleList";
import AccessDenied from "./components/AccessDenied";
import ShipmentEdit from "./components/ShipmentEdit";
import ShipmentList from "./components/ShipmentList";
import ScheduleEventList from "./components/ScheduleEventList";
import ProductionLine from "./components/ProductionLine";
import ProductionSale from "./components/ProductionSale";
import ProductionLineList from "./components/ProductionLineList";
import ProductionItemList from "./components/ProductionItemList";
import Profile from "./components/Profile";
import DailyStatus from "./components/public/DailyStatus";
import PurchaseNew from "./components/PurchaseNew";
import ItemGraph from "./components/ItemGraph";

Vue.use(Router);

const router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "root",
      component: Login
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
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemComponentList/:item_id",
      name: "itemComponentList",
      component: ItemComponentList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/componentList",
      name: "componentList",
      component: ComponentList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/supplierList",
      name: "supplierList",
      component: SupplierList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/customerList",
      name: "customerList",
      component: CustomerList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/saleList",
      name: "saleList",
      component: SaleList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/purchaseList",
      name: "purchaseList",
      component: PurchaseList,
      meta: {
        roles: ["SUPER_USER", "POADMIN"]
      }
    },
    {
      path: "/componentEdit/:component_id?",
      name: "componentEdit",
      component: ComponentEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemEdit/:item_id?",
      name: "itemEdit",
      component: ItemEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemGraph/:item_id?",
      name: "ItemGraph",
      component: ItemGraph,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/supplierEdit/:supplier_id?",
      name: "SupplierEdit",
      component: SupplierEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/customerEdit/:customer_id?",
      name: "CustomerEdit",
      component: CustomerEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/saleEdit/:sale_id?",
      name: "SaleEdit",
      component: SaleEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/purchaseEdit/:purchase_id",
      name: "PurchaseEdit",
      component: PurchaseEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/purchaseNew",
      name: "PurchaseNew",
      component: PurchaseNew,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemSaleList/:item_id",
      name: "ItemSaleList",
      component: ItemSaleList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/PurchaseComponent/:purchase_id",
      name: "PurchaseComponent",
      component: PurchaseComponent,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/PurchaseItem/:purchase_id",
      name: "PurchaseItem",
      component: PurchaseItem,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ReceivingList",
      name: "ReceivingList",
      component: ReceivingList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "ReceivingEdit",
      component: ReceivingEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ReceivingEdit/pc/:pc_id",
      name: "ReceivingEditPc",
      component: ReceivingEdit,
      meta: {
        roles: ["SUPER_USER"]
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
        roles: ["SUPER_USER"]
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
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ShipmentList",
      name: "ShipmentList",
      component: ShipmentList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ScheduleEventList/:item_id?/:sale?/:sale_id?",
      name: "ScheduleEventList",
      component: ScheduleEventList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ProductionSale/:schedule_event_id",
      name: "ProductionSale",
      component: ProductionSale,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/ProductionLine/:line_id",
      name: "ProductionLine",
      component: ProductionLine,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/ProductionLineList/:date?",
      name: "ProductionLineList",
      component: ProductionLineList,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/ProductionItemList/:date?",
      name: "ProductionItemList",
      component: ProductionItemList,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/Profile",
      name: "Profile",
      component: Profile,
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
  foundRole = securite.hasRole(to.meta.roles);
  if (foundRole) {
    next();
  } else {
    next({
      path: "/AccessDenied"
    });
  }
});

export default router;
