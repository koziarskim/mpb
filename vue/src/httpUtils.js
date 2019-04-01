import router from "./router";

export default {
  baseUrl: "http://localhost/mpb-api/api/",
  goTo: function(view) {
    return router.push(view);
  }
};
