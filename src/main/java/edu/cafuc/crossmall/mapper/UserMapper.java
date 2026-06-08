package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectUserByNameAndPassword(String username,String password);
    Integer selectUserByName(String name);
    Integer insertUser(User user);
}
