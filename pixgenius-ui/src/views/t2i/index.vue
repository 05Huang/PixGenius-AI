<template>
  <div class="t2i-modern">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <h1 class="main-title">免费文生图AI生成器</h1>
      <p class="subtitle">轻松输入并重塑你的图像，实现任何你想要的效果。</p>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <!-- 左侧：基础工作区 -->
      <div class="panel left-panel">
        <h3 class="panel-title">基础工作区</h3>
        
        <div class="scroll-container">
          <!-- 模型选择 -->
          <div class="config-section">
            <h4 class="section-label">模型</h4>
            <div class="model-selector">
              <div class="model-card" :class="{ active: form.provider === 'sd' }" @click="selectProvider('sd')">
                <div class="model-icon">🎨</div>
                <div class="model-info">
                  <div class="model-name">Stable Diffusion</div>
                  <div class="model-desc">专业级编辑</div>
                </div>
              </div>
              <div class="model-card" :class="{ active: form.provider === 'ali' }" @click="selectProvider('ali')">
                <div class="model-icon">✨</div>
                <div class="model-info">
                  <div class="model-name">通义千问</div>
                  <div class="model-desc">Image-Plus</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 生图风格 -->
          <div class="config-section">
            <h4 class="section-label">生图风格</h4>
            <div class="style-grid">
              <div :class="['style-item', 'real', form.model === 1 ? 'active' : '']" @click="selectModel(1)">
                <div class="style-overlay">
                  <span class="style-name">真实</span>
                </div>
              </div>
              <div :class="['style-item', 'ecy', form.model === 2 ? 'active' : '']" @click="selectModel(2)">
                <div class="style-overlay">
                  <span class="style-name">二次元</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 宽高比 & 张数 -->
          <div class="config-row">
            <div class="config-col">
              <h4 class="section-label">宽高比</h4>
              <div class="ratio-grid">
                <button 
                  v-for="ratio in ratioOptions" 
                  :key="ratio.value"
                  :class="['ratio-btn', { active: form.scale === ratio.value }]"
                  @click="form.scale = ratio.value"
                >
                  {{ ratio.label }}
                </button>
              </div>
            </div>
            <div class="config-col">
              <h4 class="section-label">生成张数</h4>
              <div class="ratio-grid">
                <button 
                  v-for="count in countOptions" 
                  :key="count.value"
                  :class="['ratio-btn', { active: form.size === count.value }]"
                  @click="form.size = count.value"
                >
                  {{ count.label }}
                </button>
              </div>
            </div>
          </div>

          <!-- 提示词输入 -->
          <div class="config-section prompt-section">
            <h4 class="section-label">提示词</h4>
            <textarea 
              class="prompt-input" 
              v-model="form.propmt" 
              placeholder="请输入生图文案"
            ></textarea>
            <div class="char-count">{{ form.propmt.length }}/1000</div>
          </div>

          <!-- 阿里云验证码挂载点 -->
          <div id="captcha-element"></div>
          <button id="ali-captcha-trigger" style="display:none;"></button>

          <!-- 生成按钮 -->
          <button class="generate-btn" @click="sendPropmt">
            生成图片 ({{ form.size * 2 }} 积分)
          </button>
        </div>
      </div>

      <!-- 中间：高级设置 -->
      <div class="panel middle-panel">
        <h3 class="panel-title">高级设置</h3>
        
        <div class="scroll-container">
          <div class="advanced-row">
            <div class="advanced-col">
              <label class="advanced-label">步数: {{ form.step }}</label>
              <el-slider v-model="form.step" :max="30" :min="1" :step="4" show-stops size="small"/>
            </div>
            <div class="advanced-col">
              <label class="advanced-label">强度: {{ form.cfg }}</label>
              <el-slider v-model="form.cfg" :max="10" :min="1" :step="1" show-stops size="small"/>
            </div>
          </div>

          <div class="advanced-row">
            <div class="advanced-col">
              <label class="advanced-label">采样器</label>
              <el-select v-model="form.sampler" placeholder="请选择" size="default" style="width: 100%">
                <el-option
                  v-for="item in samplerOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </div>
            <div class="advanced-col">
              <label class="advanced-label">种子</label>
              <el-input v-model="form.seed" size="default" placeholder="0"/>
            </div>
          </div>

          <div class="advanced-item full-height">
            <label class="advanced-label">负面提示词</label>
            <el-input 
              class="custom-textarea"
              v-model="form.reverse" 
              type="textarea" 
              size="default" 
              placeholder="不希望出现的内容"
              resize="none"
            />
          </div>
        </div>
      </div>

      <!-- 右侧：展示区 -->
      <div class="panel right-panel">
        <h3 class="panel-title">展示区</h3>
        <div class="scroll-container display-container">
          <result-image-view ref="viewer" :images="resultImages"></result-image-view>
        </div>
      </div>
    </div>

    <loading ref="loading" :currentQueueIndex="currentQueueIndex" :queueIndex="queueIndex" :canelGen="canelGen" :proprityTask="proprityTask" :provider="lastProvider" />
  </div>
</template>

<script setup lang="ts">
import ResultImageView from "@/components/ResultImageView.vue";
import {onMounted, ref, nextTick, inject, watch} from "vue";
import Text2ImageAPI from "@/api/t2i";
import { Client } from '@stomp/stompjs';
import Loading from "@/components/Loading.vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {useUserStore} from "@/store";

const loading = ref<Loading>();
const viewer = ref<ResultImageView>();
const userStore = useUserStore();

// 注入限制验证函数
const verifyGenerationLimit = inject('verifyGenerationLimit') as (increment?: boolean) => boolean;

// 表单状态
const form = ref({
  propmt: "",
  provider: 'ali', // sd 或 ali
  size: 1,
  model: 1,
  scale: 1,
  step: 25,
  cfg: 8,
  sampler: 1,
  seed: 0,
  reverse: ""
});

// 选项配置
const ratioOptions = [
  { label: '1:1', value: 1 },
  { label: '3:4', value: 2 },
  { label: '4:3', value: 3 }
];

const countOptions = [
  { label: '1张', value: 1 },
  { label: '2张', value: 2 },
  { label: '3张', value: 3 },
  { label: '4张', value: 4 }
];

const samplerOptions = [
  { value: 1, label: 'DPM++ SDE Karras' },
  { value: 2, label: 'DPM++ 2S Karras' },
  { value: 3, label: 'Euler Karras' },
  { value: 4, label: 'DPM++ 3M SDE Karras' }
];

const pid = ref<String>();
const queueIndex = ref<Number>();//任务序号
const currentQueueIndex = ref<Number>();//当前正在 执行的任务序号
const clientId = ref<String>();
clientId.value = new Date().getTime()+Math.floor(Math.random() * 10000);
const resultImages = ref([]);
const sdEnabled = ref<boolean>(false);
// 记录最近一次生成所使用的引擎（sd / ali），用于决定进度条逻辑
const lastProvider = ref<string>('sd');
// 阿里云验证码 token
const captchaToken = ref<string>("");
let captchaInstance: any = null;

// 监听能力变化
watch(sdEnabled, (enable) => {
  if (!enable) {
    form.value.provider = 'ali';
  }
});

function initAliCaptcha() {
  nextTick(() => {
    // @ts-ignore
    if (window && (window as any).initAliyunCaptcha) {
      // @ts-ignore
      captchaInstance = (window as any).initAliyunCaptcha({
        SceneId: "1paou3fj", // 场景ID
        prefix: "a7to3q", // 身份标
        mode: "popup", // 弹出式
        element: "#captcha-element", // 挂载点
        button: "#ali-captcha-trigger", // 触发元素
        immediate: false,
        region: "cn",
        slideStyle: {
          width: 320,
          height: 40,
        },
        success: function (captchaVerifyParam: any) {
          // 验证通过
          captchaToken.value = captchaVerifyParam;
          ElMessage.success("人机验证通过，正在为您生成图片...");
          // 自动重新发起生图
          sendPropmt();
        },
        fail: function (code: any) {
          // 验证失败
          console.error("验证失败", code);
        }
      });
    }
  });
}

function selectModel(idx: number){
  form.value.model = idx;
}

function selectProvider(provider: string){
  if(provider === 'sd' && !sdEnabled.value){
    ElMessageBox.alert("由于站长经济实力有限😭，买的只是2核2G的轻量云服务器，根本不可能带动Stable Diffusion，所以线上环境只能先选择通义千问 Image-Plus平替一下。", "提示", {
        confirmButtonText: '知道了'
      });
    return;
  }
  form.value.provider = provider;
}

watch(
  () => userStore.user.isLogin,
  (newVal) => {
    if (newVal) {
      fetchCapability();
    }
  }
);

async function fetchCapability(){
  try{
    const res = await Text2ImageAPI.capability();
    sdEnabled.value = res.sdEnabled;
    if(!sdEnabled.value){
      form.value.provider = 'ali';
      ElMessageBox.alert("由于站长经济实力有限😭，买的只是2核2G的轻量云服务器，根本不可能带动Stable Diffusion，所以线上环境只能先选择通义千问 Image-Plus平替一下。", "提示", {
        confirmButtonText: '知道了'
      });
    }
  }catch (e){
    //忽略能力查询失败，使用默认值
  }
}

function sendPropmt() {
  // 检查是否超出限制
  if (verifyGenerationLimit && !verifyGenerationLimit(false)) {
    return;
  }

  let data = {...form.value}; // 复制表单数据
  data.clientId = clientId.value;
  // 计算本次请求使用的引擎
  let provider = data.provider;
  if(!provider){
    provider = sdEnabled.value ? 'sd' : 'ali';
    data.provider = provider;
  }
  lastProvider.value = provider;

  // 人机验证
  if(!captchaToken.value){
    // 触发阿里云验证码
    const btn = document.getElementById('ali-captcha-trigger');
    if (btn) {
       btn.click();
    } else {
       ElMessage.error("验证码初始化失败，请刷新页面重试");
    }
    return;
  }

  // 附加人机验证 token
  // @ts-ignore
  data.captchaToken = captchaToken.value;

  // 增加使用次数 (如果这里返回false，说明刚好被锁定，则不进行生图)
  if (verifyGenerationLimit && !verifyGenerationLimit(true)) {
    return;
  }

  loading.value.openLoading();
  // 阿里百炼走“假进度条”：0 -> 80%，等待后端结果再跳到 100%
  if(provider === 'ali'){
    loading.value.startFakeProgress();
  }
  Text2ImageAPI.propmt(data).then(res => {
    pid.value = res.pid
    queueIndex.value = res.queueIndex
  }).catch(err=>{
    if(provider === 'ali'){
      loading.value.stopFakeProgress();
    }
    loading.value.closeLoading();
    const errorMsg = err?.message || err?.data?.message || "生成失败";
    // 针对400错误的特殊处理，虽然axios通常会把状态码放在err.response.status
    if (errorMsg.includes('400') || (err.response && err.response.status === 400)) {
       ElMessage.error("内容可能包含敏感词或不符合规范，请修改后重试");
    } else {
       ElMessage.error(errorMsg);
    }
  }).finally(() => {
    // 无论成功失败，都重置人机验证状态，以便下次重新验证
    captchaToken.value = "";
    // 如果需要重置验证码实例状态（通常V3不需要显式reset，除非是嵌入式且需要清空）
  });
}

function listImages() {
  let data = {
    pageNum: 1,
    pageSize: 5
  }
  Text2ImageAPI.listImages(data).then(res => {
    res = res.data
    for (let i = 0; i < res.length; i++) {
      resultImages.value.push(res[i].url)
    }
  }).catch(err=>{
  })
}

function canelGen() {
  if(!pid.value){
    ElMessage.warning("当前任务状态不可取消（或任务ID为空）");
    return;
  }
  ElMessageBox.confirm("是否确认要取消生成",{
    title: '取消生成',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(()=>{
    let data = {
      pid: pid.value,
      index: queueIndex.value
    }
    Text2ImageAPI.canelGen(data).then(res => {
      ElMessage.success("取消成功")
      // 取消时停止假进度
      loading.value.stopFakeProgress();
      loading.value.closeLoading();
      queueIndex.value = 0;
      currentQueueIndex.value = 0;
    }).catch(err=>{
      ElMessage.error(err?.message || err?.data?.message || "取消失败");
    })
  })
}

function proprityTask() {
  if(!pid.value){
    ElMessage.warning("当前任务状态不可插队（或任务ID为空）");
    return;
  }
  console.log('原有名次：',queueIndex.value)
  ElMessageBox.confirm("是否确认要花费5积分插队10个名次？",{
    title: '插队生成',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(()=>{
    let data = {
      pid: pid.value,
      index: queueIndex.value
    }
    Text2ImageAPI.proprityTask(data).then(res => {
      queueIndex.value = parseInt(res);
      console.log('插队名次：',queueIndex.value)
      ElMessage.success("插队成功")
    }).catch(err=>{
      ElMessage.error(err?.message || err?.data?.message || "插队失败");
    })
  })
}

// 消息内容
function parseMessage(mes){
console.log('收到消息：',mes)
  const receivedMessage = JSON.parse(mes);
  if(receivedMessage.type == 'imageResult'){
    let temps = receivedMessage.urls
    for (let i = 0; i < temps.length; i++) {
      resultImages.value.unshift(temps[i])
    }
    viewer.value.onSelected(0);//选择中第1张
    if(resultImages.value.length > 20){
      resultImages.value.splice(20,resultImages.value.length-20)
    }
    if(lastProvider.value === 'ali'){
      loading.value.finishByResult();
    }else{
      loading.value.closeLoading();
    }
  }else if("execution_error"==receivedMessage.type){
    loading.value.stopFakeProgress();
    ElMessage.error(receivedMessage.exception_message || "系统出错");
  }else if("progress"==receivedMessage.type){
    loading.value.updateProgress(receivedMessage.value*100/receivedMessage.max);
  }else if("index"==receivedMessage.type){
    currentQueueIndex.value=receivedMessage.value;
  }else if("start"==receivedMessage.type){
    loading.value.startTask();
  }
}
onMounted(()=>{
  const client = new Client({
    brokerURL: import.meta.env.VITE_WS_HOST_URL,
    connectHeaders: {
      clientId: clientId.value
    },
    reconnectDelay: 5000,
    debug: (str) => { console.log('STOMP:', str); },
    onConnect: () => {
      console.log(111,"ok")
      client.subscribe('/topic/messages', message =>
          parseMessage(message.body)
      );
      // Spring STOMP 用户目的地：convertAndSendToUser(clientId, "/topic/messages")
      // 对应客户端订阅路径为 "/user/topic/messages"
      client.subscribe('/user/topic/messages', message =>
          parseMessage(message.body)
      );
    },
  });
  client.activate();

  fetchCapability();
  listImages();
  initAliCaptcha();
})
</script>

<style scoped>
.t2i-modern {
  width: 100%;
  padding: 16px 64px;
  height: calc(100vh - 88px); /* 适配顶部导航栏 */
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

/* 页面标题区域 */
.page-header {
  text-align: center;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.main-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

/* 主要内容区域 - 3列布局 */
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr 2fr;
  gap: 24px;
  /* 关键：拉伸对齐，使容器高度一致 */
  align-items: stretch;
  flex: 1;
  min-height: 0; /* 防止grid溢出 */
}

/* 面板通用样式 */
.panel {
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden; /* 内部滚动 */
}

.panel-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

/* 自定义滚动条 - 隐藏滚动条但保留滚动功能 */
.scroll-container::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}
.scroll-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden; /* 禁止横向滚动 */
  padding-right: 0; /* 移除右侧内边距 */
  scrollbar-width: none; /* Firefox 隐藏滚动条 */
  -ms-overflow-style: none; /* IE/Edge 隐藏滚动条 */
  
  /* Flex 布局实现垂直方向均匀分布 */
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 配置区域样式 */
.config-section {
  margin-bottom: 0; /* 由 gap 控制间距 */
}

.prompt-section {
  flex: 1; /* 自动填充剩余空间 */
  display: flex;
  flex-direction: column;
  min-height: 100px; /* 最小高度 */
}

.config-row {
  display: flex;
  gap: 12px;
  margin-bottom: 0; /* 由 gap 控制间距 */
}

.config-col {
  flex: 1;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 4px 0;
}

/* 模型选择 */
.model-selector {
  display: flex;
  flex-direction: row;
  gap: 8px;
}

.model-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 8px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.model-card:hover {
  border-color: #14b8a6;
  background: #f0fdfa;
}

.model-card.active {
  border-color: #14b8a6;
  background: #ccfbf1;
}

.model-icon {
  font-size: 20px;
  margin-right: 0;
  margin-bottom: 4px;
}

.model-name {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
}

.model-desc {
  font-size: 11px;
  color: #6b7280;
}

/* 风格网格 */
.style-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.style-item {
  position: relative;
  height: 60px;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
  cursor: pointer;
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.style-item.active {
  border-color: #14b8a6;
  box-shadow: 0 0 0 2px rgba(20, 184, 166, 0.2);
}

.style-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 4px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  text-align: center;
}

.style-name {
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.real {
  background-image: url("../../assets/model/model_real.webp");
}

.ecy {
  background-image: url("../../assets/model/model_anime.webp");
}

/* 比例和张数按钮 */
.ratio-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px;
}

.ratio-btn {
  padding: 6px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  font-size: 12px;
  font-weight: 500;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s;
}

.ratio-btn:hover {
  border-color: #14b8a6;
  background: #f0fdfa;
}

.ratio-btn.active {
  border-color: #14b8a6;
  background: #14b8a6;
  color: white;
}

/* 提示词输入 */
.prompt-input {
  width: 100%;
  padding: 10px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.5;
  resize: none; /* 禁止拖拽调整大小 */
  font-family: inherit;
  box-sizing: border-box;
  flex: 1; /* 填充父容器高度 */
  height: 100%; /* 确保高度生效 */
}

.prompt-input:focus {
  outline: none;
  border-color: #14b8a6;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
  flex-shrink: 0;
}

/* 生成按钮 */
.generate-btn {
  width: 100%;
  margin-top: 0; /* 由 gap 控制 */
  padding: 12px;
  background: #14b8a6;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 6px -1px rgba(20, 184, 166, 0.3);
  flex-shrink: 0;
}

.generate-btn:hover {
  background: #0d9488;
  transform: translateY(-1px);
}

/* 高级设置项 */
.advanced-item {
  margin-bottom: 0; /* 由 gap 控制 */
}

.advanced-item.full-height {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100px;
}

.custom-textarea {
  flex: 1;
  height: 100%;
}

.custom-textarea :deep(.el-textarea__inner) {
  height: 100% !important;
}

.advanced-row {
  display: flex;
  gap: 12px;
  margin-bottom: 0; /* 由 gap 控制 */
}

.advanced-col {
  flex: 1;
  min-width: 0;
}

.advanced-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

/* 展示区 */
.display-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 覆盖 Element Plus 样式 */
:deep(.el-slider) {
  margin: 0 6px; /* 减小左右边距防止溢出 */
}
:deep(.el-slider__runway) {
  background-color: #e5e7eb;
}
:deep(.el-slider__bar) {
  background-color: #14b8a6;
}
:deep(.el-slider__button) {
  border-color: #14b8a6;
}
:deep(.el-input__wrapper) {
  border-radius: 6px;
}
:deep(.el-textarea__inner) {
  border-radius: 6px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 280px 260px 1fr;
    gap: 16px;
  }
}

@media (max-width: 1024px) {
  .t2i-modern {
    height: auto;
    padding: 16px;
  }
  
  .content-wrapper {
    grid-template-columns: 1fr;
    height: auto;
    display: flex;
    flex-direction: column;
  }
  
  .panel {
    height: auto;
    overflow: visible;
  }
  
  .scroll-container {
    overflow: visible;
  }

  /* 修复平板/移动端下风格卡片被拉伸变形的问题 */
  .style-item {
    height: auto;
    aspect-ratio: 2.5;
  }
}
</style>