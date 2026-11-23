package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.pojo.User;
import cn.edu.hbpu.service.UploadService;
import cn.edu.hbpu.service.UserService;
import cn.edu.hbpu.service.PasswordResetService;
import cn.edu.hbpu.utils.JwtUtil;
import cn.edu.hbpu.utils.PasswordUtil;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UploadService uploadService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public Result login(User user) {
        User loginUser = userService.selectUser(user);
        if (loginUser == null) {
            return Result.fail("用户名不存在");
        }
        if (loginUser.getPassword() == null || !PasswordUtil.matches(user.getPassword(), loginUser.getPassword())) {
            return Result.fail("密码错误");
        }
        if (Integer.valueOf(1).equals(loginUser.getState())) {
            return Result.fail("账号已被封禁，请联系管理员");
        }
        // 历史 MD5 哈希登录成功后平滑升级为 BCrypt
        if (!PasswordUtil.isBcrypt(loginUser.getPassword())) {
            userService.rehashPassword(loginUser.getUid(), user.getPassword());
        }
        User fullUser = userService.getInfo(loginUser.getUid());
        String token = jwtUtil.generateToken(fullUser.getUid(), fullUser.getUsername(),
                fullUser.getType(), fullUser.getTokenVersion());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", fullUser);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result register(String username, String password, String tel, String email) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        username = username.trim();
        if (username.length() < 3 || username.length() > 20) {
            return Result.fail("用户名长度需在 3-20 个字符之间");
        }
        if (password.length() < 6) {
            return Result.fail("密码长度不能少于 6 位");
        }
        if (password.length() > 64) {
            return Result.fail("密码长度不能超过 64 位");
        }
        // 选填字段：提供时校验格式
        tel = tel == null ? null : tel.trim();
        email = email == null ? null : email.trim();
        if (tel != null && !tel.isEmpty() && !ValidateUtil.isTel(tel)) {
            return Result.fail("手机号格式不正确");
        }
        if (email != null && !email.isEmpty() && !ValidateUtil.isEmail(email)) {
            return Result.fail("邮箱格式不正确");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setTel(tel);
        user.setEmail(email);
        int result = userService.register(user);
        if (result == -1) {
            return Result.fail("用户名已存在");
        }
        return result > 0 ? Result.success("注册成功", null) : Result.fail("注册失败");
    }

    @PostMapping("/sendResetCode")
    public Result sendResetCode(String email) {
        passwordResetService.sendCode(email);
        return Result.success("验证码已发送", null);
    }

    @PostMapping("/resetPassword")
    public Result resetPassword(String email, String code, String newPassword) {
        passwordResetService.reset(email, code, newPassword);
        return Result.success("密码已重置，请使用新密码登录", null);
    }

    @GetMapping("/{uid}")
    public Result getInfo(@PathVariable Integer uid) {
        // 只允许查询本人信息，管理员可查询任意用户
        Integer currentUid = UserContext.getUid();
        if (!UserContext.isAdmin() && (currentUid == null || !currentUid.equals(uid))) {
            return Result.fail("无权查看他人的个人信息");
        }
        return Result.success(userService.getInfo(uid));
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(String OldPassword, String NewPassword) {
        // 只允许修改当前登录用户自己的密码
        Integer uid = UserContext.getUid();
        if (OldPassword == null || OldPassword.isEmpty()
                || NewPassword == null || NewPassword.isEmpty()) {
            return Result.fail("请完整填写原密码和新密码");
        }
        if (NewPassword.length() < 6) {
            return Result.fail("新密码长度不能少于 6 位");
        }
        if (NewPassword.length() > 64) {
            return Result.fail("新密码长度不能超过 64 位");
        }
        int result = userService.updatePassword(uid, OldPassword, NewPassword);
        if (result == -1) {
            return Result.fail("用户不存在");
        }
        if (result == 0) {
            return Result.fail("原密码错误");
        }
        return result > 0 ? Result.success() : Result.fail("修改失败");
    }

    @PostMapping("/updateInfo")
    public Result updateInfo(String tel, String email) {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            return Result.fail("请先登录");
        }
        // 选填字段：提供时校验格式（空字符串表示清空）
        tel = tel == null ? null : tel.trim();
        email = email == null ? null : email.trim();
        if (tel != null && !tel.isEmpty() && !ValidateUtil.isTel(tel)) {
            return Result.fail("手机号格式不正确");
        }
        if (email != null && !email.isEmpty() && !ValidateUtil.isEmail(email)) {
            return Result.fail("邮箱格式不正确");
        }
        return Result.success(userService.updateInfo(uid, tel, email));
    }

    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            return Result.fail("请先登录");
        }
        String filename = uploadService.saveImage(file, "avatar_" + uid);
        return Result.success(userService.updateAvatar(uid, filename));
    }

    @GetMapping("/list")
    public Result list(Integer pageNo, Integer pageSize) {
        PageInfo<User> pageInfo = userService.listAll(pageNo, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/setState")
    public Result setState(Integer uid, Integer state) {
        int result = userService.setState(uid, state);
        return result > 0 ? Result.success("操作成功", null) : Result.fail("操作失败");
    }

    @PostMapping("/refresh")
    public Result refresh() {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            return Result.fail("请先登录");
        }
        User current = userService.getInfo(uid);
        if (current == null) {
            return Result.fail("用户不存在");
        }
        String token = jwtUtil.generateToken(current.getUid(), current.getUsername(),
                current.getType(), current.getTokenVersion());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }
}
