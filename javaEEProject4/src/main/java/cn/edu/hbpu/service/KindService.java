package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Kind;

import java.util.List;

public interface KindService {
    List<Kind> listAll();

    /** 管理员新增/编辑分类（kid 为空新增，否则更新） */
    Boolean save(Kind kind);

    /** 管理员删除分类（被图书/店铺引用时拒绝） */
    int remove(Integer kid);
}