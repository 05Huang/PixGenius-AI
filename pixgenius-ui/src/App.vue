<template>
  <!-- 密码保护 -->
  <password-protection ref="passwordProtection" @unlock="onUnlock" />
  
  <!-- 现代化顶部导航栏 -->
  <div class="modern-header">
    <div class="header-container">
      <div class="logo-section">
        <div class="logo-text">
          <span class="logo-brand">PixGenius</span>
          <span class="logo-separator">·</span>
          <span class="logo-description">AI智能图像创作平台</span>
        </div>
      </div>
      <div class="nav-actions">
        <!-- 积分显示 -->
        <div class="credits-display" v-if="userStore.user.isLogin">
          <svg class="credit-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span class="credit-number">{{ credits }}</span>
        </div>
        <!-- 登录状态 -->
        <el-popover
          v-if="userStore.user.isLogin"
          :width="200"
          placement="bottom-end"
          popper-style="box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 6px;"
        >
          <template #reference>
            <div class="user-avatar-wrapper">
              <el-avatar :src="userStore.user.avatar" :size="36" />
            </div>
          </template>
          <template #default>
            <div class="userinfo">
              <div class="user-menu-item">
                <el-avatar :src="userStore.user.avatar" :size="24" />
                <span class="user-name">{{ userStore.user.userName }}</span>
              </div>
              <div class="user-menu-divider"></div>
              <div class="user-menu-item logout" @click="logout">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </div>
            </div>
          </template>
        </el-popover>
        <button class="login-btn" v-else @click="showLogin">登录</button>
      </div>
    </div>
  </div>

  <!-- 主内容区域 -->
  <div class="main-content">
    <T2iView />
    <div class="beian-footer">
      <a href="https://beian.miit.gov.cn/" target="_blank">苏ICP备2025167047号-3</a>
    </div>
  </div>

  <login ref="login" />
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, watch, provide } from "vue";
import Login from "@/components/Login.vue";
import PasswordProtection from "@/components/PasswordProtection.vue";
import { useUserStore } from "@/store";
import { ElMessageBox, ElMessage } from "element-plus";
import { SwitchButton } from "@element-plus/icons-vue";
import T2iView from "@/views/t2i/index.vue";
import UserAPI from "@/api/user";

const userStore = useUserStore();
const credits = ref(0); // 积分显示
const passwordProtection = ref<InstanceType<typeof PasswordProtection>>();

// 验证生图限制
function verifyGenerationLimit(shouldIncrement: boolean = false): boolean {
  const MAX_COUNT = 5;
  const count = parseInt(localStorage.getItem('gen_count') || '0');
  
  if (count >= MAX_COUNT) {
    passwordProtection.value?.lock("你的生成图片过多，您的ip已被锁定，输入密码继续使用");
    return false;
  }
  
  if (shouldIncrement) {
    localStorage.setItem('gen_count', (count + 1).toString());
  }
  return true;
}

// 解锁回调
function onUnlock() {
  localStorage.setItem('gen_count', '0');
  ElMessage.success("解锁成功，已重置生成次数");
}

provide('verifyGenerationLimit', verifyGenerationLimit);

const login = ref<Login>();
function showLogin() {
  login.value?.openLogin();
}

async function fetchCredits() {
  if (userStore.user.isLogin) {
    try {
      const res = await UserAPI.getFund();
      // 响应拦截器已经解包了 data，所以这里直接获取 res.score
      if (res && res.score !== undefined) {
        credits.value = res.score;
      }
    } catch (e) {
      console.error("获取积分失败", e);
    }
  }
}

watch(
  () => userStore.user.isLogin,
  (newVal) => {
    if (newVal) {
      fetchCredits();
    }
  }
);

onMounted(() => {
  // 页面加载时检查是否已锁定
  verifyGenerationLimit(false);

  if (userStore.user.isLogin) {
    fetchCredits();
  }
  // 未登录或 token 失效时，由请求拦截器发出 auth-required 事件，这里统一弹出登录框
  const handler = () => {
    showLogin();
  };
  window.addEventListener("auth-required", handler);
  // 保存到实例上方便卸载时移除监听
  // @ts-ignore
  window.__authRequiredHandler__ = handler;
});

onBeforeUnmount(() => {
  // @ts-ignore
  const handler = window.__authRequiredHandler__;
  if (handler) {
    window.removeEventListener("auth-required", handler);
  }
});

function logout() {
  ElMessageBox.confirm("确定退出登录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    userStore.logout();
  });
}
</script>

<style scoped>
/* 现代化导航栏样式 */
.modern-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  z-index: 1000;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.header-container {
  width: 100%;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo-text {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.logo-brand {
  font-family: 'Poppins', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #14b8a6 0%, #06b6d4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.logo-separator {
  font-size: 18px;
  color: #d1d5db;
  font-weight: 300;
}

.logo-description {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  letter-spacing: 0.5px;
}

.logo-text:hover .logo-brand {
  background: linear-gradient(135deg, #0d9488 0%, #0891b2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-text:hover .logo-description {
  color: #14b8a6;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.credits-display {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f3f4f6;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.credit-icon {
  width: 20px;
  height: 20px;
  color: #10b981;
}

.credit-number {
  font-weight: 600;
  color: #10b981;
}

.user-avatar-wrapper {
  cursor: pointer;
  border-radius: 50%;
  transition: transform 0.2s;
}

.user-avatar-wrapper:hover {
  transform: scale(1.05);
}

.login-btn {
  padding: 8px 24px;
  background: #14b8a6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #0d9488;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(20, 184, 166, 0.4);
}

.main-content {
  margin-top: 64px;
  background: #f9fafb;
  min-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
}

.beian-footer {
  text-align: center;
  padding: 8px 0;
  font-size: 12px;
  color: #9ca3af;
  margin-top: auto;
}

.beian-footer a {
  color: #9ca3af;
  text-decoration: none;
  transition: color 0.3s;
}

.beian-footer a:hover {
  color: #14b8a6;
}


.userinfo {
  display: flex;
  flex-direction: column;
}

.user-menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
}

.user-menu-item:hover {
  background-color: #f3f4f6;
}

.user-menu-item.logout {
  color: #ef4444;
}

.user-menu-item.logout:hover {
  background-color: #fee2e2;
}

.user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-menu-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 4px 0;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .header-container {
    padding: 0 12px;
    height: 56px;
  }
  
  .main-content {
    margin-top: 56px;
    min-height: calc(100vh - 56px);
  }
  
  .logo-brand {
    font-size: 20px;
  }
  
  .logo-description, .logo-separator {
    display: none;
  }
  
  .nav-actions {
    gap: 12px;
  }
  
  .credit-number {
    font-size: 14px;
  }
  
  .credits-display {
    padding: 4px 8px;
  }

  .login-btn {
    padding: 6px 12px;
    font-size: 13px;
  }
}
</style>
