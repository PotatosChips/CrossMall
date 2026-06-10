package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Category;

import java.util.List;

public interface CategoryService {

    /** 查询所有分类（按 sort 升序），用于首页/商品列表页的分类导航 */
    List<Category> selectAllCategories();

    /** 根据分类 id 查询分类名称 */
    String selectCategoryNameById(Long id);

    /** 根据分类名称查询分类 id */
    Long selectCategoryIdByName(String categoryName);

    /** 统计分类个数 */
    Integer countCategories();

    Integer addCategory(String categoryName, Integer sort);

    Integer updateCategory(Long id, String categoryName, Integer sort);

    Integer deleteCategory(Long id);
}
