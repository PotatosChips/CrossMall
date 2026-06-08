package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    /** 分页查询上架商品列表，支持按分类、商家地区、关键词筛选（含分类名、商家名、地区） */
    List<ProductVO> selectProductList(@Param("categoryId") Long categoryId,
                                      @Param("region") String region,
                                      @Param("keyword") String keyword,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);

    /** 统计符合条件的上架商品总数，用于分页 */
    Integer countProducts(@Param("categoryId") Long categoryId,
                          @Param("region") String region,
                          @Param("keyword") String keyword);

    /** 根据 id 查询商品详情（含分类名、商家名、地区，仅上架商品） */
    ProductVO selectProductById(Long id);

    /** 根据 id 查询库存，用于加购/下单前校验 */
    Integer selectStockById(Long id);

    /** 扣减库存（下单时使用，stock 为扣减后的剩余库存） */
    Integer updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}
