import session from "./utils/session";

export default {
  hasRole(codes) {
    var found = false;
    if (!this.getUser().roles) {
      return false;
    }
    this.getUser().roles.forEach(ur => {
      if (ur.code == "ADMIN") {
        found = true;
        return;
      }
      codes.forEach(code => {
        if (ur.code == code) {
          found = true;
          return;
        }
      });
    })
    return found;
  },
  getUser() {
    return session.getUser();
    // return JSON.parse(window.localStorage.getItem("user"));
  },
  setUser(user){
    session.setUser(user);
    // window.localStorage.setItem("user", JSON.stringify(user));
  }
};
