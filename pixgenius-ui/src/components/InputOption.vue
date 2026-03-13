<template>
  <div class="modern-io">
    <!-- 模型选择 -->
    <div class="config-section">
      <h3 class="section-title">模型</h3>
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
      <h3 class="section-title">生图风格</h3>
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

    <!-- 宽高比 -->
    <div class="config-section">
      <h3 class="section-title">宽高比</h3>
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

    <!-- 张数 -->
    <div class="config-section">
      <h3 class="section-title">生成张数</h3>
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

    <!-- 高级设置折叠面板 -->
    <div class="config-section">
      <div class="advanced-header" @click="showAdvanced = !showAdvanced">
        <h3 class="section-title">高级设置</h3>
        <span class="toggle-icon">{{ showAdvanced ? '−' : '+' }}</span>
      </div>
      <div v-show="showAdvanced" class="advanced-content">
        <div class="advanced-item">
          <label class="advanced-label">步数: {{ form.step }}</label>
          <el-slider v-model="form.step" :max="30" :min="1" :step="4" show-stops/>
        </div>
        <div class="advanced-item">
          <label class="advanced-label">强度: {{ form.cfg }}</label>
          <el-slider v-model="form.cfg" :max="10" :min="1" :step="1" show-stops/>
        </div>
        <div class="advanced-item">
          <label class="advanced-label">采样器</label>
          <el-select v-model="form.sampler" placeholder="请选择采样器" size="default" style="width: 100%">
            <el-option
              v-for="item in samplerOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div class="advanced-item">
          <label class="advanced-label">种子 (0为随机)</label>
          <el-input v-model="form.seed" size="default" placeholder="0"/>
        </div>
        <div class="advanced-item">
          <label class="advanced-label">负面提示词</label>
          <el-input v-model="form.reverse" type="textarea" :rows="2" size="default" placeholder="不希望出现的内容"/>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {ref, watch} from "vue";
import {ElMessage} from "element-plus";

const props = defineProps<{
  sdEnabled: boolean
}>();

const showAdvanced = ref(false);
const form= ref({
  provider: 'sd', // sd 或 ali
  size: 1,
  model:1,
  scale:1,
  step:25,
  cfg:8,
  sampler: 1,
  seed: 0,
  reverse:""
});

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
  {
    value: 1,
    label: 'DPM++ SDE Karras'
  },
  {
    value: 2,
    label: 'DPM++ 2S Karras'
  },
  {
    value: 3,
    label: 'Euler Karras'
  },
  {
    value: 4,
    label: 'DPM++ 3M SDE Karras'
  }
]

function selectModel(idx){
  form.value.model = idx;
}

function selectProvider(provider: string){
  if(provider === 'sd' && !props.sdEnabled){
    ElMessage.warning("当前云环境不支持原生 Stable Diffusion，请选择通义千问 Image-Plus");
    return;
  }
  form.value.provider = provider;
}

function getFormData(){
  return form.value
}

function setProvider(val: string){
  form.value.provider = val;
}

watch(() => props.sdEnabled, (enable)=>{
  if(!enable){
    form.value.provider = 'ali';
  }
})

defineExpose({getFormData,setProvider})
</script>

<style scoped>
.modern-io {
  width: 100%;
}

/* 配置区块 */
.config-section {
  margin-bottom: 24px;
}

.config-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px 0;
}

/* 模型选择卡片 */
.model-selector {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-card {
  display: flex;
  align-items: center;
  padding: 12px;
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
  font-size: 24px;
  margin-right: 12px;
}

.model-info {
  flex: 1;
}

.model-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 2px;
}

.model-desc {
  font-size: 12px;
  color: #6b7280;
}

/* 风格网格 */
.style-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.style-item {
  position: relative;
  height: 100px;
  border-radius: 10px;
  background-size: cover;
  background-position: center;
  cursor: pointer;
  overflow: hidden;
  border: 3px solid transparent;
  transition: all 0.3s;
}

.style-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.style-item.active {
  border-color: #14b8a6;
  box-shadow: 0 0 0 3px rgba(20, 184, 166, 0.2);
}

.style-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  display: flex;
  align-items: center;
  justify-content: center;
}

.style-name {
  color: white;
  font-size: 13px;
  font-weight: 600;
}

.real {
  background-image: url("../assets/model/model_real.webp");
}

.ecy {
  background-image: url("../assets/model/model_anime.webp");
}

/* 比例按钮网格 */
.ratio-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.ratio-btn {
  padding: 8px 12px;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  font-size: 13px;
  font-weight: 500;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.3s;
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

/* 高级设置 */
.advanced-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.toggle-icon {
  font-size: 20px;
  font-weight: 600;
  color: #6b7280;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background 0.3s;
}

.toggle-icon:hover {
  background: #f3f4f6;
}

.advanced-content {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.advanced-item {
  margin-bottom: 16px;
}

.advanced-item:last-child {
  margin-bottom: 0;
}

.advanced-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

/* 覆盖 Element Plus 样式 */
:deep(.el-slider) {
  margin: 8px 0;
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

:deep(.el-select) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
}
</style>