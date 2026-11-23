package cn.edu.hbpu.configuration;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录/注册限流：按客户端 IP 的滑动窗口（默认 60 秒内最多 20 次），
 * 防止暴力破解与批量注册。单机内存实现，适用于教学演示；
 * 集群部署应改用 Redis 等集中式计数。
 */
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final long WINDOWMillis = 60_000L;
    private static final int MAX_REQUESTS = 20;

    private final RateLimitStore store;

    public RateLimitInterceptor(RateLimitStore store) {
        this.store = store;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String ip = clientIp(request);
        if (!store.allow(ip, WINDOWMillis, MAX_REQUESTS)) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"请求过于频繁，请稍后再试\",\"data\":null}");
            return false;
        }
        return true;
    }

    private String clientIp(HttpServletRequest request) {
        // 开发环境经 Vite 代理转发，优先取代理附加的原始地址
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
