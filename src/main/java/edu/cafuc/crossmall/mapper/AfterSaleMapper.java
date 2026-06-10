package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.AfterSale;
import edu.cafuc.crossmall.pojo.vo.AfterSaleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AfterSaleMapper {

    Integer insertAfterSale(AfterSale afterSale);

    AfterSale selectById(Long id);

    AfterSale selectActiveByOrderId(Long orderId);

    List<AfterSaleVO> selectByUserId(Long userId);

    List<AfterSaleVO> selectByOrderId(Long orderId);

    List<AfterSaleVO> selectListForSeller(Long sellerUserId);

    Integer updateHandle(@Param("id") Long id,
                         @Param("status") Integer status,
                         @Param("reply") String reply,
                         @Param("expectedStatus") Integer expectedStatus);
}
