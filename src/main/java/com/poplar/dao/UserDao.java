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
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getViaId(@Param("id") Long id);

    @Select("select * from user where id = #{id} and password = #{password}")
    public User login(@Param("id") Long id,@Param("password") String password);
}
