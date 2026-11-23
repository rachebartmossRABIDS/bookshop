package cn.edu.hbpu.mapper;

import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdminStatsMapper {

    @Select("select count(*) from `order` where date(time)=curdate()")
    int countTodayOrders();

    @Select("select coalesce(sum(amount),0) from `order` "
            + "where date(time)=curdate() and state in ('1','2','3','4')")
    BigDecimal sumTodaySales();

    @Select("select count(*) from user")
    int countUsers();

    @Select("select count(*) from book")
    int countBooks();

    @Select("select date(time) as day, count(*) as orderCount from `order` "
            + "where time >= date_sub(curdate(), interval 6 day) "
            + "group by date(time) order by day")
    List<Map<String, Object>> selectSevenDayTrend();

    @Select("select bid,name,volume from book order by volume desc, bid asc limit 5")
    List<Map<String, Object>> selectTopBooks();
}
