package com.seckill.seckill.redis;

public class MiaoshaKey extends BasePrefix{
    private MiaoshaKey(String prefix){
        super(prefix);
    }
    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}