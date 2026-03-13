package cn.hx.pix.genius.core.service;

import cn.hx.pix.genius.core.dto.Text2ImageReqDto;
import cn.hx.pix.genius.core.dto.Text2ImageResponeDto;

public interface Text2ImageService {
    Text2ImageResponeDto textToImage(Text2ImageReqDto text2ImageReqDto) throws Exception;

    boolean cancelTask(String taskId);

    long priorityTask(String taskId, long step);
}
