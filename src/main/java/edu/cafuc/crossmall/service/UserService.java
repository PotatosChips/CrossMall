package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AdminUserVO;

import java.util.List;

public interface UserService {
    User Userlogin(String username,String password);
    Integer insertUser(User user);
    Integer selectUserByUsername(String username);

    List<AdminUserVO> selectAllUsersForAdmin();

    Integer updateUserStatus(Long targetUserId, Integer status, Long operatorUserId);
}
