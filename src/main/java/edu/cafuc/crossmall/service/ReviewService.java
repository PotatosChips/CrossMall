package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Review;
import edu.cafuc.crossmall.pojo.vo.ReviewVO;

import java.util.List;

public interface ReviewService {
    Integer addReview(Long userId, String orderNo, Long productId, Integer score, String content);

    List<ReviewVO> selectReviewsByProductId(Long productId);

    List<ReviewVO> selectReviewsByOrderNo(String orderNo, Long userId);
}
