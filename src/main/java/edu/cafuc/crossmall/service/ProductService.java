package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Product;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /** 分页查询上架商品列表，支持按分类、商家、地区、关键词筛选 */
    List<ProductVO> selectProductList(String categoryName, Long merchantId, String region, String keyword, String sort, Integer page, Integer pageSize);

    /** 统计符合条件的上架商品总数，用于分页 */
    Integer countProducts(String categoryName, Long merchantId, String region, String keyword);

    /** 根据 id 查询商品详情（含分类名、商家名、地区，仅上架商品） */
    ProductVO selectProductById(Long id);

    /** 根据 id 查询库存，用于加购/下单前校验 */
    Integer selectStockById(Long id);

    /** 添加货物 卖家*/
    Integer insertProduct(String productName, String categoryName, Long userId, BigDecimal price, String description, Integer stock);

    /** 卖家：分页查询本店商品 */
    List<ProductVO> selectMyProductList(Long userId, String keyword, Integer page, Integer pageSize);

    /** 卖家：统计本店商品总数 */
    Integer countMyProducts(Long userId, String keyword);

    /** 卖家：查询本店商品详情 */
    ProductVO selectMyProductById(Long userId, Long productId);

    /** 卖家：更新本店商品 */
    Integer updateProduct(Long productId, String productName, String categoryName,
                          BigDecimal price, String description, Integer stock, Integer status, Long userId);

    /** 扣减库存（下单时使用，quantity 为要扣减的数量） */
    Integer deductStock(Long id, Integer quantity);

    /** 增加库存（删除订单，添加货物，quantity 为要扣减的数量） */
    Integer addStock(Long id, Integer quantity);
}
