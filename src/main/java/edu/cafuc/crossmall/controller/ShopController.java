package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.vo.MerchantVO;
import edu.cafuc.crossmall.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public Result listShops(String region, String keyword, Integer page, Integer pageSize) {
        Integer count = merchantService.countShops(region, keyword);
        return Result.okList(merchantService.selectShopList(region, keyword, page, pageSize), count);
    }

    @GetMapping("/{shopId}")
    public Result getShop(@PathVariable Long shopId) {
        MerchantVO shop = merchantService.selectShopById(shopId);
        return shop != null ? Result.okData(shop) : Result.fail("店铺不存在");
    }
}
