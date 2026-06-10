package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.CategoryMapper;
import edu.cafuc.crossmall.mapper.ProductMapper;
import edu.cafuc.crossmall.pojo.Category;
import edu.cafuc.crossmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY_CACHE_KEY = "mall:categories";
    private static final long CACHE_TTL_MINUTES = 10;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Category> selectAllCategories() {
        try {
            String cached = redisTemplate.opsForValue().get(CATEGORY_CACHE_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<Category>>() {});
            }
        } catch (Exception ignored) {
            // Redis 挂了就走数据库，不影响业务
        }

        List<Category> list = categoryMapper.selectAllCategories();

        try {
            redisTemplate.opsForValue().set(
                    CATEGORY_CACHE_KEY,
                    objectMapper.writeValueAsString(list),
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        } catch (Exception ignored) {
        }
        return list;
    }

    private void evictCategoryCache() {
        redisTemplate.delete(CATEGORY_CACHE_KEY);
    }

    @Override
    public String selectCategoryNameById(Long id) {
        return categoryMapper.selectCategoryNameById(id);
    }

    @Override
    public Long selectCategoryIdByName(String categoryName) {
        return categoryMapper.selectCategoryIdByName(categoryName);
    }

    @Override
    public Integer countCategories() {
        return categoryMapper.countCategories();
    }

    @Override
    public Integer addCategory(String categoryName, Integer sort) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new RuntimeException("分类名称不能为空");
        }
        String name = categoryName.trim();
        if (categoryMapper.countByCategoryName(name, null) > 0) {
            throw new RuntimeException("分类名称已存在");
        }
        Category category = new Category();
        category.setCategoryName(name);
        category.setSort(sort != null ? sort : 0);
        Integer rows = categoryMapper.insertCategory(category);
        if (rows != null && rows > 0) {
            evictCategoryCache();
        }
        return rows;
    }

    @Override
    public Integer updateCategory(Long id, String categoryName, Integer sort) {
        if (id == null) {
            throw new RuntimeException("分类不存在");
        }
        if (categoryMapper.selectById(id) == null) {
            throw new RuntimeException("分类不存在");
        }
        if (categoryName == null || categoryName.isBlank()) {
            throw new RuntimeException("分类名称不能为空");
        }
        String name = categoryName.trim();
        if (categoryMapper.countByCategoryName(name, id) > 0) {
            throw new RuntimeException("分类名称已存在");
        }
        Category category = new Category();
        category.setId(id);
        category.setCategoryName(name);
        category.setSort(sort != null ? sort : 0);
        Integer rows = categoryMapper.updateCategory(category);
        if (rows != null && rows > 0) {
            evictCategoryCache();
        }
        return rows;
    }

    @Override
    public Integer deleteCategory(Long id) {
        if (id == null) {
            throw new RuntimeException("分类不存在");
        }
        if (categoryMapper.selectById(id) == null) {
            throw new RuntimeException("分类不存在");
        }
        Integer productCount = productMapper.countByCategoryId(id);
        if (productCount != null && productCount > 0) {
            throw new RuntimeException("该分类下仍有商品，无法删除");
        }
        Integer rows = categoryMapper.deleteById(id);
        if (rows != null && rows > 0) {
            evictCategoryCache();
        }
        return rows;
    }
}
