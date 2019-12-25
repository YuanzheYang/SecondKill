package com.seckill.seckill.result;

public class CodeMsg {


    private int code;
    private String msg;
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "Success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "Server Rrror");

    public static CodeMsg BIND_ERROR = new CodeMsg(500101,  "Parameter Validation Error");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102,"Request Illegal");
    public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103,"Visiting Too Frequent");
    //登陆模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session does not Exist or Expires");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "Password Empty");
    public static CodeMsg MOBILE_EMPTY =  new CodeMsg(500212,  "Mobile Empty");
    public static CodeMsg MOBILE_ERROR =  new CodeMsg(500213,  "Mobile Number Type Wrong");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "Mobile Number does not Exist");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "Password Error");

    //商品模块 5003XX


    //订单模块 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "order not exist");


    //秒杀模块 5005XX
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "Seckill Over");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "Seckill Goods cannot be Bought Repeatedly");
    public static final CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "Seckill Fail");


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


    public CodeMsg fillArgs(Object...args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
