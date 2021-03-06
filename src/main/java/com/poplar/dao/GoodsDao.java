package com.poplar.dao;

import com.poplar.bean.SedKillGoods;
import com.poplar.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.price from sedkillgoods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.price from sedkillgoods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    //解决超卖的问题
    @Update("update sedkillgoods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count>0")
    public int reduceStock(@Param("goodsId") Long goodsId);

    @Select(value = "select stock_count as stockCount from sedkillgoods where goods_id = #{goodsId}")
    int getStockCount(@Param("goodsId") Long goodsId);
}
