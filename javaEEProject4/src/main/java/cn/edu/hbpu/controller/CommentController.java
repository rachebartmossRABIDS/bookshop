package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Comment;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.CommentService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @GetMapping("/listByBid")
    public Result listByBid(Integer bid){
        List<Comment> comments=commentService.selectByBid(bid);
        return Result.success(comments);
    }
    @PostMapping("/save")
    public Result save(Comment comment){
        Boolean result=commentService.save(comment);
        return result ? Result.success() : Result.fail("发表评论失败");
    }
    @GetMapping("/listByUid")
    public Result selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        PageInfo<Comment> pageInfo = commentService.selectByUid(uid, pageNo, pageSize);
        return Result.success(pageInfo);
    }
    @PostMapping("/remove")
    public Result remove(Integer cid){
        int result=commentService.remove(cid);
        return result>0?Result.success():Result.fail("删除评论失败");
    }
}
