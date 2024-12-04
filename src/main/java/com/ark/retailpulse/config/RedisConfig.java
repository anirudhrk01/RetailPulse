package com.ark.retailpulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisConfig {

    /**
     * Configures a RedisTemplate to interact with Redis for session management.
     * This template is used by Spring Session to store session data in Redis.
     *
     * @param redisConnectionFactory the factory to establish Redis connections.
     * @return a configured RedisTemplate instance.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        try {
            // Set the Redis connection factory
            template.setConnectionFactory(redisConnectionFactory);

            // Use String serialization for Redis keys
            template.setKeySerializer(new StringRedisSerializer());

            // Use JSON serialization for Redis values
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        } catch (Exception e) {
            // Log any configuration issues (use a proper logger in real apps)
            System.err.println("Error configuring RedisTemplate: " + e.getMessage());
            throw new RuntimeException("Failed to configure RedisTemplate", e);
        }

        return template;
    }
}
