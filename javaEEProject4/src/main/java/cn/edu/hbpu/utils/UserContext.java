package cn.edu.hbpu.utils;

/**
 * 当前登录用户上下文，由 AuthInterceptor 在请求开始时写入、结束时清除。
 */
public class UserContext {

    public static class LoginUser {
        private final Integer uid;
        private final String username;
        private final Integer type;

        public LoginUser(Integer uid, String username, Integer type) {
            this.uid = uid;
            this.username = username;
            this.type = type;
        }

        public Integer getUid() {
            return uid;
        }

        public String getUsername() {
            return username;
        }

        public Integer getType() {
            return type;
        }
    }

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Integer getUid() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getUid();
    }

    public static boolean isAdmin() {
        LoginUser user = HOLDER.get();
        return user != null && Integer.valueOf(2).equals(user.getType());
    }

    public static void clear() {
        HOLDER.remove();
    }
}
