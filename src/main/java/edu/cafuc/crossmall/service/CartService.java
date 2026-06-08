package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Cart;

import java.util.List;

public interface CartService {

    /** TODO: 加入购物车 */
    Integer insertCart(Cart cart);

    /** TODO: 根据 id 查询购物车项 */
    Cart selectCartById(Long id);

    /** TODO: 更新购物车项（如改数量） */
    Integer updateCart(Cart cart);

    /** TODO: 根据 id 删除购物车项 */
    Integer deleteCartById(Long id);

    /** TODO: 查询某用户的购物车列表 */
    List<Cart> selectCartByUserId(Long userId);
}
