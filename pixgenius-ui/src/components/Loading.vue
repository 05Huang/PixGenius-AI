<template>
  <div class="load-mask" v-if="show">
    <div class="load-body">
      <div class="load-icon">
        <video src="../assets/images/loading.mp4" autoplay loop muted width="120" style="border-radius: 6px" ></video>
      </div>
      <div class="load-text">
        <div class="text">请稍后，您的图片正在生成中...</div>
        <el-progress
            class="lprogress"
            :text-inside="true"
            :stroke-width="15"
            :percentage="progress"
            status="exception"
        />
        <div class="queue" v-if="currentQueueIndex>0">在您前面有 <label>{{queueIndex-currentQueueIndex}}</label> 人在排队，请耐心等待</div>
        <div class="queue" v-else-if="started">您的图片生成正在生成，请耐心等待</div>
        <div class="queue" v-else>在您的任务序号是 <label>{{queueIndex}}</label> ，请耐心等待</div>
        <div class="button" v-if="!started && provider === 'sd'">
          <div class="but-blue" style="background-color: #888888" @click="canelGen">取消生成</div>
          <div class="but-blue" @click="proprityTask">插队生成</div>
        </div>
        <div class="button" v-if="provider === 'ali'">
           <!-- 阿里模式下不支持取消和插队，显示提示或其他内容，或者直接留空 -->
           <div style="font-size: 12px; color: #999; text-align: center; width: 100%;">
             云端极速生成中，请勿关闭页面
           </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";

defineProps({
  proprityTask:{
    type: Function,
    required: true
  },
  canelGen:{
    type: Function,
    required: true
  },
  currentQueueIndex: {
    type: Number,
    default: 0,
    required: true
  },
  queueIndex: {
    type: Number,
    default: 0,
    required: true
  },
  provider: {
    type: String,
    default: 'sd'
  }
});


const show = ref(false)
const progress = ref(0)
const started = ref(false)
// 用于阿里百炼模式的“假进度条”定时器
let fakeTimer: number | null = null

function openLoading() {
  started.value = false
  progress.value = 0
  show.value = true
}
function closeLoading() {
  show.value = false
  started.value = false
  // 关闭时顺便清理假进度
  if(fakeTimer){
    clearInterval(fakeTimer)
    fakeTimer = null
  }
}
function startTask() {
  started.value = true
}
function updateProgress(num) {
  progress.value = parseInt(num)
  if(num==100){
    closeLoading();
  }
}

// 阿里云百炼模式下，前端自行模拟进度：0 -> 80%
function startFakeProgress(){
  // 保证遮罩已打开
  show.value = true
  progress.value = 0
  if(fakeTimer){
    clearInterval(fakeTimer)
  }
  fakeTimer = window.setInterval(()=>{
    if(progress.value < 80){
      progress.value += 1
    }else{
      if(fakeTimer){
        clearInterval(fakeTimer)
        fakeTimer = null
      }
    }
  },200)
}

// 生图完成时（收到 imageResult），直接跳到 100% 并关闭
function finishByResult(){
  if(fakeTimer){
    clearInterval(fakeTimer)
    fakeTimer = null
  }
  updateProgress(100)
}

// 出错或取消时，停止假进度，但由调用方决定是否关闭弹窗
function stopFakeProgress(){
  if(fakeTimer){
    clearInterval(fakeTimer)
    fakeTimer = null
  }
}

defineExpose({ openLoading,closeLoading,updateProgress,startTask,startFakeProgress,finishByResult,stopFakeProgress});

</script>

<style scoped>
.load-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 1900;
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

.load-body {
  display: flex;
  width: 360px;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 16px;
  background-color: #FFFFFF;
  padding: 32px 24px;
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

.load-icon {
  margin-bottom: 16px;
}

.load-text {
  display: flex;
  flex-direction: column;
  align-content: center;
  justify-content: center;
  text-align: center;
  width: 100%;
  
  .text {
    color: #111827;
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
  }
  
  .lprogress {
    width: 100%;
    margin-bottom: 12px;
  }
  
  .queue {
    color: #6b7280;
    font-size: 13px;
    line-height: 1.6;
    margin-top: 8px;
    
    label {
      color: #14b8a6;
      font-weight: 600;
    }
  }
  
  .button {
    display: flex;
    gap: 12px;
    margin-top: 20px;
    width: 100%;
  }
  
  .button .but-blue {
    flex: 1;
    margin: 0;
  }
}

/* 覆盖 Element Plus 进度条样式 */
:deep(.el-progress__text) {
  font-size: 14px !important;
  font-weight: 600;
}

:deep(.el-progress-bar__outer) {
  background-color: #e5e7eb;
}

:deep(.el-progress-bar__inner) {
  background: linear-gradient(90deg, #14b8a6, #06b6d4);
}
</style>