package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Review;
import edu.cafuc.crossmall.pojo.vo.ReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    Integer insertReview(Review review);

    Review selectReviewByOrderIdAndProductId(@Param("orderId") Long orderId,
                                             @Param("productId") Long productId);

    List<ReviewVO> selectReviewsByProductId(@Param("productId") Long productId);

    List<ReviewVO> selectReviewsByOrderId(@Param("orderId") Long orderId);
}
