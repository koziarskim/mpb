import axios from "axios";

export default axios.create({
  baseURL: "http://localhost/mpb/api",
  headers: {
    "Content-type": "application/json",
    "Access-Control-Allow-Origin": "http://localhost:8081"
  }
});
