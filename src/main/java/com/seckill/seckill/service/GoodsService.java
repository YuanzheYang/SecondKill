package com.seckill.seckill.service;

import com.seckill.seckill.dao.GoodsDao;
import com.seckill.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

}
