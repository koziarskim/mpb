
export default {
  user: {
    season: {},
    year: {}
  },
  getUser() {
    var sessionUser = JSON.parse(window.sessionStorage.getItem("user"));
    if (sessionUser) {
      this.user = sessionUser;
    }
    return this.user;
  },
  setUser(user) {
    window.sessionStorage.setItem("user", JSON.stringify(user));
    this.user = user;
  }
};
