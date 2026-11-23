package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.AddressMapper;
import cn.edu.hbpu.pojo.Address;
import cn.edu.hbpu.service.AddressService;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;

    @Override
    public List<Address> list() {
        return addressMapper.selectByUid(requireLogin());
    }

    @Override
    @Transactional
    public Address save(Address address) {
        Integer uid = requireLogin();
        if (address == null || ValidateUtil.isBlank(address.getName()) || address.getName().trim().length() > 100) {
            throw new BusinessException("请填写收件人（不超过 100 字）");
        }
        if (!ValidateUtil.isTel(address.getTel())) {
            throw new BusinessException("请填写正确的 11 位手机号");
        }
        if (ValidateUtil.isBlank(address.getAddr()) || address.getAddr().trim().length() > 500) {
            throw new BusinessException("请填写收件地址（不超过 500 字）");
        }
        address.setUid(uid);
        address.setName(address.getName().trim());
        address.setTel(address.getTel().trim());
        address.setAddr(address.getAddr().trim());
        int count = addressMapper.countByUid(uid);
        if (address.getAid() == null) {
            if (count >= 20) {
                throw new BusinessException("收货地址最多保存 20 条");
            }
            address.setTime(new Date());
            address.setIsDefault(count == 0 || Integer.valueOf(1).equals(address.getIsDefault()) ? 1 : 0);
            if (address.getIsDefault() == 1) {
                addressMapper.clearDefault(uid);
            }
            addressMapper.insert(address);
            return addressMapper.selectByAid(address.getAid());
        }
        Address old = addressMapper.selectByAid(address.getAid());
        if (old == null || !uid.equals(old.getUid())) {
            throw new BusinessException("无权编辑他人的收货地址");
        }
        address.setIsDefault(Integer.valueOf(1).equals(address.getIsDefault()) ? 1 : 0);
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefault(uid);
        }
        if (addressMapper.update(address) != 1) {
            throw new BusinessException("保存收货地址失败");
        }
        return addressMapper.selectByAid(address.getAid());
    }

    @Override
    @Transactional
    public int remove(Integer aid) {
        Integer uid = requireLogin();
        Address old = requireOwned(aid, uid);
        int result = addressMapper.delete(aid, uid);
        if (result > 0 && Integer.valueOf(1).equals(old.getIsDefault())) {
            List<Address> remaining = addressMapper.selectByUid(uid);
            if (!remaining.isEmpty()) {
                addressMapper.setDefault(remaining.get(0).getAid(), uid);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void setDefault(Integer aid) {
        Integer uid = requireLogin();
        requireOwned(aid, uid);
        addressMapper.clearDefault(uid);
        if (addressMapper.setDefault(aid, uid) != 1) {
            throw new BusinessException("设置默认地址失败");
        }
    }

    private Address requireOwned(Integer aid, Integer uid) {
        if (aid == null) {
            throw new BusinessException("地址编号不能为空");
        }
        Address address = addressMapper.selectByAid(aid);
        if (address == null || !uid.equals(address.getUid())) {
            throw new BusinessException("无权操作他人的收货地址");
        }
        return address;
    }

    private Integer requireLogin() {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        return uid;
    }
}
