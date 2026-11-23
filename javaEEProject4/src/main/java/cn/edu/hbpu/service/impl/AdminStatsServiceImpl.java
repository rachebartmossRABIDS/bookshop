package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.AdminStatsMapper;
import cn.edu.hbpu.service.AdminStatsService;
import cn.edu.hbpu.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {
    private final AdminStatsMapper adminStatsMapper;

    @Override
    public Map<String, Object> stats() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayOrders", adminStatsMapper.countTodayOrders());
        BigDecimal sales = adminStatsMapper.sumTodaySales();
        result.put("todaySales", sales == null ? BigDecimal.ZERO : sales);
        result.put("userCount", adminStatsMapper.countUsers());
        result.put("bookCount", adminStatsMapper.countBooks());
        result.put("sevenDayTrend", adminStatsMapper.selectSevenDayTrend());
        result.put("topBooks", adminStatsMapper.selectTopBooks());
        return result;
    }
}
