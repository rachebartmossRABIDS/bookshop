package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Comment;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CommentService {
    List<Comment> selectByBid(Integer bid);
    Boolean save(Comment comment);
    PageInfo<Comment> selectByUid(Integer uid,Integer pageNo, Integer pageSize);
    int remove (Integer cid);
    /** 管理员分页查询全部评论（嵌套图书与评论人公开信息） */
    PageInfo<Comment> listAll(Integer pageNo, Integer pageSize);
    void like(Integer cid);
    void unlike(Integer cid);
    Comment reply(Integer cid, String reply);
}
