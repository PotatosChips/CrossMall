package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.ReviewVO;
import edu.cafuc.crossmall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result addReview(String orderNo, Long productId, Integer score, String content,
                            @AuthenticationPrincipal User user) {
        Integer rows = reviewService.addReview(user.getId(), orderNo, productId, score, content);
        return rows > 0 ? Result.ok() : Result.fail("评价失败");
    }

    @GetMapping
    public Result listReviews(Long productId, String orderNo, @AuthenticationPrincipal User user) {
        if (productId != null) {
            List<ReviewVO> list = reviewService.selectReviewsByProductId(productId);
            return Result.okList(list, list.size());
        }
        if (orderNo != null && !orderNo.isBlank()) {
            if (user == null) {
                return Result.fail("请先登录");
            }
            List<ReviewVO> list = reviewService.selectReviewsByOrderNo(orderNo, user.getId());
            return Result.okList(list, list.size());
        }
        return Result.fail("请指定 productId 或 orderNo");
    }
}
