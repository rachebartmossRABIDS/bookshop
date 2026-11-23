package cn.edu.hbpu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SearchLogMapper {

    @Insert("insert into search_log(keyword,time) values(#{keyword},now())")
    int insert(String keyword);

    @Select("select keyword, count(*) as hotCount from search_log "
            + "where time >= date_sub(now(), interval 30 day) "
            + "group by keyword order by hotCount desc, max(time) desc limit 10")
    List<Map<String, Object>> selectHotWords();
}
