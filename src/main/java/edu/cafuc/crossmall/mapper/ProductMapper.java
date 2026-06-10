package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Product;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    /** 分页查询上架商品列表，支持按分类、商家地区、关键词筛选（含分类名、商家名、地区） */
    List<ProductVO> selectProductList(@Param("categoryId") Long categoryId,
                                      @Param("merchantId") Long merchantId,
                                      @Param("region") String region,
                                      @Param("keyword") String keyword,
                                      @Param("sort") String sort,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);

    /** 统计符合条件的上架商品总数，用于分页 */
    Integer countProducts(@Param("categoryId") Long categoryId,
                          @Param("merchantId") Long merchantId,
                          @Param("region") String region,
                          @Param("keyword") String keyword);

    /** 根据 id 查询商品详情（含分类名、商家名、地区，仅上架商品） */
    ProductVO selectProductById(Long id);

    /** 添加货物 卖家*/
    Integer insertProduct(Product product);

    /** 根据 id 查询库存，用于加购/下单前校验 */
    Integer selectStockById(Long id);

    /** 扣减库存（下单时使用，stock 为扣减后的剩余库存） */
    Integer updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    /** 卖家：分页查询本店商品（含下架） */
    List<ProductVO> selectProductListByMerchantId(@Param("merchantId") Long merchantId,
                                                  @Param("keyword") String keyword,
                                                  @Param("offset") Integer offset,
                                                  @Param("pageSize") Integer pageSize);

    /** 卖家：统计本店商品总数 */
    Integer countProductsByMerchantId(@Param("merchantId") Long merchantId,
                                      @Param("keyword") String keyword);

    /** 卖家：查询本店商品详情（校验 merchant_id，含下架） */
    ProductVO selectProductByIdForSeller(@Param("id") Long id,
                                         @Param("merchantId") Long merchantId);

    /** 卖家：更新本店商品 */
    Integer updateProductForSeller(Product product);

    /** 统计某分类下商品数量（管理员删分类前校验） */
    Integer countByCategoryId(Long categoryId);
}
