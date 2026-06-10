package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.UserMapper;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AdminUserVO;
import edu.cafuc.crossmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final int ROLE_ADMIN = 2;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User Userlogin(String username, String password) {
        return userMapper.selectUserByNameAndPassword(username, password);
    }

    @Override
    public Integer insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public Integer selectUserByUsername(String username) {
        return userMapper.selectUserByName(username);
    }

    @Override
    public List<AdminUserVO> selectAllUsersForAdmin() {
        return userMapper.selectAllForAdmin();
    }

    @Override
    public Integer updateUserStatus(Long targetUserId, Integer status, Long operatorUserId) {
        if (targetUserId == null || status == null) {
            throw new RuntimeException("参数无效");
        }
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态无效");
        }
        if (operatorUserId != null && operatorUserId.equals(targetUserId)) {
            throw new RuntimeException("不能操作自己的账号");
        }

        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new RuntimeException("用户不存在");
        }
        if (target.getRole() != null && target.getRole() == ROLE_ADMIN) {
            throw new RuntimeException("不能封禁管理员账号");
        }

        return userMapper.updateStatus(targetUserId, status);
    }
}
