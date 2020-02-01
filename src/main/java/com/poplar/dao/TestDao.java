package com.poplar.dao;

import com.poplar.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * by poplar created on 2020/1/31
 */
@Mapper
public interface TestDao {

    @Select("select * from user where id = #{id}")
    public User getById(@Param("id")int id	);

    @Insert("insert into user(id, name)values(#{id}, #{name})")
    public int insert(User user);
}
