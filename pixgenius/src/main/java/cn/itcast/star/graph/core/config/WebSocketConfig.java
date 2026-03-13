package cn.hx.pix.genius.core.config;

import cn.hx.pix.genius.core.wserver.WebscoketUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 设置允许访问的地址和域名
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*");
    }

    /**
     * 配置服务器哪些地址是可以定义或发送的地址
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/user");
        // 显式设置用户目的地前缀，配合 convertAndSendToUser
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 让websocket生效解析用户的数据信息
     * @param registry
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registry) {
        registry.interceptors(new WebscoketUserInterceptor());
    }

}
