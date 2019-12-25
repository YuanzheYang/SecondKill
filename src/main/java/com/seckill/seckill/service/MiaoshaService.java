package com.seckill.seckill.service;

import com.seckill.seckill.controller.MiaoshaController;
import com.seckill.seckill.domain.MiaoshaOrder;
import com.seckill.seckill.domain.MiaoshaUser;
import com.seckill.seckill.domain.OrderInfo;
import com.seckill.seckill.redis.MiaoshaKey;
import com.seckill.seckill.redis.RedisService;
import com.seckill.seckill.util.MD5Util;
import com.seckill.seckill.util.UUIDUtil;
import com.seckill.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(pathOld);
    }

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        //order_info maiosha_order
        if(success){
            return orderService.createOrder(user, goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(!isOver) {
                return 0;
            }else{//此商品的秒杀已经结束，但是可能订单还在生成中
                //获取所有的秒杀订单，判断订单数量和参与秒杀的商品数量
                List<MiaoshaOrder> orders = orderService.getAllMiaoshaOrdersByGoodsId(goodsId);
                if(orders == null || orders.size() < MiaoshaController.getGoodsStockOriginal(goodsId)) {
                    return 0; //订单还在生成中
                }else{
                    //判断是否有此用户的订单
                    MiaoshaOrder o = get(orders,userId);
                    if(o != null) {
                        return o.getOrderId();
                    }else{
                        return -1;
                    }
                }
            }
        }
    }

    private MiaoshaOrder get(List<MiaoshaOrder> orders, Long userId) {
        if(orders == null || orders.size() <= 0) {
            return null;
        }
        for(MiaoshaOrder order: orders) {
            if(order.getUserId().equals(userId)) {
                return order;
            }
        }
        return null;
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+ goodsId);
    }

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    public BufferedImage createCaptcha(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        // create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);
        //draw the border
        g.setColor(Color.black);
        g.drawRect(0,0,width-1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for(int i = 0;i <50; i ++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        // generate a random code
        String captcha = generateCaptcha(rdm);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(captcha,2,24);
        g.dispose();
        //put the captcha into redis
        int rnd = calc(captcha);
        redisService.set(MiaoshaKey.getMiaoshaCaptcha,user.getId()+","+goodsId,rnd);
        return image;
    }

    private static int calc(String exp) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+','-','*'};
    private String generateCaptcha(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+num1+op1+num2+op2+num3;
        return exp;
    }

    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    public boolean checkCaptcha(MiaoshaUser user, long goodsId, int captcha) {
        if(user == null || goodsId<= 0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaCaptcha,user.getId()+","+goodsId,Integer.class);
        if(codeOld == null || codeOld - captcha != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaCaptcha,user.getId()+","+goodsId);
        return true;
    }
}
