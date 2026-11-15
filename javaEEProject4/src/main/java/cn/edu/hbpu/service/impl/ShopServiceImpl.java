package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.mapper.ShopMapper;
import cn.edu.hbpu.pojo.Shop;
import cn.edu.hbpu.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    @Override
    public List<Shop> listByKid(Integer kid){
        return shopMapper.selectBykid(kid);
    }
    @Override
    public Shop getBySid(Integer sid) {
        return shopMapper.selectByPrimaryKey(Long.parseLong(sid+""));
    }
}
