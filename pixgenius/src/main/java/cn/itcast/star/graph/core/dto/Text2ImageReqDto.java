package cn.hx.pix.genius.core.dto;

import lombok.Data;

@Data
public class Text2ImageReqDto {
    int size;
    int model;
    int scale;
    int step;
    int cfg;
    int sampler;
    int seed;
    String reverse;
    String propmt;
    String clientId;
    /**
     * 前端 Cloudflare Turnstile 人机验证 token
     */
    String captchaToken;
    /**
     * 生成器类型：sd 表示本地 Stable Diffusion/ComfyUI，ali 表示通义千问-Image-Plus
     */
    String provider;

    public String modelName(){
        switch (model){
            case 2:
                return "anythingelseV4_v45.safetensors";
            default:
                return "majicmixRealistic_v7.safetensors";
        }
    }

    public String samplerName(){
        switch (sampler){
            case 1:
                return "dpmpp_sde";
            case 2:
                return "dpmpp_2m";
            case 3:
                return "euler";
            case 4:
                return "dpmpp_3m_sde";
            default:
                return "euler";
        }
    }

    public String scheduler(){
        return "karras";
    }


    public int width() {
        if(scale==3){
            return 768;
        } else {
            return 512;
        }
    }

    public int height() {
        if(scale==2){
            return 768;
        } else {
            return 512;
        }
    }


}
