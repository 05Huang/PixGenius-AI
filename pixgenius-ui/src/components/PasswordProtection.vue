<template>
  <div v-if="!isAuthenticated" class="password-mask">
    <div class="password-container">
      <div class="password-card" @mousedown.stop @click.stop>
        <div class="lock-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2C9.243 2 7 4.243 7 7v3H6c-1.103 0-2 .897-2 2v8c0 1.103.897 2 2 2h12c1.103 0 2-.897 2-2v-8c0-1.103-.897-2-2-2h-1V7c0-2.757-2.243-5-5-5zm-1 13.723V18h2v-2.277A1.993 1.993 0 0014 14a2 2 0 10-3 1.723zM9 10V7c0-1.654 1.346-3 3-3s3 1.346 3 3v3H9z" fill="currentColor"/>
          </svg>
        </div>
        <h2 class="password-title">{{ title }}</h2>
        <p class="password-subtitle">{{ subtitle }}</p>
        
        <div class="password-input-wrapper">
          <input 
            v-model="password" 
            type="password" 
            class="password-input"
            placeholder="请输入访问密码"
            @keyup.enter="verifyPassword"
            ref="passwordInput"
          />
        </div>
        
        <button 
          class="verify-btn" 
          @click="verifyPassword"
          :disabled="loading || !password"
        >
          <span v-if="!loading">验证</span>
          <span v-else class="loading-spinner">验证中...</span>
        </button>
        
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineExpose } from 'vue';
import request from '@/utils/request';

const isAuthenticated = ref(true); // 默认为true，允许访问
const passwordInput = ref<HTMLInputElement | null>(null);
const password = ref('');
const loading = ref(false);
const errorMessage = ref('');
const title = ref('访问验证');
const subtitle = ref('请输入访问密码以继续');

const emit = defineEmits(['unlock']);

// 锁定屏幕
const lock = (msg?: string) => {
  isAuthenticated.value = false;
  if (msg) {
    subtitle.value = msg;
    title.value = "IP已被锁定";
  }
  // 自动聚焦，解决光标丢失问题
  setTimeout(() => {
    passwordInput.value?.focus();
  }, 100);
};

// 验证密码
const verifyPassword = async () => {
  if (!password.value) {
    errorMessage.value = '请输入密码';
    return;
  }
  
  loading.value = true;
  errorMessage.value = '';
  
  try {
    const response = await request({
      url: '/api/1.0/auth/verify-password',
      method: 'post',
      data: {
        password: password.value
      }
    });
    
    if (response.success) {
      // 验证成功
      isAuthenticated.value = true;
      emit('unlock'); // 触发解锁事件
    } else {
      errorMessage.value = '密码错误，请重试';
      password.value = '';
      // 重新聚焦
      setTimeout(() => {
        passwordInput.value?.focus();
      }, 0);
    }
  } catch (error: any) {
    errorMessage.value = error?.message || error?.data?.message || '验证失败，请重试';
    password.value = '';
  } finally {
    loading.value = false;
  }
};

defineExpose({ lock, isAuthenticated });
</script>

<style scoped>
.password-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: #f9fafb;
  z-index: 99999;
  display: flex;
  align-items: center;
  justify-content: center;
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

.password-container {
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

.password-card {
  background: white;
  border-radius: 16px;
  padding: 48px 40px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  text-align: center;
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

.lock-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 24px;
  background: #14b8a6;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.lock-icon svg {
  width: 32px;
  height: 32px;
}

.password-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.password-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.password-input-wrapper {
  margin-bottom: 20px;
}

.password-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s;
  box-sizing: border-box;
  background: #fafafa;
}

.password-input:focus {
  outline: none;
  border-color: #14b8a6;
  background: white;
  box-shadow: 0 0 0 3px rgba(20, 184, 166, 0.1);
}

.verify-btn {
  width: 100%;
  padding: 12px 24px;
  background: #14b8a6;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.verify-btn:hover:not(:disabled) {
  background: #0d9488;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(20, 184, 166, 0.3);
}

.verify-btn:active:not(:disabled) {
  transform: translateY(0);
}

.verify-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
}

.error-message {
  margin-top: 16px;
  padding: 10px 16px;
  background: #fee2e2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #dc2626;
  font-size: 13px;
  animation: shake 0.4s ease;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-8px);
  }
  75% {
    transform: translateX(8px);
  }
}

/* 响应式 */
@media (max-width: 640px) {
  .password-card {
    padding: 36px 24px;
  }
  
  .password-title {
    font-size: 22px;
  }
  
  .lock-icon {
    width: 56px;
    height: 56px;
  }
  
  .lock-icon svg {
    width: 28px;
    height: 28px;
  }
}
</style>

