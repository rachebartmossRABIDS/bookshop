package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Address;

import java.util.List;

public interface AddressService {
    List<Address> list();
    Address save(Address address);
    int remove(Integer aid);
    void setDefault(Integer aid);
}
