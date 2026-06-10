package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Category;
import edu.cafuc.crossmall.pojo.Product;
import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import edu.cafuc.crossmall.service.CategoryService;
import edu.cafuc.crossmall.service.ProductService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    //商品列表
    @GetMapping
    public Result GetAllProduct(String categoryName, Long merchantId, String region, String keyword, String sort, Integer page, Integer pageSize) {
        Integer count = productService.countProducts(categoryName, merchantId, region, keyword);
        return Result.okList(productService.selectProductList(categoryName, merchantId, region, keyword, sort, page, pageSize), count);
    }
    @GetMapping("/{productId}")
    public Result selectProductById(@PathVariable Long productId ){
        if(productId != null) {
            return Result.okData(productService.selectProductById(productId));
        }
        return Result.fail("未找到商品详情");
    }
    @PutMapping
    public Result updateProduct(Long id,Integer quantity){
        productService.deductStock(id, quantity);
        return Result.ok();
    }
}
