package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.KindMapper;
import cn.edu.hbpu.mapper.ShopMapper;
import cn.edu.hbpu.pojo.Shop;
import cn.edu.hbpu.service.ShopService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final KindMapper kindMapper;
    private final OperationLogService operationLogService;

    @Override
    public List<Shop> listByKid(Integer kid){
        return shopMapper.selectBykid(kid);
    }

    @Override
    public Shop getBySid(Integer sid) {
        return shopMapper.selectByPrimaryKey(Long.parseLong(sid+""));
    }

    @Override
    public List<Shop> listAll() {
        return shopMapper.selectAll();
    }

    @Override
    @Transactional
    public Boolean save(Shop shop) {
        requireAdmin();
        if (shop == null || ValidateUtil.isBlank(shop.getName()) || shop.getName().trim().length() > 255) {
            throw new BusinessException("请填写店铺名称（不超过 255 字）");
        }
        if (shop.getKid() == null) {
            throw new BusinessException("请选择店铺所属分类");
        }
        if (kindMapper.selectByPrimaryKey(shop.getKid().longValue()) == null) {
            throw new BusinessException("所属分类不存在");
        }
        shop.setName(shop.getName().trim());
        if (ValidateUtil.isBlank(shop.getState())) {
            shop.setState("0");
        }
        // shop 表无 content 列，避免写入
        shop.setContent(null);
        if (shop.getSid() == null) {
            boolean created = shopMapper.insertSelective(shop) == 1;
            if (created) {
                operationLogService.log("新增店铺", "店铺 #" + shop.getSid() + "：" + shop.getName());
            }
            return created;
        }
        if (shopMapper.selectByPrimaryKey(shop.getSid().longValue()) == null) {
            throw new BusinessException("店铺不存在");
        }
        boolean updated = shopMapper.updateByPrimaryKeySelective(shop) == 1;
        if (updated) {
            operationLogService.log("编辑店铺", "店铺 #" + shop.getSid() + "：" + shop.getName());
        }
        return updated;
    }

    @Override
    @Transactional
    public int remove(Integer sid) {
        requireAdmin();
        if (sid == null || shopMapper.selectByPrimaryKey(sid.longValue()) == null) {
            throw new BusinessException("店铺不存在");
        }
        try {
            int result = shopMapper.deleteByPrimaryKey(sid.longValue());
            if (result > 0) {
                operationLogService.log("删除店铺", "店铺 #" + sid);
            }
            return result;
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("该店铺下仍有图书，不能删除");
        }
    }

    private void requireAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
    }
}
