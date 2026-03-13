import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";

// 静态路由：仅保留文生图页面作为主页面
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/t2i",
  },
  {
    path: "/t2i",
    name: "Text2Image",
    component: () => import("@/views/t2i/index.vue"),
    meta: {
      title: "文生图",
    },
  },
];

/**
 * 创建路由
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

export default router;
