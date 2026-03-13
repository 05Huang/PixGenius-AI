package cn.hx.pix.genius.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云验证码2.0 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.captcha")
public class AliyunCaptchaProperties {

    private String accessKeyId;
    private String accessKeySecret;
    private String sceneId;
}
