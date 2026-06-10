package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.CartMapper;
import edu.cafuc.crossmall.pojo.Cart;
import edu.cafuc.crossmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public Cart addCart(Long userId, Long productId, Integer quantity) {
        Cart existing = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (existing != null) {
            int newQuantity = existing.getQuantity() + quantity;
            cartMapper.updateCartById(existing.getId(), userId, newQuantity);
            existing.setQuantity(newQuantity);
            return existing;
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cartMapper.insertCart(cart);
        return cart;
    }

    @Override
    public Integer updateCartById(Long id, Long userId, Integer quantity) {
        return cartMapper.updateCartById(id, userId, quantity);
    }

    @Override
    public Integer deleteCartById(Long id, Long userId) {
        return cartMapper.deleteCartById(id, userId);
    }

    @Override
    /** 根据 userId 删除购物车项 */
    public Integer deleteCartByUserId(Long userId){
        return cartMapper.deleteCartByUserId(userId);
    };

    @Override
    public List<Cart> selectCartByUserId(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }
}
