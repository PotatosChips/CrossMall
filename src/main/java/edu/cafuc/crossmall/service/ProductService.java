package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.vo.ProductVO;

import java.util.List;

public interface ProductService {

    /** 分页查询上架商品列表，支持按分类、商家地区、关键词筛选 */
    List<ProductVO> selectProductList(Long categoryId, String region, String keyword, Integer page, Integer pageSize);

    /** 统计符合条件的上架商品总数，用于分页 */
    Integer countProducts(Long categoryId, String region, String keyword);

    /** 根据 id 查询商品详情（含分类名、商家名、地区，仅上架商品） */
    ProductVO selectProductById(Long id);

    /** 根据 id 查询库存，用于加购/下单前校验 */
    Integer selectStockById(Long id);

    /** 扣减库存（下单时使用，quantity 为要扣减的数量） */
    Integer deductStock(Long id, Integer quantity);
}
