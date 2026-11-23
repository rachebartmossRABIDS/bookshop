package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.KindMapper;
import cn.edu.hbpu.pojo.Kind;
import cn.edu.hbpu.service.KindService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.service.CacheService;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class KindServiceImpl implements KindService {
    private final KindMapper kindMapper;
    private final OperationLogService operationLogService;
    private final CacheService cacheService;

    @Override
    public List<Kind> listAll() {
        List<Kind> cached = cacheService.get("kind:list", new TypeReference<List<Kind>>() { });
        if (cached != null) {
            return cached;
        }
        List<Kind> list = kindMapper.listAll();
        cacheService.put("kind:list", list, 300);
        return list;
    }

    @Override
    @Transactional
    public Boolean save(Kind kind) {
        requireAdmin();
        if (kind == null || ValidateUtil.isBlank(kind.getContent()) || kind.getContent().trim().length() > 255) {
            throw new BusinessException("请填写分类名称（不超过 255 字）");
        }
        kind.setContent(kind.getContent().trim());
        if (kind.getKid() == null) {
            boolean created = kindMapper.insertSelective(kind) == 1;
            if (created) {
                operationLogService.log("新增分类", "分类 #" + kind.getKid() + "：" + kind.getContent());
                cacheService.evict("kind:list");
            }
            return created;
        }
        if (kindMapper.selectByPrimaryKey(kind.getKid().longValue()) == null) {
            throw new BusinessException("分类不存在");
        }
        boolean updated = kindMapper.updateByPrimaryKeySelective(kind) == 1;
        if (updated) {
            operationLogService.log("编辑分类", "分类 #" + kind.getKid() + "：" + kind.getContent());
            cacheService.evict("kind:list");
        }
        return updated;
    }

    @Override
    @Transactional
    public int remove(Integer kid) {
        requireAdmin();
        if (kid == null || kindMapper.selectByPrimaryKey(kid.longValue()) == null) {
            throw new BusinessException("分类不存在");
        }
        try {
            int result = kindMapper.deleteByPrimaryKey(kid.longValue());
            if (result > 0) {
                operationLogService.log("删除分类", "分类 #" + kid);
                cacheService.evict("kind:list");
            }
            return result;
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("该分类下仍有图书或店铺，不能删除");
        }
    }

    private void requireAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
    }
}
