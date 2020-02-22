package com.poplar.controller;

import com.poplar.anno.AccessLimit;
import com.poplar.bean.OrderInfo;
import com.poplar.bean.SedKillOrder;
import com.poplar.bean.User;
import com.poplar.enums.ResultEnum;
import com.poplar.redis.GoodsListKey;
import com.poplar.redis.RedisHelper;
import com.poplar.redis.SedKillKey;
import com.poplar.sevice.GoodsService;
import com.poplar.sevice.OrderService;
import com.poplar.sevice.SedKillService;
import com.poplar.sms.MQSender;
import com.poplar.sms.SedKillMessage;
import com.poplar.util.MD5Util;
import com.poplar.util.ResultEnvelope;
import com.poplar.util.UUIDUtil;
import com.poplar.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.time.Clock;
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

    @Autowired
    private HttpServletResponse httpServletResponse;

    private final HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /*QPS 692 50000*/ /*QPS 836 50000*/
    @PostMapping(value = "{path}/do_sedKill")
    @ResponseBody
    public ResultEnvelope<Integer> do_sedKill(@PathVariable("path") String path, @RequestParam("goodsId") Long goodsId, User user) {
        if (user == null) {
            return ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        if (StringUtils.isEmpty(path)) {
            return ResultEnvelope.failure(ResultEnum.ILLEGALITY_REQUEST);
        }
        //判断请求是否合法
        String result = redisHelper.get(SedKillKey.getSedKillPath, "" + user.getId(), String.class);
        if (!path.equals(result)) {
            return ResultEnvelope.failure(ResultEnum.ILLEGALITY_REQUEST);
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
        Long stock = redisHelper.decr(GoodsListKey.getGoodsStock, "" + goodsId);
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
            redisHelper.set(GoodsListKey.getGoodsStock, "" + e.getId(), e.getStockCount());
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


    /**
     * 获取秒杀路劲
     *
     * @param goodsId
     * @param user
     * @return
     */
    @GetMapping(value = "/path")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 2, needLogin = true)
    public ResultEnvelope<String> getPath(@RequestParam("goodsId") Long goodsId, @RequestParam(value = "verifyCode") Integer verifyCode, User user) {
        if (user == null) {
            return ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        boolean result = sedKillService.checkVerifyCode(user, goodsId, verifyCode);
        if (!result) {
            return ResultEnvelope.failure(ResultEnum.VERIFYCODE_ERROR);
        }
        final String path = MD5Util.md5(UUIDUtil.uuid() + Clock.systemUTC().millis());
        //把路径存入redis
        redisHelper.set(SedKillKey.getSedKillPath, "" + user.getId(), path);
        return ResultEnvelope.success(path);
    }

    /**
     * 生成验证码
     *
     * @param goodsId
     * @param user
     * @return
     */
    @GetMapping(value = "/verifyCode")
    @ResponseBody
    public ResultEnvelope<String> getVerifyCode(@RequestParam("goodsId") Long goodsId, User user) {
        if (user == null) {
            return ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        try {
            BufferedImage image = sedKillService.createVerifyCode(user, goodsId);
            OutputStream out = httpServletResponse.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEnvelope.failure(ResultEnum.FAILURE);
        }
    }
}
