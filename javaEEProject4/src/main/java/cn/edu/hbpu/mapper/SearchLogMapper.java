package cn.edu.hbpu.mapper;

import org.apache.ibatis.annotations.Insert;

public interface SearchLogMapper {

    @Insert("insert into search_log(keyword,time) values(#{keyword},now())")
    int insert(String keyword);
}
