import axios from "axios";
import httpUtils from "./httpUtils";
import router from "./router";
import NProgress from 'nprogress';

const http = axios.create({
  baseURL: httpUtils.baseUrl
});

http.interceptors.request.use(
  config => {
    NProgress.start();
    return config;
  },
  error => {
    console.log("Interceptor Request Error: " + error);
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
    }
    return Promise.reject(error);
  }
);

export default http;
