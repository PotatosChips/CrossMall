package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.ProductMapper;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import edu.cafuc.crossmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductVO> selectProductList(Long categoryId, String region, String keyword, Integer page, Integer pageSize) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePage - 1) * safePageSize;
        return productMapper.selectProductList(categoryId, region, keyword, offset, safePageSize);
    }

    @Override
    public Integer countProducts(Long categoryId, String region, String keyword) {
        return productMapper.countProducts(categoryId, region, keyword);
    }

    @Override
    public ProductVO selectProductById(Long id) {
        return productMapper.selectProductById(id);
    }

    @Override
    public Integer selectStockById(Long id) {
        return productMapper.selectStockById(id);
    }

    @Override
    public Integer deductStock(Long id, Integer quantity) {
        Integer currentStock = productMapper.selectStockById(id);
        if (currentStock == null || quantity == null || quantity <= 0 || currentStock < quantity) {
            return 0;
        }
        return productMapper.updateStock(id, currentStock - quantity);
    }
}
