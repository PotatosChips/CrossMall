package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.ReviewVO;
import edu.cafuc.crossmall.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Result addReview(String orderNo, Long productId, Integer score, String content, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        try {
            Integer rows = reviewService.addReview(user.getId(), orderNo, productId, score, content);
            return rows > 0 ? Result.ok() : Result.fail("评价失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping
    public Result listReviews(Long productId, String orderNo, HttpSession session) {
        if (productId != null) {
            List<ReviewVO> list = reviewService.selectReviewsByProductId(productId);
            return Result.okList(list, list.size());
        }
        if (orderNo != null && !orderNo.isBlank()) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.fail("请先登录");
            }
            try {
                List<ReviewVO> list = reviewService.selectReviewsByOrderNo(orderNo, user.getId());
                return Result.okList(list, list.size());
            } catch (RuntimeException e) {
                return Result.fail(e.getMessage());
            }
        }
        return Result.fail("请指定 productId 或 orderNo");
    }
}
