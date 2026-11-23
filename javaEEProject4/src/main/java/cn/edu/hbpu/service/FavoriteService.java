package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Favorite;
import com.github.pagehelper.PageInfo;

public interface FavoriteService {
    /** 收藏图书（当前登录用户），重复收藏幂等成功 */
    Boolean save(Integer bid);

    /** 取消收藏（当前登录用户） */
    int remove(Integer bid);

    /** 分页查询某用户的收藏（本人或管理员） */
    PageInfo<Favorite> selectByUid(Integer uid, Integer pageNo, Integer pageSize);

    /** 当前登录用户是否已收藏该图书 */
    Boolean check(Integer bid);
}