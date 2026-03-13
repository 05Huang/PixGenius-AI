package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.core.service.WsNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WsNoticeServiceImpl implements WsNoticeService {

    public final static String COMFYUI_QUEUE_TOPIC = "/topic/messages";

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendToUser(String clientId, String message) {
        // 便于排查推送是否到达
        System.out.println("WsNotice sendToUser clientId=" + clientId + " payload=" + message);
        simpMessagingTemplate.convertAndSendToUser(clientId,COMFYUI_QUEUE_TOPIC,message);
    }

    @Override
    public void sendToAll(String message) {
        simpMessagingTemplate.convertAndSend(COMFYUI_QUEUE_TOPIC,message);
    }

}
