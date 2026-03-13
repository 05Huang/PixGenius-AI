package cn.hx.pix.genius.core.service;

import java.util.List;

public interface AliImageService {
    /**
     * 调用通义千问 Image-Plus 生成图片，返回图片URL列表
     */
    List<String> generateImage(String prompt,
                               String negativePrompt,
                               int width,
                               int height,
                               int size,
                               int steps,
                               int cfg,
                               int seed,
                               int sampler,
                               int model) throws Exception;
}

