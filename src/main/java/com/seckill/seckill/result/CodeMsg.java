package com.seckill.seckill.result;

public class CodeMsg {
    private int code;
    private String msg;
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "password empty");
    public static CodeMsg MOBILE_EMPTY =  new CodeMsg(500212,  "mobile empty");
    public static CodeMsg MOBILE_ERROR =  new CodeMsg(500213,  "mobile number type wrong");
    //登陆模块 5002XX


    //商品模块 5003XX


    //订单模块 5004XX


    //秒杀模块 5005XX
    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }



    public String getMsg() {
        return msg;
    }


}
