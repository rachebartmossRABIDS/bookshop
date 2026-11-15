package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Shop;

import java.util.List;

public interface ShopService {
    public List<Shop> listByKid(Integer kid);
    public Shop getBySid(Integer sid);
}
