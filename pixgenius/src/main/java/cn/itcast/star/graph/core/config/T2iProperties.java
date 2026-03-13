package cn.hx.pix.genius.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文生图能力开关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "t2i")
public class T2iProperties {

    /**
     * 是否启用本地 Stable Diffusion/ComfyUI 能力。
     * 云环境无 SD/GPU 时可关闭。
     */
    private boolean sdEnabled = true;
}

