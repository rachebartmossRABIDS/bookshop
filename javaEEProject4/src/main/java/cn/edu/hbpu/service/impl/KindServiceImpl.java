package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.mapper.KindMapper;
import cn.edu.hbpu.pojo.Kind;
import cn.edu.hbpu.service.KindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class KindServiceImpl implements KindService {
    private final KindMapper kindMapper;
    @Override
    public List<Kind> listAll() {
        return kindMapper.listAll();
    }
}

