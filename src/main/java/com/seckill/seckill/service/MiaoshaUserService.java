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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final  String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id){
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById,"" + id, MiaoshaUser.class);
        if(user != null) {
            return user;
        }
        // get from database
        user = miaoshaUserDao.getById(id);
        if(user != null) {
            redisService.set(MiaoshaUserKey.getById,""+id, user);
        }
        return user;
    }

    public boolean updataPassword(String token, long id, String formPass) {
        // fetch user
        MiaoshaUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // update database
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        // deal with redis
        redisService.delete(MiaoshaUserKey.getById,""+ id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVo loginVo)  {
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
        addCookie(response, token, user);
        return token;
    }

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if(user != null) {
            addCookie(response, token, user);
        }

        return user;
    }

    public void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
