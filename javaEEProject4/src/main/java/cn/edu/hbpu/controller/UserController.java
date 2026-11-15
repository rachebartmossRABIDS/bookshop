package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.pojo.User;
import cn.edu.hbpu.service.UserService;
import cn.edu.hbpu.utils.Md5Util;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/login")
    public Result login(User user){
        User loginUser=userService.selectUser(user);
        if(loginUser==null){
            return Result.fail("用户名不存在");
        }
        if(loginUser.getPassword()==null || !Md5Util.checkPassword(user.getPassword(),loginUser.getPassword())){
            return Result.fail("密码错误");
        }
        return Result.success(loginUser);
    }
    @GetMapping("/{uid}")
    public Result getInfo(@PathVariable Integer uid){
        return Result.success(userService.getInfo(uid));
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(Integer uid, String OldPassword, String NewPassword){
        if (uid == null || OldPassword == null || OldPassword.isEmpty()
                || NewPassword == null || NewPassword.isEmpty()) {
            return Result.fail("请完整填写原密码和新密码");
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
}