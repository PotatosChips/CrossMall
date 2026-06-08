package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.User;

public interface UserService {
    User Userlogin(String username,String password);
    Integer insertUser(User user);
    Integer selectUserByUsername(String username);
}
