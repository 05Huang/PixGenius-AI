package cn.hx.pix.genius.core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedissionConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        String address = String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort());
        config.useSingleServer()
                .setAddress(address);
        
        // 如果Redis设置了密码，添加密码配置
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        
        return Redisson.create(config);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisProperties redisProperties) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
        
        // 如果Redis设置了密码，添加密码配置
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            lettuceConnectionFactory.setPassword(redisProperties.getPassword());
        }
        
        lettuceConnectionFactory.start();
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

}
