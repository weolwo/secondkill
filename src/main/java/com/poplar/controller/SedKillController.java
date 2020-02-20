package com.poplar.controller;

import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.enums.ResultEnum;
import com.poplar.redis.GoodsListPrefix;
import com.poplar.redis.RedisHelper;
import com.poplar.sevice.GoodsService;
import com.poplar.sevice.OrderService;
import com.poplar.sevice.SedKillService;
import com.poplar.sms.MQSender;
import com.poplar.sms.SedKillMessage;
import com.poplar.util.ResultEnvelope;
import com.poplar.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * by poplar created on 2020/2/9
 */
@Controller
@RequestMapping(value = "/sedKill")
public class SedKillController implements InitializingBean {

    @Autowired
    private SedKillService sedKillService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private MQSender mqSender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /*QPS 692 50000*/ /*QPS 836 50000*/
    @PostMapping(value = "/do_sedKill")
    @ResponseBody
    public ResultEnvelope<Integer> do_sedKill(@RequestParam("goodsId") Long goodsId, User user) {
        if (user == null) {
            return ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        if (localOverMap.get(goodsId)) {
            return ResultEnvelope.failure(ResultEnum.SEDKILL_OVER);
        }
        //判断是否重复秒杀
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ResultEnvelope.failure(ResultEnum.REPEAT_SEDKILL);
        }
        //预减库存
        Long stock = redisHelper.decr(GoodsListPrefix.getGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return ResultEnvelope.failure(ResultEnum.SEDKILL_OVER);

        }

        localOverMap.put(goodsId, false);
        SedKillMessage message = new SedKillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        mqSender.sendSedKillMessage(message);
        return ResultEnvelope.success(0);
        /*GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断商品库存
        int stock = goodsService.getStockCount(goodsId);
        if (stock <= 0) {
            return ResultEnvelope.failure(ResultEnum.SEDKILL_OVER);
        }
        //判断是否重复秒杀
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ResultEnvelope.failure(ResultEnum.REPEAT_SEDKILL);
        }
        //下订单，写入库存
        OrderInfo orderInfo = sedKillService.sedKill(user, goodsVo);
        return ResultEnvelope.success(orderInfo);*/
    }

    /*QPS 692 50000*/
    @PostMapping(value = "/do_sedKill2")
    public String do_sedKill2(Model model, @RequestParam("goodsId") Long goodsId, User user) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断商品库存
        int stock = goodsService.getStockCount(goodsId);
        if (stock <= 0) {
            model.addAttribute("error", ResultEnum.SEDKILL_OVER.getMessage());
            return "fail";
        }
        //判断是否重复秒杀
        SedKillOrder order = orderService.getSedKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("error", ResultEnum.REPEAT_SEDKILL.getMessage());
            return "fail";
        }
        //下订单，写入库存
        OrderInfo orderInfo = sedKillService.sedKill(user, goodsVo);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        return "order_detail";
    }

    //初始化的时候预先把库存写入redis
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        goodsVos.forEach(e -> {
            redisHelper.set(GoodsListPrefix.getGoodsStock, "" + e.getId(), e.getStockCount());
            localOverMap.put(e.getId(), false);
        });
    }

    /**
     * 秒杀成功返回订单id
     * 失败返回-1
     * 派对中 0
     *
     * @param goodsId
     * @param user
     * @return
     */
    @GetMapping(value = "/result")
    @ResponseBody
    public ResultEnvelope<Long> result(@RequestParam("goodsId") Long goodsId, User user) {
        Long orderId = sedKillService.getSedKillResult(user.getId(), goodsId);
        return ResultEnvelope.success(orderId);
    }

}
