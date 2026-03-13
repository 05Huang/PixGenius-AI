package cn.hx.pix.genius.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dashscope")
public class DashscopeProperties {
    /**
     * 阿里云百炼平台 API Key
     */
    private String apiKey;
}

