package com.poplar.sevice;

import com.poplar.bean.SedKillGoods;
import com.poplar.dao.GoodsDao;
import com.poplar.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * by poplar created on 2020/2/9
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public int reduceStock(Long goodsId) {
        return goodsDao.reduceStock(goodsId);
    }

    public int getStockCount(Long goodsId) {
        return goodsDao.getStockCount(goodsId);
    }
}
