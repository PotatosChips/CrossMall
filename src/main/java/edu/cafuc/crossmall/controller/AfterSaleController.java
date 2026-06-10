package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AfterSaleVO;
import edu.cafuc.crossmall.service.AfterSaleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/after-sales")
public class AfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    //申请售后
    @PostMapping
    public Result apply(String orderNo, Integer type, String reason, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        try {
            Integer rows = afterSaleService.apply(user.getId(), orderNo, type, reason);
            return rows > 0 ? Result.ok() : Result.fail("申请失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    //卖家售后列表
    @GetMapping("/seller")
    public Result listForSeller(HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        List<AfterSaleVO> list = afterSaleService.listForSeller(user.getId());
        return Result.okList(list, list.size());
    }

    //订单的 售后列表
    @GetMapping
    public Result list(String orderNo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        try {
            List<AfterSaleVO> list;
            if (orderNo != null && !orderNo.isBlank()) {
                list = afterSaleService.listByOrderNo(orderNo, user.getId());
            } else {
                list = afterSaleService.listByUser(user.getId());
            }
            return Result.okList(list, list.size());
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    //售后处理
    @PutMapping("/{id}/handle")
    public Result handle(@PathVariable Long id, Integer status, String reply, String company,
                         HttpSession session) {
        Result auth = sellerAuth(session);
        if (auth != null) {
            return auth;
        }
        User user = (User) session.getAttribute("user");
        try {
            Integer rows = afterSaleService.handle(id, user.getId(), status, reply, company);
            return rows > 0 ? Result.ok() : Result.fail("处理失败");
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
