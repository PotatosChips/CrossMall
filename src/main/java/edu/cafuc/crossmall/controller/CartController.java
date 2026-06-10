package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Cart;
import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public Result addProductToCart(Long productId, Integer quantity, @AuthenticationPrincipal User user) {
        Cart cart = cartService.addCart(user.getId(), productId, quantity);
        return Result.okData(cart);
    }

    @PutMapping("/{id}")
    public Result updateProductFromCart(@PathVariable Long id, Integer quantity, @AuthenticationPrincipal User user) {
        Integer rows = cartService.updateCartById(id, user.getId(), quantity);
        return rows != null && rows > 0 ? Result.ok() : Result.fail("更新购物车失败,请重试");
    }

    @DeleteMapping("/{id}")
    public Result deleteProductFromCart(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Integer rows = cartService.deleteCartById(id, user.getId());
        return rows != null && rows > 0 ? Result.ok() : Result.fail("删除购物车失败,请重试");
    }

    @GetMapping
    public Result getProductFromCart(@AuthenticationPrincipal User user) {
        List<Cart> cart = cartService.selectCartByUserId(user.getId());
        return Result.okList(cart, cart.size());
    }
}
