import router from "./router";

export default {
  baseUrl: "http://mpb.noovitec.com/mpb-api/api/",
  //   baseUrl: "http://marcin/mpb-api/api/",
  goTo: function(view) {
    return router.push(view);
  }
};
