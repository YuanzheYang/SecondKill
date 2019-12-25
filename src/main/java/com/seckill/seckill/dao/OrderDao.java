package com.seckill.seckill.dao;

import com.seckill.seckill.domain.MiaoshaOrder;
import com.seckill.seckill.domain.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    public long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);

    @Select("select * from miaosha_order where goods_id=#{goodsId}")
    List<MiaoshaOrder> listByGoodsId(@Param("goodsId") long goodsId);

    @Delete("delete from order_info")
    public void deleteOrders();

    @Delete("delete from miaosha_order")
    public void deleteMiaoshaOrders();
}
