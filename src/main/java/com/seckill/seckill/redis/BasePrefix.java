package com.seckill.seckill.redis;

public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix){
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }
    public int expireSeconds(){
        return expireSeconds;
    }
    public String getPrefix(){
        String classname = getClass().getSimpleName();
        return classname + ":" + prefix;
    }
}
