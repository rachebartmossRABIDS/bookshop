package cn.edu.hbpu.configuration;

import cn.edu.hbpu.utils.JwtUtil;
import cn.edu.hbpu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.TimeZone;
import java.util.Arrays;

@Configuration
@ComponentScan({"cn.edu.hbpu.controller", "cn.edu.hbpu.advice"})
@PropertySource({
        "classpath:jwt.properties",
        "classpath:pic.properties",
        "classpath:redis.properties",
        "classpath:cors.properties"
})
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserMapper userMapper;
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Value("${redis.enabled:true}")
    private boolean redisEnabled;

    @Value("${cors.allowed-origins:}")
    private String corsAllowedOrigins;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire-minutes}")
    private long jwtExpireMinutes;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret, jwtExpireMinutes);
    }

    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor(jwtUtil(), userMapper);
    }

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor(rateLimitStore());
    }

    @Bean
    public InMemoryRateLimitStore inMemoryRateLimitStore() {
        return new InMemoryRateLimitStore();
    }

    @Bean
    public RateLimitStore rateLimitStore() {
        if (redisEnabled && redisTemplate != null) {
            return new RedisRateLimitStore(redisTemplate, inMemoryRateLimitStore());
        }
        return inMemoryRateLimitStore();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录/注册限流（防暴力破解/批量注册）
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/user/login", "/user/register", "/user/sendResetCode");
        registry.addInterceptor(authInterceptor())
                .addPathPatterns("/**")
                // 无需登录即可访问的接口（浏览类公开；管理写操作需登录+管理员）
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/sendResetCode",
                        "/user/resetPassword",
                        "/kind/list",
                        "/book/list/**",
                        "/book/listBySid",
                        "/book/detail",
                        "/book/search",
                        "/shop/list/**",
                        "/shop/listAll",
                        "/shop/sdetail",
                        "/pic/**",
                        "/book/hotWords",
                        "/index.jsp",
                        "/favicon.ico",
                        "/error"
                );
    }

    /**
     * Jackson 默认按 UTC 序列化日期，导致时间显示比库中早 8 小时；
     * 统一为 GMT+8，与 JDBC serverTimezone=Asia/Shanghai 保持一致。
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).getObjectMapper()
                        .setTimeZone(TimeZone.getTimeZone("GMT+8"));
            }
        }
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        if (corsAllowedOrigins == null || corsAllowedOrigins.trim().isEmpty()) {
            return;
        }
        String[] origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
