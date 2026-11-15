package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Kind;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
* @author joker
* @description 针对表【kind】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.Kind
*/
public interface KindMapper {
    @Select("select * from kind")
    List<Kind> listAll();

    int deleteByPrimaryKey(Long id);

    int insert(Kind record);

    int insertSelective(Kind record);

    Kind selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Kind record);

    int updateByPrimaryKey(Kind record);

}
