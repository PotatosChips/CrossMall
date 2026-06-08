package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.CategoryMapper;
import edu.cafuc.crossmall.pojo.Category;
import edu.cafuc.crossmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

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
}
