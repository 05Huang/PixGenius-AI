<template>
  <div class="modern-riv">
    <!-- 空状态或图片展示 -->
    <div v-if="images.length === 0" class="empty-state">
      <div class="empty-animation">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
        </div>
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <p class="empty-text">输入提示词，开始创作吧</p>
      </div>
    </div>
    
    <div v-else class="image-display">
      <!-- 主图展示 -->
      <div class="main-image-container" :class="{ 'shrink': images.length > 1 }">
        <img :src="images[active]" :key="active" class="main-image" />
      </div>
      
      <!-- 下载按钮 -->
      <button class="download-btn" @click="downloadImage">
        <svg class="download-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        下载
      </button>
      
      <!-- 缩略图列表 -->
      <div v-if="images.length > 1" class="thumbnail-list">
        <div 
          v-for="(item, index) in images" 
          :key="index"
          :class="['thumbnail-item', { active: index === active }]"
          @click="onSelected(index)"
        >
          <img :src="item" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";

const props = defineProps({
  images: {
    type: Array,
    required: true
  }
});

const active = ref(0);

function onSelected(index){
  active.value = index;
}

function downloadImage() {
  if (props.images[active.value]) {
    const link = document.createElement('a');
    link.href = props.images[active.value];
    link.download = `generated-image-${Date.now()}.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}

defineExpose({onSelected})
</script>

<style scoped>
.modern-riv {
  width: 100%;
  height: 100%;
  min-height: 600px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 空状态 */
.empty-state {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.empty-animation {
  text-align: center;
  position: relative;
  z-index: 1;
}

.floating-shapes {
  position: absolute;
  width: 300px;
  height: 300px;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #14b8a6, #0d9488);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  top: 60%;
  left: 70%;
  animation-delay: 1s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  top: 70%;
  left: 15%;
  animation-delay: 2s;
}

.shape-4 {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #ec4899, #db2777);
  top: 20%;
  left: 75%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.1);
  }
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  color: #d1d5db;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: 16px;
  color: #9ca3af;
  margin: 0;
}

/* 图片展示区域 */
.image-display {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.main-image-container {
  width: 100%;
  max-width: 700px;
  aspect-ratio: 1;
  background: #f9fafb;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: max-width 0.3s ease;
}

.main-image-container.shrink {
  max-width: 500px;
}

.main-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 下载按钮 */
.download-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  background: #14b8a6;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.download-btn:hover {
  background: #0d9488;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(20, 184, 166, 0.3);
}

.download-icon {
  width: 20px;
  height: 20px;
}

/* 缩略图列表 */
.thumbnail-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 100%;
  overflow-x: auto;
  padding: 8px 0;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s;
  background: #f3f4f6;
}

.thumbnail-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.thumbnail-item.active {
  border-color: #14b8a6;
  box-shadow: 0 0 0 3px rgba(20, 184, 166, 0.2);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 滚动条样式 */
.thumbnail-list::-webkit-scrollbar {
  height: 6px;
}

.thumbnail-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.thumbnail-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>