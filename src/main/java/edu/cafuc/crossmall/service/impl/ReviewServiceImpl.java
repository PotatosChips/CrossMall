package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.exception.BusinessException;
import edu.cafuc.crossmall.mapper.OrderItemMapper;
import edu.cafuc.crossmall.mapper.OrderMapper;
import edu.cafuc.crossmall.mapper.ReviewMapper;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.pojo.OrderItem;
import edu.cafuc.crossmall.pojo.Review;
import edu.cafuc.crossmall.pojo.vo.ReviewVO;
import edu.cafuc.crossmall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Integer addReview(Long userId, String orderNo, Long productId, Integer score, String content) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new BusinessException("订单号不能为空");
        }
        if (productId == null) {
            throw new BusinessException("商品不能为空");
        }
        if (score == null || score < 1 || score > 5) {
            throw new BusinessException("评分须为 1-5 星");
        }

        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 3) {
            throw new BusinessException("仅已完成订单可评价");
        }

        boolean productInOrder = orderItemMapper.selectOrderItemByOrderId(order.getId()).stream()
                .anyMatch(item -> productId.equals(item.getProductId()));
        if (!productInOrder) {
            throw new BusinessException("该商品不在此订单中");
        }

        if (reviewMapper.selectReviewByOrderIdAndProductId(order.getId(), productId) != null) {
            throw new BusinessException("该商品已评价");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setOrderId(order.getId());
        review.setScore(score);
        review.setContent(content);
        return reviewMapper.insertReview(review);
    }

    @Override
    public List<ReviewVO> selectReviewsByProductId(Long productId) {
        return reviewMapper.selectReviewsByProductId(productId);
    }

    @Override
    public List<ReviewVO> selectReviewsByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return reviewMapper.selectReviewsByOrderId(order.getId());
    }
}
