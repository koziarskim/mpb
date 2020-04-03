import axios from "axios";
import httpUtils from "./httpUtils";
import router from "./router";
import NProgress from 'nprogress';
import securite from "./securite";

const http = axios.create({
  baseURL: httpUtils.baseUrl
});

http.interceptors.request.use(
  config => {
    NProgress.start();
    var user = securite.getUser();
    if (user.year) {
      if (!config.params) {
        config.params = {};
      }
      config.params.yearContext = user.year.name;
    }
    return config;
  },
  error => {
    console.log("Interceptor Request Error: " + error);
    alert("There was an error! /n"+error);
    return Promise.reject(error);
  }
);

http.interceptors.response.use(
  response => {
    NProgress.done();
    return response;
  },
  error => {
    console.log("Interceptor Response Error: " + error);
    console.log(error.response.data.message);
    if (error.response.status === 401) {
      router.push("/login");
      NProgress.done();
    } else {
      alert("There was an error!\n" + error.response.data);
      NProgress.done();
    }
    return Promise.reject(error);
  }
);

export default http;
