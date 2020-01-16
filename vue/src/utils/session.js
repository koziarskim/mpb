
export default {
  user: {
    season: {},
    year: {}
  },
  getUser() {
    this.user = JSON.parse(window.localStorage.getItem("user"));
    return this.user;
  },
  setUser(user) {
    window.localStorage.setItem("user", JSON.stringify(user));
    this.user = user;
  }
};
