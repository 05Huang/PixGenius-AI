package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.core.config.DashscopeProperties;
import cn.hx.pix.genius.core.service.AliImageService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AliImageServiceImpl implements AliImageService {

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";

    @Autowired
    DashscopeProperties dashscopeProperties;

    @Override
    public List<String> generateImage(String prompt,
                                      String negativePrompt,
                                      int width,
                                      int height,
                                      int size,
                                      int steps,
                                      int cfg,
                                      int seed,
                                      int sampler,
                                      int model) throws Exception {
        if (dashscopeProperties.getApiKey() == null || dashscopeProperties.getApiKey().isEmpty()) {
            throw new IllegalStateException("未配置 dashscope.api-key");
        }
        // 通义千问 qwen-image-plus 当前只支持固定的五种尺寸：
        // 1664*928,1472*1140,1328*1328,1140*1472,928*1664
        // 这里根据传入的宽高比例做一个简单映射：
        // - 接近方形：1328*1328
        // - 竖图：928*1664
        // - 横图：1664*928
        String sizeStr;
        if (width == height) {
            sizeStr = "1328*1328";
        } else if (height > width) {
            // 竖图
            sizeStr = "928*1664";
        } else {
            // 横图
            sizeStr = "1664*928";
        }

        // 生图风格提示：沿用前端“生图风格”(原“生图模型”)的选择进行轻量提示
        String styleHint = "";
        // model: 1=真实/写真，2=二次元
        if (model == 2) {
            styleHint = "二次元动漫风格，细腻、干净、线条分明；";
        } else {
            styleHint = "真实写真风格，光影自然、质感丰富；";
        }
        String finalPrompt = styleHint + (prompt == null ? "" : prompt);

        // ===== 构造 MultiModalConversation 请求体（与官方 SDK 对齐） =====
        // messages: 与 Python 示例保持一致 [{ "role":"user", "content":[{"text": "..."}]}]
        JSONArray contentArr = new JSONArray();
        JSONObject textObj = new JSONObject();
        textObj.put("text", finalPrompt);
        contentArr.add(textObj);

        JSONArray messages = new JSONArray();
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", contentArr);
        messages.add(userMsg);

        // input 部分
        JSONObject input = new JSONObject();
        input.put("messages", messages);

        // parameters 部分：对应 SDK 中的 result_format / size / negative_prompt / 步数 / 种子 / 强度 等参数
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        parameters.put("stream", false);
        parameters.put("watermark", false);
        parameters.put("prompt_extend", true);
        parameters.put("negative_prompt", negativePrompt == null ? "" : negativePrompt);
        parameters.put("size", sizeStr);
        // 张数：与前端“张数(size)”联动，限制 1~4，仅用于控制循环次数；
        // 由于百炼当前接口限制 num_images_per_prompt 必须为 1，这里不再下发 n 参数，而是循环调用多次。
        int count = Math.max(1, Math.min(size, 4));
        // 步数：与前端“步数(step)”联动，限制在 1~50 之间
        int safeSteps = Math.max(1, Math.min(steps, 50));
        parameters.put("steps", safeSteps);
        // 强度：与前端“强度(cfg)”联动，映射为 guidance_scale，常见区间 1~20
        int safeCfg = Math.max(1, Math.min(cfg, 20));
        parameters.put("guidance_scale", safeCfg);
        // 种子：与前端“种子(seed)”联动，大于 0 时才传入，否则由服务端随机
        if (seed > 0) {
            parameters.put("seed", seed);
        }
        // 采样器 sampler：通义千问文档中未强制要求，且不同版本支持不一致，这里暂不下发，避免因不兼容导致报错

        // 允许按次调试详细日志：在运行环境加环境变量/配置
        // LOGGING_LEVEL_cn.hx.pix.genius.core.service.impl.AliImageServiceImpl=DEBUG
        OkHttpClient client = new OkHttpClient();

        List<String> allUrls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            JSONObject body = new JSONObject();
            body.put("model", "qwen-image-plus");
            body.put("input", input);
            body.put("parameters", parameters);

            // ===== 打印完整请求参数日志（不含密钥） =====
            log.info("调用通义千问 Image-Plus，第 {} 次，最终提示词: {}, 负向词: {}, size: {}, steps: {}, guidance_scale: {}, seed: {}, model(风格): {}",
                    (i + 1),
                    finalPrompt,
                    negativePrompt,
                    sizeStr,
                    safeSteps,
                    safeCfg,
                    (seed > 0 ? seed : null),
                    model);
            log.debug("通义千问 Image-Plus 第 {} 次请求体: {}", (i + 1), body.toJSONString());

            RequestBody requestBody = RequestBody.create(body.toJSONString(),
                    MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + dashscopeProperties.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();

            long start = System.currentTimeMillis();
            log.info("调用通义千问 Image-Plus，第 {} 次，size={}x{}, mappedSize={}, seed={}, steps={}, cfg={}, model={}, requestIdHint={}",
                    (i + 1), width, height, sizeStr, (seed > 0 ? seed : null), safeSteps, safeCfg, model, wsRequestIdHint(prompt));
            log.debug("[BAILIAN_REQ] 第 {} 次 headers={}, body={}", (i + 1), request.headers(), body.toJSONString());
            try (Response response = client.newCall(request).execute()) {
                long cost = System.currentTimeMillis() - start;
                if (!response.isSuccessful()) {
                    String err = response.body() != null ? response.body().string() : "empty";
                    log.error("[BAILIAN_ERR] httpCode={}, cost={}ms, body={}", response.code(), cost, err);
                    throw new RuntimeException("调用通义千问失败: " + response.code());
                }
                String respStr = response.body() != null ? response.body().string() : "";
                log.debug("[BAILIAN_RESP] cost={}ms, body={}", cost, respStr);
                JSONObject respJson = JSON.parseObject(respStr);
                log.debug("[BAILIAN_RESP_PARSED] keys={}, outputKeys={}", respJson.keySet(), respJson.getJSONObject("output") != null ? respJson.getJSONObject("output").keySet() : null);
                // 解析图片URL：优先尝试 MultiModalConversation 格式：output.choices[0].message.content[*].image_url
                List<String> urls = new ArrayList<>();
                JSONObject output = respJson.getJSONObject("output");
                if (output != null) {
                    JSONArray choices = output.getJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JSONObject first = choices.getJSONObject(0);
                        JSONObject msg = first.getJSONObject("message");
                        if (msg != null) {
                            JSONArray contents = msg.getJSONArray("content");
                            if (contents != null) {
                                contents.forEach(item -> {
                                    if (item instanceof JSONObject) {
                                        JSONObject obj = (JSONObject) item;
                                        // 优先尝试 image_url，其次 url，最后 image（当前实际返回使用 image 字段）
                                        String url = obj.getString("image_url");
                                        if (url == null || url.isEmpty()) {
                                            url = obj.getString("url");
                                        }
                                        if (url == null || url.isEmpty()) {
                                            url = obj.getString("image");
                                        }
                                        if (url != null && !url.isEmpty()) {
                                            urls.add(url);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    // 如果上面的 choices 结构没有解析出图片，再尝试 image-generation 的 results 结构：output.results[*].url
                    if (urls.isEmpty()) {
                        JSONArray results = output.getJSONArray("results");
                        if (results != null) {
                            results.forEach(item -> {
                                if (item instanceof JSONObject) {
                                    String url = ((JSONObject) item).getString("url");
                                    if (url != null && !url.isEmpty()) {
                                        urls.add(url);
                                    }
                                }
                            });
                        }
                    }
                }
                if (urls.isEmpty()) {
                    log.error("[BAILIAN_PARSE_ERR] 未找到图片URL，原始响应: {}", respStr);
                    throw new RuntimeException("通义千问未返回图片URL");
                }
                // 逐条打印 URL 便于人工验证签名是否被截断/解码
                for (int idx = 0; idx < urls.size(); idx++) {
                    String u = urls.get(idx);
                    log.info("[BAILIAN_URL] 第 {} 次，第 {} 张，长度={}，raw={}", (i + 1), (idx + 1), u.length(), u);
                }
                allUrls.addAll(urls);
            }
        }
        return allUrls;
    }

    /**
     * 简单提取一个请求ID提示，便于日志串联（只截取提示词hash前8位）
     */
    private String wsRequestIdHint(String prompt) {
        if (prompt == null || prompt.isEmpty()) {
            return "no-prompt";
        }
        int h = prompt.hashCode();
        return Integer.toHexString(h);
    }
}

