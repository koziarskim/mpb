import Vue from "vue";
import securite from "./securite";
import navigate from "./utils/navigation";
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
import SaleItemList from "./components/SaleItemList";
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
      name: "item",
      component: ItemList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemComponentList/:item_id",
      name: "item",
      component: ItemComponentList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/componentList",
      name: "component",
      component: ComponentList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/supplierList",
      name: "supplier",
      component: SupplierList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/customerList",
      name: "customer",
      component: CustomerList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/saleList",
      name: "sale",
      component: SaleList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/saleItemList",
      name: "sale",
      component: SaleItemList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/purchaseList",
      name: "purchase",
      component: PurchaseList,
      meta: {
        roles: ["SUPER_USER", "POADMIN"]
      }
    },
    {
      path: "/componentEdit/:component_id?",
      name: "component",
      component: ComponentEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemEdit/:item_id?",
      name: "item",
      component: ItemEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemGraph/:item_id?",
      name: "item",
      component: ItemGraph,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/supplierEdit/:supplier_id?",
      name: "supplier",
      component: SupplierEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/customerEdit/:customer_id?",
      name: "customer",
      component: CustomerEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/saleEdit/:sale_id?",
      name: "sale",
      component: SaleEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/purchaseEdit/:purchase_id",
      name: "purchase",
      component: PurchaseEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/purchaseNew",
      name: "purchase",
      component: PurchaseNew,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/itemSaleList/:item_id",
      name: "item",
      component: ItemSaleList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/PurchaseComponent/:purchase_id",
      name: "purchase",
      component: PurchaseComponent,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/PurchaseItem/:purchase_id",
      name: "purchase",
      component: PurchaseItem,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ReceivingList",
      name: "receiving",
      component: ReceivingList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "receiving",
      component: ReceivingEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ReceivingEdit/pc/:pc_id",
      name: "receiving",
      component: ReceivingEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/Users",
      name: "user",
      component: Users,
      meta: {
        roles: ["ADMIN"]
      }
    },
    {
      path: "/Login",
      name: "login",
      component: Login
    },
    {
      path: "/Schedule",
      name: "schedule",
      component: Schedule,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/AccessDenied",
      name: "denied",
      component: AccessDenied
    },
    {
      path: "/ShipmentEdit/:shipment_id?",
      name: "shipment",
      component: ShipmentEdit,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ShipmentList",
      name: "shipment",
      component: ShipmentList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ScheduleEventList/:item_id?/:sale?/:sale_id?",
      name: "schedule",
      component: ScheduleEventList,
      meta: {
        roles: ["SUPER_USER"]
      }
    },
    {
      path: "/ProductionSale/:schedule_event_id",
      name: "production",
      component: ProductionSale,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/ProductionLine/:line_id",
      name: "production",
      component: ProductionLine,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/ProductionLineList/:date?",
      name: "production",
      component: ProductionLineList,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/ProductionItemList/:date?",
      name: "production",
      component: ProductionItemList,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"]
      }
    },
    {
      path: "/Profile",
      name: "profile",
      component: Profile,
    },
    //public
    {
      path: "/DailyStatus",
      name: "status",
      component: DailyStatus
    }
  ]
});

router.beforeEach((to, from, next) => {
  navigate.before(to, from, next);
});

export default router;
