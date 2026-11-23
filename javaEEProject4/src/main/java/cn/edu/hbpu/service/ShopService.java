package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Shop;

import java.util.List;

public interface ShopService {
    public List<Shop> listByKid(Integer kid);
    public Shop getBySid(Integer sid);

    /** 全部店铺（公开浏览 + 管理端列表） */
    List<Shop> listAll();

    /** 管理员新增/编辑店铺（sid 为空新增，否则更新） */
    Boolean save(Shop shop);

    /** 管理员删除店铺（被图书引用时拒绝） */
    int remove(Integer sid);
}