package cn.hx.pix.genius.core.wserver;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.List;

/**
 * 一对一消息发送时的用户信息解析类
 */
public class WebscoketUserInterceptor implements ChannelInterceptor {
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // 如果是连接指令（CONNECT），从 header 中解析 clientId 并设置为 Principal
        if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
            List<String> clientIds = accessor.getNativeHeader("clientId");
            if (clientIds != null && !clientIds.isEmpty()) {
                String clientId = clientIds.get(0);
                accessor.setUser(new Principal() {
                    @Override
                    public String getName() {
                        return clientId;
                    }
                });
            }
        }
        return message;
    }

}
