package cn.hx.pix.genius.core.service;

public interface AliyunCaptchaService {

    /**
     * 校验阿里云验证码2.0
     * @param captchaVerifyParam 前端传入的验证参数
     */
    void verify(String captchaVerifyParam);
}
