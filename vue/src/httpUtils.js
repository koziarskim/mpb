import router from "./router";

export default {
  baseUrl: "http://localhost/mpb/api",
  goTo: function(view) {
    return router.push(view);
  }
};
