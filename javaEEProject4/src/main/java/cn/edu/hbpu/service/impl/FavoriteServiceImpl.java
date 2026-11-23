package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.mapper.FavoriteMapper;
import cn.edu.hbpu.pojo.Favorite;
import cn.edu.hbpu.service.FavoriteService;
import cn.edu.hbpu.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteMapper favoriteMapper;
    private final BookMapper bookMapper;

    @Override
    public Boolean save(Integer bid) {
        Integer uid = requireLogin();
        if (bid == null) {
            throw new BusinessException("请选择图书");
        }
        if (bookMapper.getByBidPrimary(bid) == null) {
            throw new BusinessException("图书不存在");
        }
        // 已收藏则幂等成功
        if (favoriteMapper.selectByUidBid(uid, bid) != null) {
            return true;
        }
        Favorite favorite = new Favorite();
        favorite.setUid(uid);
        favorite.setBid(bid);
        favorite.setTime(new Date());
        // insert ignore 兜底并发重复收藏
        favoriteMapper.insertIgnore(favorite);
        return true;
    }

    @Override
    public int remove(Integer bid) {
        Integer uid = requireLogin();
        if (bid == null) {
            throw new BusinessException("请选择图书");
        }
        return favoriteMapper.deleteByUidBid(uid, bid);
    }

    @Override
    public PageInfo<Favorite> selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        Integer currentUid = requireLogin();
        if (!UserContext.isAdmin() && !currentUid.equals(uid)) {
            throw new BusinessException("无权查看他人的收藏");
        }
        PageHelper.startPage(pageNo, pageSize);
        List<Favorite> favorites = favoriteMapper.selectByUid(uid);
        return new PageInfo<>(favorites);
    }

    @Override
    public Boolean check(Integer bid) {
        Integer uid = requireLogin();
        if (bid == null) {
            throw new BusinessException("请选择图书");
        }
        return favoriteMapper.selectByUidBid(uid, bid) != null;
    }

    private Integer requireLogin() {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        return uid;
    }
}