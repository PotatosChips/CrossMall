package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.UserMapper;
import edu.cafuc.crossmall.service.UserService;
import edu.cafuc.crossmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User Userlogin(String username, String password) {
        return userMapper.selectUserByNameAndPassword(username,password);
    }

    @Override
    public Integer insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public Integer selectUserByUsername(String username) {
        return userMapper.selectUserByName(username);
    }
}
