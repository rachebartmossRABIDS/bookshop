package cn.edu.hbpu.mapper;

import cn.edu.hbpu.pojo.Address;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AddressMapper {

    @Select("select aid,uid,name,tel,addr,is_default isDefault,time from address "
            + "where uid=#{uid} order by is_default desc, aid desc")
    List<Address> selectByUid(Integer uid);

    @Select("select aid,uid,name,tel,addr,is_default isDefault,time from address where aid=#{aid}")
    Address selectByAid(Integer aid);

    @Select("select count(*) from address where uid=#{uid}")
    int countByUid(Integer uid);

    @Insert("insert into address(uid,name,tel,addr,is_default,time) "
            + "values(#{uid},#{name},#{tel},#{addr},#{isDefault},#{time})")
    @Options(useGeneratedKeys = true, keyProperty = "aid")
    int insert(Address address);

    @Update("update address set name=#{name},tel=#{tel},addr=#{addr},is_default=#{isDefault} "
            + "where aid=#{aid} and uid=#{uid}")
    int update(Address address);

    @Delete("delete from address where aid=#{aid} and uid=#{uid}")
    int delete(@Param("aid") Integer aid, @Param("uid") Integer uid);

    @Update("update address set is_default=0 where uid=#{uid}")
    int clearDefault(Integer uid);

    @Update("update address set is_default=1 where aid=#{aid} and uid=#{uid}")
    int setDefault(@Param("aid") Integer aid, @Param("uid") Integer uid);

    @Select("select aid,uid,name,tel,addr,is_default isDefault,time from address "
            + "where uid=#{uid} and is_default=1 limit 1")
    Address selectDefault(Integer uid);
}
