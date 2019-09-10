package com.seckill.seckill.redis;

public class MiaoshaUserKey extends BasePrefix {
    private MiaoshaUserKey(String prefix) {
        super(prefix);
    }
    public static MiaoshaUserKey token  = new MiaoshaUserKey("id");
    public static MiaoshaUserKey getByName  = new MiaoshaUserKey("name");
}
