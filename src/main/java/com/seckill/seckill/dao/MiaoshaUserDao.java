package com.seckill.seckill.dao;

import com.seckill.seckill.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface MiaoshaUserDao {
    @Select("select * from miaosha_user where id = ")
    public MiaoshaUser getById(long id);
}
