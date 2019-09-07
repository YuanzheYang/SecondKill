package com.seckill.seckill.service;

import com.seckill.seckill.dao.UserDao;
import com.seckill.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;
    public User getById(int id) {
        return userDao.getById(id);
    }

}
