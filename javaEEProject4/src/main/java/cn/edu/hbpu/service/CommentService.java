package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Comment;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CommentService {
    List<Comment> selectByBid(Integer bid);
    Boolean save(Comment comment);
    PageInfo<Comment> selectByUid(Integer uid,Integer pageNo, Integer pageSize);
    int remove (Integer cid);
}