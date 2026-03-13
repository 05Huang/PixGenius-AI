package cn.hx.pix.genius.comfyui.client.handler;

import cn.hx.pix.genius.comfyui.client.pojo.MessageBase;
import cn.hx.pix.genius.core.service.ComfyuiMessageService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class ComfyuiMessageHandler extends TextWebSocketHandler {

    @Autowired
    ComfyuiMessageService comfyuiMessageService;

    /**
     * 一个新的链接连接成功之后处理的逻辑
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("ComfyUI WebSocket连接成功, sessionId={}", session.getId());
    }

    /**
     * 收到消息之后如何处理
     * @param session
     * @param message
     * @throws Exception
     */
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到ComfyUI原始消息, sessionId={}, payload={}", session.getId(), payload);
        MessageBase messageBase = JSON.parseObject(payload, MessageBase.class);
        comfyuiMessageService.handleMessage(messageBase);
        log.info("ComfyUI消息已分发, type={}, sessionId={}", messageBase.getType(), session.getId());
    }

}
