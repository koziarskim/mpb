import Vue from "vue";
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
import ProductionLineList from "./components/ProductionLineList";
import ProductionItemList from "./components/ProductionItemList";
import Profile from "./components/Profile";
import DailyStatus from "./components/public/DailyStatus";
import PurchaseNew from "./components/PurchaseNew";
import ItemGraph from "./components/ItemGraph";
import ItemReturnList from "./components/ItemReturnList";

Vue.use(Router);

const router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "root",
      component: Login,
      meta: {
        group: "login"
      }
    },
    {
      path: "/Login",
      name: "Login",
      component: Login,
      meta: {
        group: "login"
      }
    },
    {
      path: "/home",
      name: "Home",
      component: Home,
      meta: {
        group: "home"
      }
    },
    {
      path: "/itemList",
      name: "itemList",
      component: ItemList,
      meta: {
        roles: ["SUPER_USER"],
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/itemComponentList/:item_id",
      name: "ItemComponentList",
      component: ItemComponentList,
      meta: {
        roles: ["SUPER_USER"],
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/itemEdit/:item_id?",
      name: "ItemEdit",
      component: ItemEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/itemGraph/:item_id?",
      name: "ItemGraph",
      component: ItemGraph,
      meta: {
        roles: ["SUPER_USER"],
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/itemSaleList/:item_id",
      name: "ItemSaleList",
      component: ItemSaleList,
      meta: {
        roles: ["SUPER_USER"],
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/componentList",
      name: "ComponentList",
      component: ComponentList,
      meta: {
        roles: ["SUPER_USER"],
        group: "component",
        viewClass: "view-component"
      }
    },
    {
      path: "/componentEdit/:component_id?",
      name: "ComponentEdit",
      component: ComponentEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "component",
        viewClass: "view-component"
      }
    },
    {
      path: "/supplierList",
      name: "SupplierList",
      component: SupplierList,
      meta: {
        roles: ["SUPER_USER"],
        group: "supplier",
        viewClass: "view-supplier"
      }
    },
    {
      path: "/supplierEdit/:supplier_id?",
      name: "SupplierEdit",
      component: SupplierEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "supplier",
        viewClass: "view-supplier"
      }
    },
   {
      path: "/customerList",
      name: "CustomerList",
      component: CustomerList,
      meta: {
        roles: ["SUPER_USER"],
        group: "customer",
        viewClass: "view-customer"
      }
    },
    {
      path: "/customerEdit/:customer_id?",
      name: "CustomerEdit",
      component: CustomerEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "customer",
        viewClass: "view-customer"
      }
    },
    {
      path: "/saleList",
      name: "SaleList",
      component: SaleList,
      meta: {
        roles: ["SUPER_USER"],
        group: "sale",
        viewClass: "view-sale"
      }
    },
    {
      path: "/saleItemList",
      name: "SaleItemList",
      component: SaleItemList,
      meta: {
        roles: ["SUPER_USER"],
        group: "sale",
        viewClass: "view-sale"
      }
    },
    {
      path: "/saleEdit/:sale_id?",
      name: "SaleEdit",
      component: SaleEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "sale",
        viewClass: "view-sale"
      }
    },
    {
      path: "/purchaseList",
      name: "PurchaseList",
      component: PurchaseList,
      meta: {
        roles: ["SUPER_USER", "POADMIN"],
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/purchaseEdit/:purchase_id",
      name: "PurchaseEdit",
      component: PurchaseEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/purchaseNew",
      name: "PurchaseNew",
      component: PurchaseNew,
      meta: {
        roles: ["SUPER_USER"],
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/PurchaseComponent/:purchase_id",
      name: "PurchaseComponent",
      component: PurchaseComponent,
      meta: {
        roles: ["SUPER_USER"],
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/PurchaseItem/:purchase_id",
      name: "PurchaseItem",
      component: PurchaseItem,
      meta: {
        roles: ["SUPER_USER"],
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/ReceivingList",
      name: "ReceivingList",
      component: ReceivingList,
      meta: {
        roles: ["SUPER_USER"],
        group: "receiving",
        viewClass: "view-receiving"
      }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "ReceivingEdit",
      component: ReceivingEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "receiving",
        viewClass: "view-receiving"
      }
    },
    {
      path: "/ReceivingEdit/pc/:pc_id",
      name: "ReceivingEditPc",
      component: ReceivingEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "receiving",
        viewClass: "view-receiving"
      }
    },
    {
      path: "/Schedule",
      name: "Schedule",
      component: Schedule,
      meta: {
        roles: ["SUPER_USER"],
        group: "schedule",
        viewClass: "view-schedule"
      }
    },
    {
      path: "/ScheduleEventList/:item_id?/:sale?/:sale_id?",
      name: "ScheduleEventList",
      component: ScheduleEventList,
      meta: {
        roles: ["SUPER_USER"],
        group: "schedule",
        viewClass: "view-schedule"
      }
    },
    {
      path: "/ShipmentEdit/:shipment_id?",
      name: "ShipmentEdit",
      component: ShipmentEdit,
      meta: {
        roles: ["SUPER_USER"],
        group: "shipment",
        viewClass: "view-shipment"
      }
    },
    {
      path: "/ShipmentList",
      name: "ShipmentList",
      component: ShipmentList,
      meta: {
        roles: ["SUPER_USER"],
        group: "shipment",
        viewClass: "view-shipment"
      }
    },
    {
      path: "/ProductionLine/:line_id",
      name: "ProductionLine",
      component: ProductionLine,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"],
        group: "production",
        viewClass: "view-production"
      }
    },
    {
      path: "/ProductionLineList/:date?",
      name: "ProductionLineList",
      component: ProductionLineList,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"],
        group: "production",
        viewClass: "view-production"
      }
    },
    {
      path: "/ProductionItemList/:date?",
      name: "ProductionItemList",
      component: ProductionItemList,
      meta: {
        roles: ["SUPER_USER", "PRODUCTION_ADMIN", "PRODUCTION_LEADER"],
        group: "production",
        viewClass: "view-production"
      }
    },
    {
      path: "/ItemReturnList/:itemId?",
      name: "ItemReturnList",
      component: ItemReturnList,
      meta: {
        roles: ["SUPER_USER"],
        group: "itemReturn",
        viewClass: "view-item-return"
      }
    },
    {
      path: "/Profile",
      name: "Profile",
      component: Profile,
      meta: {
        group: "profile"
      }
    },
    {
      path: "/Users",
      name: "Users",
      component: Users,
      meta: {
        roles: ["ADMIN"],
        group: "user"
      }
    },
    {
      path: "/AccessDenied",
      name: "AccessDenied",
      component: AccessDenied,
      meta: {
        group: "denied"
      }
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
