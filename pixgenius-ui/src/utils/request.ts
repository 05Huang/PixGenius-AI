import axios, {AxiosResponse, InternalAxiosRequestConfig} from "axios";
import {ElMessage} from "element-plus";

const TOKEN_KEY = "accessToken";
// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_PREFIX_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
});

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem(TOKEN_KEY);
    if (accessToken) {
      config.headers.Authorization = accessToken;
    }
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

let isAuthDialogShown = false;

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 检查配置的响应类型是否为二进制类型（'blob' 或 'arraybuffer'）, 如果是，直接返回响应对象
    if (
      response.config.responseType === "blob" ||
      response.config.responseType === "arraybuffer"
    ) {
      return response;
    }

    const { code, data, msg } = response.data;
    if (code === "00000"||code === "200") {
      return data;
    }

    // 如果是敏感词相关的错误（通常包含 400），不在此处弹出全局错误，交给组件处理
    if (msg && String(msg).includes('400')) {
      // suppress global error
    } else {
      ElMessage.error(msg || "系统出错");
    }
    return Promise.reject(new Error(msg || "Error"));
  },
  (error: any) => {
    // 异常处理
    if (error.response && error.response.data) {
      const { code, msg } = error.response.data;
      // 未授权统一处理：A0230 token 失效，A0301 未登录/未授权
      if (code === "A0230" || code === "A0301") {
        if (!isAuthDialogShown) {
          isAuthDialogShown = true;
          // 通知全局应用弹出登录框（App.vue 里监听此事件）
          window.dispatchEvent(new CustomEvent("auth-required"));
          // 简单提示一次
          // ElMessage.warning(msg || "当前页面已失效，请重新登录");
          // 一段时间后允许再次弹出
          setTimeout(() => {
            isAuthDialogShown = false;
          }, 2000);
        }
      } else {
        // 如果是敏感词相关的错误（通常包含 400），不在此处弹出全局错误，交给组件处理
        if (msg && String(msg).includes('400')) {
          // suppress global error
        } else {
          ElMessage.error(msg || "系统出错");
        }
      }
    } else {
      ElMessage.error("网络异常或服务器无响应");
    }
    return Promise.reject(error.message);
  }
);

// 导出 axios 实例
export default service;
