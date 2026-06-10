package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Category;
import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public Result GetAllCategoryName(){
        Integer count = categoryService.countCategories();
        return  Result.okList(categoryService.selectAllCategories(),count);
    }

}
