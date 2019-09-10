package com.seckill.seckill.service;

import com.seckill.seckill.dao.MiaoshaUserDao;
import com.seckill.seckill.domain.MiaoshaUser;
import com.seckill.seckill.exception.GlobalException;
import com.seckill.seckill.redis.MiaoshaUserKey;
import com.seckill.seckill.redis.RedisService;
import com.seckill.seckill.result.CodeMsg;
import com.seckill.seckill.util.MD5Util;
import com.seckill.seckill.util.UUIDUtil;
import com.seckill.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
public class MiaoshaUserService {

    private static final  String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public boolean login(LoginVo loginVo)  {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //  verify the password
        String dbPass = user.getPassword();
        String slatDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,slatDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new
        return true;
    }
}
