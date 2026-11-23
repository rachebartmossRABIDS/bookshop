package cn.edu.hbpu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌工具：登录成功后颁发 token，拦截器解析校验。
 */
public class JwtUtil {

    private final SecretKey key;
    private final long expireMillis;

    public JwtUtil(String secret, long expireMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60 * 1000;
    }

    public String generateToken(Integer uid, String username, Integer type, Integer tokenVersion) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(uid))
                .claim("username", username)
                .claim("type", type == null ? 1 : type)
                .claim("ver", tokenVersion == null ? 0 : tokenVersion)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Integer uid, String username, Integer type) {
        return generateToken(uid, username, type, 0);
    }

    /**
     * 解析 token，无效或过期返回 null。
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
