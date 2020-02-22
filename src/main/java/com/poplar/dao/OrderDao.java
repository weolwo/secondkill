package com.poplar.dao;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from sedkillorder where user_id=#{userId} and goods_id=#{goodsId}")
    public SedKillOrder getSedKillOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    public long insert(OrderInfo orderInfo);

    @Insert("insert into sedkillorder (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public Long insertSedKillOrder(SedKillOrder sedKillOrder);

    @Select("select * from order_info where id = #{orderId}")
    public OrderInfo getOrderById(@Param("orderId")long orderId);
}
