import router from "./router";

export default {
  baseUrl: process.env.VUE_APP_API_BASE_URL,
  goTo: function(view) {
    return router.push(view);
  }
};
