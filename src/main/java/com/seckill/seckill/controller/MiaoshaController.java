package com.seckill.seckill.controller;

import com.seckill.seckill.access.AccessLimit;
import com.seckill.seckill.domain.MiaoshaOrder;
import com.seckill.seckill.domain.MiaoshaUser;
import com.seckill.seckill.domain.OrderInfo;
import com.seckill.seckill.rabbitmq.MQSender;
import com.seckill.seckill.rabbitmq.MiaoshaMessage;
import com.seckill.seckill.redis.GoodsKey;
import com.seckill.seckill.redis.MiaoshaKey;
import com.seckill.seckill.redis.OrderKey;
import com.seckill.seckill.redis.RedisService;
import com.seckill.seckill.result.CodeMsg;
import com.seckill.seckill.result.Result;
import com.seckill.seckill.service.GoodsService;
import com.seckill.seckill.service.MiaoshaService;
import com.seckill.seckill.service.MiaoshaUserService;
import com.seckill.seckill.service.OrderService;
import com.seckill.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender sender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<>();
    private static HashMap<Long,Integer> stockMap = new HashMap<>();
    /**
     QPS 1654   before rabbit mq
     1000 * 10
     */

    /**
     *
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null) {
            return;
        }
        for(GoodsVo goods: goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(),false);
            stockMap.put(goods.getId(), goods.getStockCount());
        }
    }
    /**获取初始的商品秒杀数量*/
    public static int getGoodsStockOriginal(long goodsId) {
        Integer stock = stockMap.get(goodsId);
        if(stock == null) {
            return 0;
        }
        return stock;
    }
    @RequestMapping(value ="/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(100);
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(), 100);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }
    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path") String path){
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //check path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //reduce the visit of redis
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if(stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if( order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);
        /*
        //判断库存

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

        return Result.success(orderInfo);
        */
    }
    @AccessLimit(seconds = 5, maxCount = 5,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam("captcha")int captcha){
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkCaptcha(user, goodsId,captcha);
        // check whether the captcha input by the user is right
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillCaptcha(HttpServletResponse response, MiaoshaUser user,
                                            @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try{
            BufferedImage image = miaoshaService.createCaptcha(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }


    /**
     * orderId:成功
     * -1：秒杀失败
     * 0：排队中
     *
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
}
