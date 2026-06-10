package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {

    /** 加入购物车 */
    Integer insertCart(Cart cart);

    /** 加购前判断是否已有该商品 */
    Cart selectByUserIdAndProductId(@Param("userId") Long userId,
                                    @Param("productId") Long productId);

    /** 根据 id 更新数量 */
    Integer updateCartById(@Param("id") Long id,
                           @Param("userId") Long userId,
                           @Param("quantity") Integer quantity);

    /** 根据 id 删除购物车项 */
    Integer deleteCartById(@Param("id") Long id,
                           @Param("userId") Long userId);

    /** 根据 userId 删除购物车项 */
    Integer deleteCartByUserId(Long userId);

    /** 查询某用户的购物车列表 */
    List<Cart> selectCartByUserId(Long userId);
}
