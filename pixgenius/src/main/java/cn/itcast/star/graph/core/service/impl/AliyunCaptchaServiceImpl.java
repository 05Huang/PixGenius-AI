package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.core.config.AliyunCaptchaProperties;
import cn.hx.pix.genius.core.exception.CustomException;
import cn.hx.pix.genius.core.service.AliyunCaptchaService;
import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AliyunCaptchaServiceImpl implements AliyunCaptchaService {

    @Autowired
    private AliyunCaptchaProperties properties;

    @Override
    public void verify(String captchaVerifyParam) {
        if (captchaVerifyParam == null || captchaVerifyParam.isEmpty()) {
            throw new CustomException("请先完成人机验证");
        }
        
        try {
            Config config = new Config()
                    .setAccessKeyId(properties.getAccessKeyId())
                    .setAccessKeySecret(properties.getAccessKeySecret());
            // 默认 Endpoint，如有需要可配置
            config.endpoint = "captcha.cn-shanghai.aliyuncs.com";

            Client client = new Client(config);

            VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest();
            request.setCaptchaVerifyParam(captchaVerifyParam);
            request.setSceneId(properties.getSceneId());

            VerifyIntelligentCaptchaResponse response = client.verifyIntelligentCaptcha(request);

            if (response.getBody().getResult() != null && response.getBody().getResult().verifyResult) {
                // 验证通过
                return;
            } else {
                log.warn("阿里云验证码校验未通过: code={}, msg={}", response.getBody().getCode(), response.getBody().getMessage());
                throw new CustomException("人机验证未通过，请重试");
            }

        } catch (Exception e) {
            log.error("调用阿里云验证码服务异常", e);
            throw new CustomException("人机验证服务异常，请稍后重试");
        }
    }
}
