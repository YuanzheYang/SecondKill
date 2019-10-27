package com.seckill.seckill.controller;

import com.seckill.seckill.domain.MiaoshaUser;
import com.seckill.seckill.redis.RedisService;
import com.seckill.seckill.result.Result;
import com.seckill.seckill.service.GoodsService;
import com.seckill.seckill.service.MiaoshaUserService;
import com.seckill.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }
}
