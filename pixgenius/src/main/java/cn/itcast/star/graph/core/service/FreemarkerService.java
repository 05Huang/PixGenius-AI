package cn.hx.pix.genius.core.service;

import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiModel;

public interface FreemarkerService {

    /**
     * 返回替换好的comfyui字符串
     * @param model
     * @return
     * @throws Exception
     */
    public String renderText2Image(ComfyuiModel model) throws Exception ;

}
