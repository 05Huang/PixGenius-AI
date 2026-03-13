<template>
  <div class="io ali-workspace">
    <el-tabs v-model="activeName" class="demo-tabs">
      <el-tab-pane label="阿里百炼工作区" name="ali-config">
        <!-- 基本设置 -->
        <div class="scbox">
          <div class="scbox-title">基本设置（Image-Plus）</div>
          <div class="scbox-form">
            <el-form :model="form" label-width="auto" size="small">
              <el-form-item label="画质">
                <el-select v-model="form.quality" placeholder="默认">
                  <el-option label="标准（standard）" value="standard" />
                  <el-option label="高清（hd）" value="hd" />
                  <el-option label="极速（fast）" value="fast" />
                </el-select>
              </el-form-item>
              <el-form-item label="输出张数">
                <el-input-number v-model="form.n" :min="1" :max="4" />
              </el-form-item>
              <el-form-item label="尺寸">
                <el-select v-model="form.size" placeholder="自动">
                  <el-option label="方图 1024x1024" value="1024x1024" />
                  <el-option label="竖图 720x1280" value="720x1280" />
                  <el-option label="横图 1280x720" value="1280x720" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 风格与控制 -->
        <div class="scbox">
          <div class="scbox-title">风格与控制</div>
          <div class="scbox-form">
            <el-form :model="form" label-width="auto" size="small">
              <el-form-item label="风格预设">
                <el-select v-model="form.style" placeholder="默认">
                  <el-option label="写实摄影" value="photographic" />
                  <el-option label="动漫二次元" value="anime" />
                  <el-option label="水彩插画" value="watercolor" />
                  <el-option label="油画艺术" value="oil-painting" />
                </el-select>
              </el-form-item>
              <el-form-item label="随机种子">
                <el-input v-model="form.seed" placeholder="留空为随机" />
              </el-form-item>
              <el-form-item label="采样步数">
                <el-slider v-model="form.num_inference_steps" :max="50" :min="10" :step="5" show-stops />
              </el-form-item>
              <el-form-item label="提示词权重">
                <el-slider v-model="form.guidance_scale" :max="20" :min="1" :step="1" show-stops />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 负向提示词 -->
        <div class="scbox">
          <div class="scbox-title">负向提示词（negative_prompt）</div>
          <div class="scbox-form">
            <el-input
              v-model="form.negative_prompt"
              type="textarea"
              :rows="3"
              placeholder="不希望在画面中出现的内容，例如：低质量、扭曲、畸形、模糊、噪点等"
            />
          </div>
        </div>

        <!-- 参考图生图（可选） -->
        <div class="scbox">
          <div class="scbox-title">参考图控制（可选）</div>
          <div class="scbox-form">
            <el-form :model="form" label-width="auto" size="small">
              <el-form-item label="参考图 URL">
                <el-input v-model="form.image_url" placeholder="填写可公网访问的图片地址" />
              </el-form-item>
              <el-form-item label="参考强度">
                <el-slider
                  v-model="form.image_guidance_scale"
                  :max="10"
                  :min="0"
                  :step="0.5"
                  show-stops
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const activeName = ref("ali-config");

// 这里的字段尽量贴合阿里百炼 Image-Plus 文生图常见参数命名
const form = ref({
  // 基本设置
  quality: "standard", // 画质：standard/hd/fast
  n: 1, // 张数
  size: "1024x1024", // 输出尺寸

  // 风格与采样
  style: "", // 风格预设
  seed: "", // 随机种子
  num_inference_steps: 30, // 采样步数
  guidance_scale: 7.5, // 提示词权重

  // 负向提示词
  negative_prompt: "",

  // 参考图控制
  image_url: "",
  image_guidance_scale: 0,
});

function getFormData() {
  return form.value;
}

defineExpose({ getFormData });
</script>

<style scoped>
.ali-workspace.io {
  width: 100%;
  max-width: 320px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 8px 15px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
}

.scbox {
  margin-top: 12px;
  width: 100%;
  padding-bottom: 10px;
  background-image: linear-gradient(270deg, #eeefff 9%, #eff5ff 93%);
  border-radius: 10px;
}

.scbox-form {
  padding: 0 15px 10px;
}

.scbox-title {
  font-family: PingFangSC-Medium;
  font-weight: 500;
  font-size: 14px;
  color: #5555ff;
  width: 100%;
  padding: 10px 15px;
}
</style>


