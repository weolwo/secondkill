package com.poplar.dao;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {
	
	@Select("select * from sedkillorder where user_id=#{userId} and goods_id=#{goodsId}")
	public SedKillOrder getSedKillOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

	@Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
			+ "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
	@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
	public long insert(OrderInfo orderInfo);
	
	@Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
	public int insertSedKillOrder(SedKillOrder sedKillOrder);

	
}
