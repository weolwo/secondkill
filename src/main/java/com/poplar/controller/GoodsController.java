package com.poplar.controller;

import com.poplar.bean.User;
import com.poplar.enums.ResultEnum;
import com.poplar.redis.GoodsDetailPrefix;
import com.poplar.redis.GoodsListPrefix;
import com.poplar.redis.RedisHelper;
import com.poplar.sevice.GoodsService;
import com.poplar.util.ResultEnvelope;
import com.poplar.vo.GoodsDetailVo;
import com.poplar.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;

    //QPS 578 50000 Q
    //页面静态化缓存
    @GetMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String to_list(Model model, User user) {
        model.addAttribute("user", user);
        String html = redisHelper.get(GoodsListPrefix.getGoodsList, "", String.class);
        if (StringUtils.isNotEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsVoList);
        //return "goods_list";
        //手动渲染
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (StringUtils.isNotEmpty(html)) {
            redisHelper.set(GoodsListPrefix.getGoodsList, "", html);
        }
        return html;
    }

    @GetMapping(value = "/to_detail/{goodsId}")
    @ResponseBody
    public ResultEnvelope<GoodsDetailVo> to_detail(User user, @PathVariable("goodsId") Long goodsId) {
        if (user == null) {
            ResultEnvelope.failure(ResultEnum.USER_NOT_LOGIN);
        }
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int status = 0;
        int remainTime = 0;
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();
        if (nowTime < startTime) {
            status = 0;//秒杀还没开始
            //倒计时
            remainTime = (int) (startTime - nowTime) / 1000;
        } else if (nowTime > startTime) {
            status = 1;//秒正在秒杀
        } else {
            status = 2;//秒杀结束
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setRemainSeconds(remainTime);
        goodsDetailVo.setStatus(status);
        goodsDetailVo.setUser(user);
        return ResultEnvelope.success(goodsDetailVo);
    }

    @GetMapping(value = "/to_detail2/{id}", produces = "text/html")
    @ResponseBody
    public String to_detail2(Model model, User user, @PathVariable("id") Long id) {
        model.addAttribute("user", user);
        String html = redisHelper.get(GoodsDetailPrefix.getGoodsDetail, "", String.class);
        if (StringUtils.isNotEmpty(html)) {
            return html;
        }
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(id);
        model.addAttribute("goods", goods);
        int status = 0;
        int remainTime = 0;
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();
        if (nowTime < startTime) {
            status = 0;//秒杀还没开始
            //倒计时
            remainTime = (int) (startTime - nowTime) / 1000;
        } else if (nowTime > startTime) {
            status = 1;//秒正在秒杀
        } else {
            status = 2;//秒杀结束
        }
        model.addAttribute("status", status);
        model.addAttribute("remainTime", remainTime);
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if (StringUtils.isNotEmpty(html)) {
            redisHelper.set(GoodsDetailPrefix.getGoodsDetail, "" + id, html);
        }
        // return "goods_detail";
        return html;
    }
}
