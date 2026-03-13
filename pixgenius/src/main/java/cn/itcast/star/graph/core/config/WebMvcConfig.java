package cn.hx.pix.genius.core.config;

import cn.hx.pix.genius.core.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .excludePathPatterns(
                        "/ws/**",          // 放行 WebSocket 握手，避免 401
                        "/topic/**",       // 放行 STOMP topic
                        "/user/**"         // 放行 STOMP user 通道
                );
    }
}
