import session from "./session.js";

export default {
  hasRole(codes) {
    var found = false;
    this.getUser().roles.forEach(ur => {
      if (ur.code == "ADMIN") {
        found = true;
        return;
      }
      codes.forEach(code => {
        if (ur.code === code) {
          found = true;
          return;
        }
      })
    });
    return found;
  },
  getUser() {
    return session.getUser();
  }
};
