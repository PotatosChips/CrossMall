package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.CategoryMapper;
import edu.cafuc.crossmall.mapper.ProductMapper;
import edu.cafuc.crossmall.pojo.Category;
import edu.cafuc.crossmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Category> selectAllCategories() {
        return categoryMapper.selectAllCategories();
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
        return categoryMapper.insertCategory(category);
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
        return categoryMapper.updateCategory(category);
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
        return categoryMapper.deleteById(id);
    }
}
