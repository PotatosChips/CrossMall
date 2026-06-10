package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.pojo.Result;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.pojo.vo.AdminUserVO;
import edu.cafuc.crossmall.service.CategoryService;
import edu.cafuc.crossmall.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Result addCategory(String categoryName, Integer sort, HttpSession session) {
        Result auth = adminAuth(session);
        if (auth != null) {
            return auth;
        }
        try {
            Integer rows = categoryService.addCategory(categoryName, sort);
            return rows > 0 ? Result.ok() : Result.fail("新增失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/categories/{id}")
    public Result updateCategory(@PathVariable Long id, String categoryName, Integer sort, HttpSession session) {
        Result auth = adminAuth(session);
        if (auth != null) {
            return auth;
        }
        try {
            Integer rows = categoryService.updateCategory(id, categoryName, sort);
            return rows > 0 ? Result.ok() : Result.fail("更新失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/categories/{id}")
    public Result deleteCategory(@PathVariable Long id, HttpSession session) {
        Result auth = adminAuth(session);
        if (auth != null) {
            return auth;
        }
        try {
            Integer rows = categoryService.deleteCategory(id);
            return rows > 0 ? Result.ok() : Result.fail("删除失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/users")
    public Result listUsers(HttpSession session) {
        Result auth = adminAuth(session);
        if (auth != null) {
            return auth;
        }
        List<AdminUserVO> list = userService.selectAllUsersForAdmin();
        return Result.okList(list, list.size());
    }

    @PutMapping("/users/{id}/status")
    public Result updateUserStatus(@PathVariable Long id, Integer status, HttpSession session) {
        Result auth = adminAuth(session);
        if (auth != null) {
            return auth;
        }
        User operator = (User) session.getAttribute("user");
        try {
            Integer rows = userService.updateUserStatus(id, status, operator.getId());
            return rows > 0 ? Result.ok() : Result.fail("更新失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    private Result adminAuth(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.fail("请先登录");
        }
        if (user.getRole() == null || user.getRole() != 2) {
            return Result.fail("仅管理员可操作");
        }
        return null;
    }
}
