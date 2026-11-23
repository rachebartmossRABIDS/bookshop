package cn.edu.hbpu.configuration;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.utils.JwtUtil;
import cn.edu.hbpu.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录鉴权拦截器：校验 Authorization: Bearer <token>，
 * 通过后将当前用户写入 UserContext，失败返回 401。
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<String> optionalPaths = new HashSet<>(Arrays.asList(
            "/comment/listByBid"
    ));

    public AuthInterceptor(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            Claims claims = jwtUtil.parseToken(header.substring(7));
            if (claims != null) {
                try {
                    Integer uid = Integer.valueOf(claims.getSubject());
                    Integer currentVersion = userMapper.selectTokenVersion(uid);
                    Number tokenVersion = claims.get("ver", Number.class);
                    Integer state = userMapper.selectState(uid);
                    if (currentVersion != null
                            && (tokenVersion == null || tokenVersion.intValue() == currentVersion)
                            && !Integer.valueOf(1).equals(state)) {
                        UserContext.set(new UserContext.LoginUser(
                                uid,
                                claims.get("username", String.class),
                                claims.get("type", Integer.class)));
                        return true;
                    }
                } catch (Exception ignored) {
                    // 无效用户或 token 版本不一致，按未登录处理
                }
            }
        }
        if (optionalPaths.contains(request.getRequestURI().substring(request.getContextPath().length()))) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail("未登录或登录已过期，请先登录")));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
