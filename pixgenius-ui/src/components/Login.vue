<template>
  <div class="login-mask" v-if="show">
    <div class="login-body">
      <div class="login-icon"></div>
      <div class="login-right">
        <div class="login-close" @click="show=false"></div>
        <div class="login-area">
          <div class="l-title">用户登录</div>
          <div class="fp-body"><label>账号</label><input class="input" v-model="form.username" placeholder="请输入用户名"/></div>
          <div class="fp-body"><label>密码</label><input class="input" v-model="form.password" type="password" placeholder="请输入8-20位密码"/></div>
          <div class="l-button" @click="handleLogin">立即登录</div>
          <div class="l-tip">
            <p>已自动填充内置账号密码，直接点击登录按钮即可</p>
            <p>网站开发者：黄暄</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {ElMessage} from "element-plus";
import UserAPI from "@/api/user";
import {useUserStore} from "@/store";
const show = ref(false)

const userStore = useUserStore();
const form = ref({
  username: 'admin',
  password: 'admin'
})

function openLogin() {
  show.value = true
}

function handleLogin(){
  if(form.value.username && form.value.password){
    UserAPI.login(form.value).then(res => {
      localStorage.setItem('accessToken',res.token)
      userStore.login(res)
      show.value = false
    });
  }else{
    ElMessage.error('请输入用户名或密码')
  }
}

defineExpose({ openLogin });

</script>

<style scoped>
.login-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 1500;
  display: flex;
  justify-content: center;
  align-items: center;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.login-body {
  display: flex;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.login-right {
  width: 400px;
  display: flex;
  flex-direction: column;
  position: relative;
  
  .login-area {
    background-color: #FFFFFF;
    width: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: column;
    padding: 40px;
    
    .l-title {
      font-weight: 700;
      font-size: 28px;
      color: #111827;
      letter-spacing: -0.5px;
      text-align: center;
      margin-bottom: 32px;
    }
    
    .l-button {
      width: 100%;
      height: 44px;
      background: #14b8a6;
      line-height: 44px;
      border-radius: 10px;
      text-align: center;
      font-weight: 600;
      font-size: 15px;
      color: #FFFFFF;
      cursor: pointer;
      margin-top: 24px;
      transition: all 0.3s;
    }
    
    .l-button:hover {
      background: #0d9488;
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(20, 184, 166, 0.3);
    }

    .l-tip {
      margin-top: 16px;
      text-align: center;
      
      p {
        font-size: 12px;
        color: #9ca3af;
        margin: 4px 0;
        line-height: 1.5;
      }
    }
  }
  
  .login-close {
    background: url("../assets/images/Close.png");
    background-size: 100% 100%;
    width: 20px;
    height: 20px;
    position: absolute;
    top: 16px;
    right: 16px;
    cursor: pointer;
    opacity: 0.6;
    transition: opacity 0.3s;
  }
  
  .login-close:hover {
    opacity: 1;
  }
}

.login-icon {
  background: url("../assets/images/img_denglu.webp");
  background-size: cover;
  background-position: center;
  width: 320px;
  min-height: 400px;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-body {
    width: 90%;
    max-width: 400px;
  }
  
  .login-icon {
    display: none;
  }
  
  .login-right {
    width: 100%;
  }
}
</style>