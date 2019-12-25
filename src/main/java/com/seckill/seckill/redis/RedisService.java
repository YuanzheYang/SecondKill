package com.seckill.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /*
    * get one object
    * */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str,clazz);
            return t;

        }finally{
            returnToPool(jedis);
        }
    }
    /*
    *
    * set one object
    * */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try{
            jedis = jedisPool. getResource();
            String str = beanToString(value);
            if(str  == null || str.length()  <= 0) {
                return false;
            }
            String realKey  = prefix.getPrefix() + key;
            int  seconds =  prefix.expireSeconds();
            if(seconds <= 0) {
                jedis.set(realKey,str);
            }else{
                jedis.setex(realKey, seconds,  str);
            }

            return true;

        }finally{
            returnToPool(jedis);
        }
    }
    /*
        delete
     */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+ key;
            long ret = jedis.del(realKey);
            return ret  > 0;
        }finally {
            returnToPool(jedis);
        }
    }
    /*
    *  judge whether key exists
    * */
    public <T> boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis  = null;
        try{
            jedis = jedisPool.getResource();
            String realKey =  prefix.getPrefix() + key;
            return jedis.exists(realKey);

        }finally{
            returnToPool(jedis);
        }
    }
    /*
    * increment value
    * */
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            // generate real key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally{
            returnToPool(jedis);
        }
    }

    /*
    decrement value
    */
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            // generate real key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally{
            returnToPool(jedis);
        }
    }
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys == null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        }catch (final Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    private List<String> scanKeys(String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor,sp);
                List<String> result = ret.getResult();
                if(result != null && result.size() >0) {
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getCursor();
            }while(!cursor.equals("0"));
            return keys;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public static <T> String beanToString(T value) {
        if(value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return "" + value;
        }else if(clazz == String.class) {
            return (String) value;
        }else if(clazz == long.class || clazz == Long.class) {
            return "" + value;
        }else{
            return JSON.toJSONString(value);
        }

    }

    public static <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz ==  Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return (T)Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }

    }

    private void returnToPool(Jedis jedis){
        if(jedis != null) {
            jedis.close();
        }
    }

}
