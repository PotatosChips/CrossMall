package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /** 查询所有分类（按 sort 升序），用于首页/商品列表页的分类导航 */
    List<Category> selectAllCategories();

    /** 根据分类 id 查询分类名称 */
    String selectCategoryNameById(Long id);

    /** 根据分类名称查询分类 id */
    Long selectCategoryIdByName(String categoryName);

    /** 统计分类个数 */
    Integer countCategories();

    Category selectById(Long id);

    Integer countByCategoryName(@Param("categoryName") String categoryName,
                                @Param("excludeId") Long excludeId);

    Integer insertCategory(Category category);

    Integer updateCategory(Category category);

    Integer deleteById(Long id);
}
