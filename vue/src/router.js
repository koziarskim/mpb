import Vue from "vue";
import Router from "vue-router";
import Home from "./views/Home.vue";
import CustomersList from "./components/CustomersList.vue";
import AddCustomer from "./components/AddCustomer.vue";
import SearchCustomers from "./components/SearchCustomers.vue";
import Customer from "./components/Customer.vue";
import EditItem from "./components/EditItem.vue";
import EditComponent from "./components/EditComponent.vue";
import ItemList from "./components/ItemList";

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
      path: "/about",
      name: "about",
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () =>
        import(/* webpackChunkName: "about" */ "./views/About.vue")
    },
    {
      path: "/customers",
      name: "customers",
      alias: "/customer",
      component: CustomersList,
      children: [
        {
          path: "/customer/:id",
          name: "customer-details",
          component: Customer,
          props: true
        }
      ]
    },
    {
      path: "/add",
      name: "add",
      component: AddCustomer
    },
    {
      path: "/search",
      name: "search",
      component: SearchCustomers
    },
    {
      path: "/editItem",
      name: "editItem",
      component: EditItem
    },
    {
      path: "/editComponent",
      name: "editComponent",
      component: EditComponent
    },
    {
      path: "/itemList",
      name: "itemList",
      component: ItemList
    }
  ]
});
