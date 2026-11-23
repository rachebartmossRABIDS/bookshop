package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Favorite;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.FavoriteService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/save")
    public Result save(Integer bid) {
        favoriteService.save(bid);
        return Result.success("收藏成功", null);
    }

    @PostMapping("/remove")
    public Result remove(Integer bid) {
        int result = favoriteService.remove(bid);
        return result > 0 ? Result.success("已取消收藏", null) : Result.fail("尚未收藏该图书");
    }

    @GetMapping("/listByUid")
    public Result listByUid(Integer uid, Integer pageNo, Integer pageSize) {
        PageInfo<Favorite> pageInfo = favoriteService.selectByUid(uid, pageNo, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/check")
    public Result check(Integer bid) {
        return Result.success(favoriteService.check(bid));
    }
}