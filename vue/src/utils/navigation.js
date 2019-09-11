import router from "../router";

export default {
  baseUrl: process.env.VUE_APP_API_BASE_URL,
  goTo(view) {
    return router.push(view);
  },
  goToItemEdit(item_id) {
    router.push("/itemEdit/" + item_id);
  }
};
