package cn.edu.hbpu.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    @Value("${redis.host:localhost}")
    private String host;
    @Value("${redis.port:6379}")
    private int port;
    @Value("${redis.password:}")
    private String password;
    @Value("${redis.database:0}")
    private int database;
    @Value("${redis.timeout:500}")
    private int timeout;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(database);
        if (password != null && !password.trim().isEmpty()) {
            config.setPassword(password);
        }
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.setTimeout(timeout);
        return factory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
