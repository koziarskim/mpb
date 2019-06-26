import Vue from "vue";
import BootstrapVue from "bootstrap-vue";
import moment from "moment";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import babelPolyfill from 'babel-polyfill';
import "./registerServiceWorker";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import '../node_modules/nprogress/nprogress.css'
import { ModelListSelect } from "vue-search-select";
import { CustomMask } from "./directives/CustomMask";
import Search from "./directives/Search";
import UploadImage from "./directives/UploadImage";
import ScheduleModal from "./components/ScheduleModal";
import ProductionModal from "./components/ProductionModal";


Vue.use(BootstrapVue, {
  prefix: "b"
});
Vue.directive("mask", CustomMask);
Vue.component("search", Search);
Vue.component("upload", UploadImage);
Vue.component("b-select", ModelListSelect);
Vue.component("schedule-modal", ScheduleModal);
Vue.component("production-modal", ProductionModal);
Vue.config.productionTip = false;
Vue.filter("formatDate", function(value) {
  if (value) {
    return moment(String(value))
      .utc()
      .format("MM/DD/YYYY");
  }
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
