package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Shop;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author joker
* @description 针对表【shop】的数据库操作Mapper
* @createDate 2024-12-17 01:33:08
* @Entity generator.pojo.Shop
*/
public interface ShopMapper {
    @Select("select sid,`name`,logo,score from shop where kid=#{kid} order by sid desc ")
    List<Shop> selectBykid(Integer kid);

    @Select("select sid,`name`,logo,score from shop where sid=#{sid}")
    Shop getBySidPrimary(Integer sid);
    int deleteByPrimaryKey(Long id);

    int insert(Shop record);

    int insertSelective(Shop record);

    Shop selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Shop record);

    int updateByPrimaryKey(Shop record);

}
