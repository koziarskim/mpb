import axios from "axios";
import httpUtils from "./httpUtils";
import router from "./router";

const http = axios.create({
  baseURL: httpUtils.baseUrl
  //   headers: {
  //     "Content-type": "application/json",
  //     "Access-Control-Allow-Origin": "*",
  //     "Access-Control-Allow-Methods": "GET, OPTIONS, HEAD, PUT, POST, DELETE",
  //     "Access-Control-Allow-Credentials": true,
  //     "Access-Control-Allow-Headers":
  //       "Access-Control-Allow-Headers, Access-Control-Allow-Origin, Access-Control-Allow-Methods"
  //   }
});

http.interceptors.request.use(
  config => {
    return config;
  },
  error => {
    console.log("Interceptor Request Error: " + error);
    return Promise.reject(error);
  }
);

http.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    console.log("Interceptor Response Error: " + error);
    // console.log("Interceptor Response Status: " + error.response.data.status);
    if (error.response.data.status === 401) {
      router.push("/login");
    }
    return Promise.reject(error);
  }
);

export default http;
