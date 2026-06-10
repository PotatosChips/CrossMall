package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AfterSaleVO;
import edu.cafuc.crossmall.service.AfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/after-sales")
public class AfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result apply(String orderNo, Integer type, String reason, @AuthenticationPrincipal User user) {
        Integer rows = afterSaleService.apply(user.getId(), orderNo, type, reason);
        return rows > 0 ? Result.ok() : Result.fail("申请失败");
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public Result listForSeller(@AuthenticationPrincipal User user) {
        List<AfterSaleVO> list = afterSaleService.listForSeller(user.getId());
        return Result.okList(list, list.size());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Result list(String orderNo, @AuthenticationPrincipal User user) {
        List<AfterSaleVO> list;
        if (orderNo != null && !orderNo.isBlank()) {
            list = afterSaleService.listByOrderNo(orderNo, user.getId());
        } else {
            list = afterSaleService.listByUser(user.getId());
        }
        return Result.okList(list, list.size());
    }

    @PutMapping("/{id}/handle")
    @PreAuthorize("hasRole('SELLER')")
    public Result handle(@PathVariable Long id, Integer status, String reply, String company,
                         @AuthenticationPrincipal User user) {
        Integer rows = afterSaleService.handle(id, user.getId(), status, reply, company);
        return rows > 0 ? Result.ok() : Result.fail("处理失败");
    }
}
