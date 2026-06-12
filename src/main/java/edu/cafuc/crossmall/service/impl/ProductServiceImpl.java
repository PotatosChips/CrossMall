package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.exception.BusinessException;
import edu.cafuc.crossmall.mapper.CategoryMapper;
import edu.cafuc.crossmall.mapper.MerchantMapper;
import edu.cafuc.crossmall.mapper.ProductMapper;
import edu.cafuc.crossmall.pojo.Product;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import edu.cafuc.crossmall.service.ProductService;
import edu.cafuc.crossmall.service.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private RedisLockService redisLockService;

    @Override
    public List<ProductVO> selectProductList(String categoryName, Long merchantId, String region, String keyword, String sort, Integer page, Integer pageSize) {
        Long categoryId = resolveCategoryId(categoryName);
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePage - 1) * safePageSize;
        return productMapper.selectProductList(categoryId, merchantId, region, keyword, sort, offset, safePageSize);
    }

    @Override
    public Integer countProducts(String categoryName, Long merchantId, String region, String keyword) {
        Long categoryId = resolveCategoryId(categoryName);
        return productMapper.countProducts(categoryId, merchantId, region, keyword);
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
    public Integer insertProduct(String productName, String categoryName, Long userId, BigDecimal price, String description, Integer stock) {
        validateProductFields(productName, categoryName, price, stock);
        Long categoryId = requireCategoryId(categoryName);
        Long merchantId = merchantMapper.selectMerchantIdByUserId(userId);
        if (merchantId == null) {
            throw new BusinessException("未找到店铺信息");
        }
        Product product = new Product();
        product.setProductName(productName.trim());
        product.setCategoryId(categoryId);
        product.setMerchantId(merchantId);
        product.setPrice(price);
        product.setDescription(description);
        product.setStock(stock);
        product.setImage("https://picsum.photos/seed/400/400");
        product.setStatus(1);
        return productMapper.insertProduct(product);
    }

    @Override
    public List<ProductVO> selectMyProductList(Long userId, String keyword, Integer page, Integer pageSize) {
        Long merchantId = requireMerchantId(userId);
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePage - 1) * safePageSize;
        return productMapper.selectProductListByMerchantId(merchantId, keyword, offset, safePageSize);
    }

    @Override
    public Integer countMyProducts(Long userId, String keyword) {
        Long merchantId = requireMerchantId(userId);
        return productMapper.countProductsByMerchantId(merchantId, keyword);
    }

    @Override
    public ProductVO selectMyProductById(Long userId, Long productId) {
        Long merchantId = requireMerchantId(userId);
        return productMapper.selectProductByIdForSeller(productId, merchantId);
    }

    @Override
    public Integer updateProduct(Long productId, String productName, String categoryName,
                                 BigDecimal price, String description, Integer stock, Integer status, Long userId) {
        validateProductFields(productName, categoryName, price, stock);
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("商品状态无效");
        }
        Long categoryId = requireCategoryId(categoryName);
        Long merchantId = requireMerchantId(userId);
        Product product = new Product();
        product.setId(productId);
        product.setProductName(productName.trim());
        product.setCategoryId(categoryId);
        product.setMerchantId(merchantId);
        product.setPrice(price);
        product.setDescription(description);
        product.setStock(stock);
        product.setStatus(status);
        return productMapper.updateProductForSeller(product);
    }

    @Override
    public Integer deductStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return 0;
        }
        return redisLockService.executeWithProductLock(id, () ->
                productMapper.deductStock(id, quantity)
        );
    }
    @Override
    public Integer addStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return 0;
        }
        return productMapper.addStock(id, quantity);
    }

    private Long resolveCategoryId(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return categoryMapper.selectCategoryIdByName(categoryName);
    }

    private Long requireCategoryId(String categoryName) {
        Long categoryId = categoryMapper.selectCategoryIdByName(categoryName);
        if (categoryId == null) {
            throw new BusinessException("分类不存在");
        }
        return categoryId;
    }

    private Long requireMerchantId(Long userId) {
        Long merchantId = merchantMapper.selectMerchantIdByUserId(userId);
        if (merchantId == null) {
            throw new BusinessException("未找到店铺信息");
        }
        return merchantId;
    }

    private void validateProductFields(String productName, String categoryName, BigDecimal price, Integer stock) {
        if (productName == null || productName.isBlank()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (categoryName == null || categoryName.isBlank()) {
            throw new BusinessException("请选择分类");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("价格必须大于 0");
        }
        if (stock == null || stock < 0) {
            throw new BusinessException("库存不能为负数");
        }
    }
}
