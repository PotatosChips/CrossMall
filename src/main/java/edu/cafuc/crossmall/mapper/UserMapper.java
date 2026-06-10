package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AdminUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectUserByNameAndPassword(String username,String password);
    Integer selectUserByName(String name);
    Integer insertUser(User user);

    User selectById(Long id);

    List<AdminUserVO> selectAllForAdmin();

    Integer updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
