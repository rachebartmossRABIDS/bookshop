package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.CommentMapper;
import cn.edu.hbpu.mapper.CommentLikeMapper;
import cn.edu.hbpu.mapper.OrderMapper;
import cn.edu.hbpu.pojo.Comment;
import cn.edu.hbpu.service.CommentService;
import cn.edu.hbpu.service.NotificationService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.utils.UserContext;
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
    private final CommentLikeMapper commentLikeMapper;
    private final OrderMapper orderMapper;
    private final NotificationService notificationService;
    private final OperationLogService operationLogService;

    @Override
    public List<Comment> selectByBid(Integer bid) {
        List<Comment> list = commentMapper.selectByBid(bid);
        enrich(list);
        return list;
    }

    @Override
    public Boolean save(Comment comment) {
        // 评论人取当前登录用户，不信任客户端传入的 uid
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        if (comment.getBid() == null) {
            throw new BusinessException("请选择图书");
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new BusinessException("评论内容不能为空");
        }
        String content = comment.getContent().trim();
        if (content.length() > 500) {
            throw new BusinessException("评论内容不能超过 500 字");
        }
        comment.setContent(content);
        if (comment.getScore() == null) {
            comment.setScore(5);
        }
        if (comment.getScore() < 1 || comment.getScore() > 5) {
            throw new BusinessException("评分必须为 1-5 分");
        }
        if (comment.getOid() != null) {
            cn.edu.hbpu.pojo.Order order = orderMapper.selectByPrimaryKey(comment.getOid().longValue());
            if (order == null || !uid.equals(order.getUid())) {
                throw new BusinessException("已购标识对应的订单不属于当前用户");
            }
        }
        comment.setUid(uid);
        comment.setTime(new Date());
        return commentMapper.insertSelective(comment) == 1;
    }

    @Override
    public PageInfo<Comment> selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        checkSelfOrAdmin(uid, "无权查看他人的评论");
        //调用PageHelper执行分页
        PageHelper.startPage(pageNo, pageSize);//pageNo页码 pageSize每页显示的记录数
        List<Comment> comments = commentMapper.selectByUid(uid);
        //查询结果封装成PageInfo对象
        enrich(comments);
        return new PageInfo<>(comments);
    }

    @Override
    public int remove(Integer cid) {
        if (cid == null) {
            throw new BusinessException("评论编号不能为空");
        }
        Comment comment = commentMapper.selectByPrimaryKey(cid.longValue());
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        checkSelfOrAdmin(comment.getUid(), "无权删除他人的评论");
        commentLikeMapper.deleteByCid(cid);
        if (UserContext.isAdmin() && !UserContext.getUid().equals(comment.getUid())) {
            operationLogService.log("删除评论", "评论 #" + cid + "，内容：" + comment.getContent());
        }
        return commentMapper.deleteByPrimaryKey(cid.longValue());
    }

    @Override
    public PageInfo<Comment> listAll(Integer pageNo, Integer pageSize) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
        PageHelper.startPage(pageNo, pageSize);
        List<Comment> comments = commentMapper.selectAll();
        enrich(comments);
        return new PageInfo<>(comments);
    }

    @Override
    public void like(Integer cid) {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        requireComment(cid);
        commentLikeMapper.insertIgnore(uid, cid);
    }

    @Override
    public void unlike(Integer cid) {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        requireComment(cid);
        commentLikeMapper.delete(uid, cid);
    }

    @Override
    public Comment reply(Integer cid, String reply) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("仅管理员可回复评论");
        }
        Comment comment = requireComment(cid);
        if (reply == null || reply.trim().isEmpty()) {
            throw new BusinessException("回复内容不能为空");
        }
        String content = reply.trim();
        if (content.length() > 1000) {
            throw new BusinessException("回复内容不能超过 1000 字");
        }
        if (commentMapper.updateReply(cid, content) != 1) {
            throw new BusinessException("回复评论失败");
        }
        notificationService.send(comment.getUid(), "评论收到商家回复",
                "您对《" + (comment.getBook() == null ? "图书" : comment.getBook().getName())
                        + "》的评论已收到回复：" + content);
        operationLogService.log("回复评论", "评论 #" + cid + "：" + content);
        return commentMapper.selectByPrimaryKey(cid.longValue());
    }

    private Comment requireComment(Integer cid) {
        if (cid == null) {
            throw new BusinessException("评论编号不能为空");
        }
        Comment comment = commentMapper.selectByPrimaryKey(cid.longValue());
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        return comment;
    }

    private void enrich(List<Comment> comments) {
        Integer uid = UserContext.getUid();
        for (Comment comment : comments) {
            comment.setLikeCount(commentLikeMapper.countByCid(comment.getCid()));
            comment.setLiked(uid != null && commentLikeMapper.exists(uid, comment.getCid()) > 0);
        }
    }

    private void checkSelfOrAdmin(Integer targetUid, String message) {
        Integer currentUid = UserContext.getUid();
        if (UserContext.isAdmin()) {
            return;
        }
        if (currentUid == null || !currentUid.equals(targetUid)) {
            throw new BusinessException(message);
        }
    }
}
