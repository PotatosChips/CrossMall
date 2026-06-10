package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import edu.cafuc.crossmall.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {
    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public Result addProduct(String productName, String categoryName, BigDecimal price,
                            String description, Integer stock, HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        try {
            Integer rows = productService.insertProduct(productName, categoryName, user.getId(), price, description, stock);
            return rows > 0 ? Result.ok() : Result.fail("增加商品失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/products")
    public Result listMyProducts(String keyword, Integer page, Integer pageSize, HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        Integer count = productService.countMyProducts(user.getId(), keyword);
        List<ProductVO> list = productService.selectMyProductList(user.getId(), keyword, page, pageSize);
        return Result.okList(list, count);
    }

    @GetMapping("/products/{productId}")
    public Result getMyProduct(@PathVariable Long productId, HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        ProductVO product = productService.selectMyProductById(user.getId(), productId);
        return product != null ? Result.okData(product) : Result.fail("未找到商品详情");
    }

    @PutMapping("/products/{productId}")
    public Result updateProduct(@PathVariable Long productId, String productName, String categoryName,
                                BigDecimal price, String description, Integer stock, Integer status,
                                HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        try {
            Integer rows = productService.updateProduct(productId, productName, categoryName, price, description, stock, status, user.getId());
            return rows > 0 ? Result.ok() : Result.fail("更新商品详情失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    private Result sellerAuth(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        if (user.getRole() == null || user.getRole() != 1) {
            return Result.fail("仅卖家可操作");
        }
        return null;
    }
}
