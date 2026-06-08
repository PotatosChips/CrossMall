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
    public Integer insertCart(Cart cart) {
        return cartMapper.insertCart(cart);
    }

    @Override
    public Cart selectCartById(Long id) {
        return cartMapper.selectCartById(id);
    }

    @Override
    public Integer updateCart(Cart cart) {
        return cartMapper.updateCart(cart);
    }

    @Override
    public Integer deleteCartById(Long id) {
        return cartMapper.deleteCartById(id);
    }

    @Override
    public List<Cart> selectCartByUserId(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }
}
