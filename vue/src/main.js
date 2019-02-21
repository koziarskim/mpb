import Vue from "vue";
import BootstrapVue from "bootstrap-vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "./registerServiceWorker";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import { ModelListSelect } from "vue-search-select";
import { CustomMask} from "./directives/CustomMask";

Vue.use(BootstrapVue, { prefix: "b" });
Vue.directive("mask", CustomMask);
Vue.component("b-select", ModelListSelect);
Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
