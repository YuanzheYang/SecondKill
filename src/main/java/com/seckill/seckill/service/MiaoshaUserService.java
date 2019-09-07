package com.seckill.seckill.service;

import com.seckill.seckill.dao.MiaoshaUserDao;
import com.seckill.seckill.domain.MiaoshaUser;
import com.seckill.seckill.result.CodeMsg;
import com.seckill.seckill.util.MD5Util;
import com.seckill.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public CodeMsg login(LoginVo loginVo)  {
        if(loginVo == null) {
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        //  verify the password
        String dbPass = user.getPassword();
        String slatDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,slatDB);
        if(!calcPass.equals(dbPass)) {
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
