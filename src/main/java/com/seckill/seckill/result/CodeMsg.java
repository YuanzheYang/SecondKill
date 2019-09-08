package com.seckill.seckill.result;

public class CodeMsg {
    private int code;
    private String msg;
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");

    public static CodeMsg BIND_ERROR = new CodeMsg(500101,  "parameter validation error");
    //登陆模块 5002XX
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "password empty");
    public static CodeMsg MOBILE_EMPTY =  new CodeMsg(500212,  "mobile empty");
    public static CodeMsg MOBILE_ERROR =  new CodeMsg(500213,  "mobile number type wrong");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "mobile number does not exist");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "password error");

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
