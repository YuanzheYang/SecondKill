package com.seckill.seckill.redis;

public class MiaoshaKey extends BasePrefix{
    private MiaoshaKey(int expireSeconds, String prefix){
        super(expireSeconds, prefix);
    }
    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(0,"mp");
    public static MiaoshaKey getMiaoshaCaptcha = new MiaoshaKey(300,"vc");

}
