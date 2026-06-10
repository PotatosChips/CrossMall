package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Cart;

import java.util.List;

public interface CartService {

    /** 加入购物车（有则累加数量，无则新增） */
    Cart addCart(Long userId, Long productId, Integer quantity);

    /** 根据 id 更新数量 */
    Integer updateCartById(Long id, Long userId, Integer quantity);

    /** 根据 id 删除购物车项 */
    Integer deleteCartById(Long id, Long userId);

    /** 根据 userId 删除购物车项 */
    Integer deleteCartByUserId(Long userId);
    /** 查询某用户的购物车列表 */
    List<Cart> selectCartByUserId(Long userId);
}
