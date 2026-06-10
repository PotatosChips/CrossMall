package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public Result getAllRegions() {
        List<String> regions = merchantService.selectAllRegions();
        return Result.okList(regions, regions.size());
    }
}
