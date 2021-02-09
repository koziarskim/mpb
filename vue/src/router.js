import Vue from "vue";
import navigate from "./utils/navigation";
import Router from "vue-router";
import Home from "./components/Home.vue";
import ItemEdit from "./components/ItemEdit.vue";
import ComponentEdit from "./components/ComponentEdit.vue";
import ItemList from "./components/ItemList";
import ComponentList from "./components/ComponentList";
import ComponentInventoryList from "./components/ComponentInventoryList";
import SupplierList from "./components/SupplierList";
import SupplierEdit from "./components/SupplierEdit";
import CustomerList from "./components/CustomerList";
import CustomerEdit from "./components/CustomerEdit";
import SaleList from "./components/SaleList";
import SaleEdit from "./components/SaleEdit";
import PurchaseList from "./components/PurchaseList";
import PurchaseEdit from "./components/PurchaseEdit";
import ReceivingList from "./components/ReceivingList";
import ReceivingEdit from "./components/ReceivingEdit";
import Users from "./components/Users";
import Login from "./components/Login";
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
import ItemGraph from "./components/ItemGraph";
import ItemReturnList from "./components/ItemReturnList";
import ShipmentSchedule from "./components/ShipmentSchedule";
import InvoiceList from "./components/InvoiceList";
import InvoiceItemList from "./components/InvoiceItemList";
import InvoiceEdit from "./components/InvoiceEdit";
import BillList from "./components/BillList";
import PackagingList from "./components/PackagingList";
import PackagingEdit from "./components/PackagingEdit";
import ItemPackagingList from "./components/ItemPackagingList";
import ComponentAdjustmentList from "./components/ComponentAdjustmentList";

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
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/itemEdit/:item_id?",
      name: "ItemEdit",
      component: ItemEdit,
      meta: {
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/itemGraph/:item_id?",
      name: "ItemGraph",
      component: ItemGraph,
      meta: {
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/packagingList",
      name: "PackagingList",
      component: PackagingList,
      meta: {
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/ItemPackagingList",
      name: "ItemPackagingList",
      component: ItemPackagingList,
      meta: {
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/packagingEdit/:packaging_id?",
      name: "PackagingEdit",
      component: PackagingEdit,
      meta: {
        group: "item",
        viewClass: "view-item"
      }
    },
    {
      path: "/componentList",
      name: "ComponentList",
      component: ComponentList,
      meta: {
        group: "component",
        viewClass: "view-component"
      }
    },
    {
      path: "/componentInventoryList",
      name: "ComponentInventoryList",
      component: ComponentInventoryList,
      meta: {
        group: "component",
        viewClass: "view-component"
      }
    },
    {
      path: "/componentAdjustmentList",
      name: "ComponentAdjustmentList",
      component: ComponentAdjustmentList,
      meta: {
        group: "component",
        viewClass: "view-component"
      }
    },
    {
      path: "/componentEdit/:component_id?",
      name: "ComponentEdit",
      component: ComponentEdit,
      meta: {
        group: "component",
        viewClass: "view-component"
      }
    },
    {
      path: "/supplierList",
      name: "SupplierList",
      component: SupplierList,
      meta: {
        group: "supplier",
        viewClass: "view-supplier"
      }
    },
    {
      path: "/supplierEdit/:supplier_id?",
      name: "SupplierEdit",
      component: SupplierEdit,
      meta: {
        group: "supplier",
        viewClass: "view-supplier"
      }
    },
   {
      path: "/customerList",
      name: "CustomerList",
      component: CustomerList,
      meta: {
        group: "customer",
        viewClass: "view-customer"
      }
    },
    {
      path: "/customerEdit/:customer_id?",
      name: "CustomerEdit",
      component: CustomerEdit,
      meta: {
        group: "customer",
        viewClass: "view-customer"
      }
    },
    {
      path: "/saleList",
      name: "SaleList",
      component: SaleList,
      meta: {
        group: "sale",
        viewClass: "view-sale"
      }
    },
    {
      path: "/saleItemList",
      name: "SaleItemList",
      component: SaleItemList,
      meta: {
        group: "sale",
        viewClass: "view-sale"
      }
    },
    {
      path: "/saleEdit/:sale_id?",
      name: "SaleEdit",
      component: SaleEdit,
      meta: {
        group: "sale",
        viewClass: "view-sale"
      }
    },
    {
      path: "/purchaseList",
      name: "PurchaseList",
      component: PurchaseList,
      meta: {
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/purchaseEdit/:purchase_id?",
      name: "PurchaseEdit",
      component: PurchaseEdit,
      meta: {
        group: "purchase",
        viewClass: "view-purchase"
      }
    },
    {
      path: "/ReceivingList",
      name: "ReceivingList",
      component: ReceivingList,
      meta: {
        group: "receiving",
        viewClass: "view-receiving"
      }
    },
    {
      path: "/ReceivingEdit/:receiving_id",
      name: "ReceivingEdit",
      component: ReceivingEdit,
      meta: {
        group: "receiving",
        viewClass: "view-receiving"
      }
    },
    {
      path: "/ReceivingEdit/pc/:pc_id",
      name: "ReceivingEditPc",
      component: ReceivingEdit,
      meta: {
        group: "receiving",
        viewClass: "view-receiving"
      }
    },
    {
      path: "/ShipmentEdit/:shipment_id?",
      name: "ShipmentEdit",
      component: ShipmentEdit,
      meta: {
        group: "shipment",
        viewClass: "view-shipment"
      }
    },
    {
      path: "/ShipmentList",
      name: "ShipmentList",
      component: ShipmentList,
      meta: {
        group: "shipment",
        viewClass: "view-shipment"
      }
    },
    {
      path: "/ShipmentSchedule",
      name: "ShipmentSchedule",
      component: ShipmentSchedule,
      meta: {
        group: "shipmentSchedule",
        viewClass: "view-shipment"
      }
    },
    {
      path: "/ProductionLine/:line_id",
      name: "ProductionLine",
      component: ProductionLine,
      meta: {
        group: "production",
        viewClass: "view-production"
      }
    },
    {
      path: "/ProductionLineList/:date?",
      name: "ProductionLineList",
      component: ProductionLineList,
      meta: {
        group: "production",
        viewClass: "view-production"
      }
    },
    {
      path: "/ProductionItemList/:date?",
      name: "ProductionItemList",
      component: ProductionItemList,
      meta: {
        group: "production",
        viewClass: "view-production"
      }
    },
    {
      path: "/ScheduleEventList",
      name: "ScheduleEventList",
      component: ScheduleEventList,
      meta: {
        group: "production",
        viewClass: "view-schedule"
      }
    },
    {
      path: "/ItemReturnList/:itemId?",
      name: "ItemReturnList",
      component: ItemReturnList,
      meta: {
        group: "itemReturn",
        viewClass: "view-item-return"
      }
    },
    {
      path: "/InvoiceList",
      name: "InvoiceList",
      component: InvoiceList,
      meta: {
        group: "invoice",
        viewClass: "view-invoice"
      }
    },
    {
      path: "/InvoiceItemList",
      name: "InvoiceItemList",
      component: InvoiceItemList,
      meta: {
        group: "invoice",
        viewClass: "view-invoice"
      }
    },    
    {
      path: "/InvoiceEdit/:invoiceId?",
      name: "InvoiceEdit",
      component: InvoiceEdit,
      meta: {
        group: "invoice",
        viewClass: "view-invoice"
      }
    },
    {
      path: "/BillList",
      name: "BillList",
      component: BillList,
      meta: {
        group: "invoice",
        viewClass: "view-invoice"
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
        roles: ['USER_ADMIN', 'ADMIN'],
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
