package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.mapper.CommentMapper;
import cn.edu.hbpu.pojo.Comment;
import cn.edu.hbpu.service.CommentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    @Override
    public List<Comment> selectByBid(Integer bid) {
        return commentMapper.selectByBid(bid);
    }

    @Override
    public Boolean save(Comment comment) {
        comment.setTime(new Date());
        return commentMapper.insertSelective(comment)==1?true:false;
    }

    @Override
    public PageInfo<Comment> selectByUid(Integer uid,Integer pageNo, Integer pageSize) {
        //调用PageHelper执行分页
        PageHelper.startPage(pageNo,pageSize);//pageNo页码 pageSize每页显示的记录数
        List<Comment> comments = commentMapper.selectByUid(uid);
        //查询结果封装成PageInfo对象
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        return pageInfo;
    }

    @Override
    public int remove(Integer cid) {
        return commentMapper.deleteByPrimaryKey(Long.parseLong(cid+""));
    }
}
