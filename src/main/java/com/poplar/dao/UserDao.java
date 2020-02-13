package com.poplar.dao;

import com.poplar.bean.User;
import org.apache.ibatis.annotations.*;

/**
 * by poplar created on 2020/1/31
 */
@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getViaId(@Param("id") Long id);

    @Select("select * from user where id = #{id} and password = #{password}")
    User login(@Param("id") Long id, @Param("password") String password);

    @Update("update user set password = #{password} where id = #{id}")
    int update(User u);
}
