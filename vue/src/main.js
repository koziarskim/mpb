import Vue from "vue";
import BootstrapVue from "bootstrap-vue";
import moment from "moment";
import App from "./App.vue";
import router from "./router";
import "./registerServiceWorker";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import "../node_modules/nprogress/nprogress.css";
import { ModelListSelect } from "vue-search-select";
import UploadImage from "./directives/UploadImage";
import Chart from "./directives/Chart";
import VueCal from "vue-cal";
import "vue-cal/dist/vuecal.css";
import { Money } from 'v-money'

Vue.use(BootstrapVue);
Vue.component("v-money", Money)
Vue.component("chart", Chart);
Vue.component("upload", UploadImage);
Vue.component("b-select", ModelListSelect);
Vue.component("vue-cal", VueCal);
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
  render: h => h(App)
}).$mount("#app");
