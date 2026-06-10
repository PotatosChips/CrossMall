package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AdminUserVO;
import edu.cafuc.crossmall.service.CategoryService;
import edu.cafuc.crossmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public Result addCategory(String categoryName, Integer sort) {
        Integer rows = categoryService.addCategory(categoryName, sort);
        return rows > 0 ? Result.ok() : Result.fail("新增失败");
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateCategory(@PathVariable Long id, String categoryName, Integer sort) {
        Integer rows = categoryService.updateCategory(id, categoryName, sort);
        return rows > 0 ? Result.ok() : Result.fail("更新失败");
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteCategory(@PathVariable Long id) {
        Integer rows = categoryService.deleteCategory(id);
        return rows > 0 ? Result.ok() : Result.fail("删除失败");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result listUsers() {
        List<AdminUserVO> list = userService.selectAllUsersForAdmin();
        return Result.okList(list, list.size());
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateUserStatus(@PathVariable Long id, Integer status, @AuthenticationPrincipal User operator) {
        Integer rows = userService.updateUserStatus(id, status, operator.getId());
        return rows > 0 ? Result.ok() : Result.fail("更新失败");
    }

}
