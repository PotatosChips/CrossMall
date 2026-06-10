package edu.cafuc.crossmall.controller;

import edu.cafuc.crossmall.config.SecurityAuthSupport;
import edu.cafuc.crossmall.pojo.Merchant;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.service.MerchantService;
import edu.cafuc.crossmall.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController


@RequestMapping("/api")
public class ApiloginController {

    @Autowired
    UserService userService;
    @Autowired
    MerchantService merchantService;

    @PostMapping("/userLogin")
    public ResponseEntity<Map<String, Object>> userLogin(String username, String password, HttpSession session) {
        User login = userService.Userlogin(username, password);
        if (login == null) {
            return unauthorized("用户名与密码不匹配");
        }
        if (login.getStatus() != null && login.getStatus() == 0) {
            return unauthorized("账号已被禁用");
        }
        // 登录成功：Session + Spring Security 同步
        session.setAttribute("user", login);
        SecurityAuthSupport.login(login);
        //给前端信息
        Map<String, Object> m = singleSuccess();
        m.put("username", login.getUsername());
        m.put("nickname", login.getNickname());
        m.put("role", login.getRole());
        return ResponseEntity.ok(m);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<Map<String, Object>> userInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return unauthorized("请先登录");
        }
        Map<String, Object> m = singleSuccess();
        m.put("username", user.getUsername());
        m.put("nickname", user.getNickname());
        m.put("role", user.getRole());
        return ResponseEntity.ok(m);
    }

    @PostMapping("/userLogout")
    public ResponseEntity<Map<String, Object>> userLogout(HttpSession session) {
        SecurityAuthSupport.logout();
        session.invalidate();
        return ResponseEntity.ok(singleSuccess());
    }
    @PostMapping("/userRegister")
    public ResponseEntity<Map<String, Object>> userRegister(User user,Merchant merchant) {
        String errorMessage = validateUserAndMerchant(user, merchant);
        if (errorMessage != null) {
            return badRequest(errorMessage);
        }
        user.setStatus(1);
        Integer rows = userService.insertUser(user);
        if (rows == 0) {
            return badRequest("注册失败");
        }
        if (user.getRole() == 1) {
            merchant.setUser_id(user.getId());
            if (merchantService.insertMerchant(merchant) == 0) {
                return badRequest("注册商户失败");
            }
        }
        Map<String, Object> m = singleSuccess();
        m.put("username", user.getUsername());
        if (user.getRole() == 1) {
            m.put("merchantName", merchant.getMerchantName());
        }
        return ResponseEntity.ok(m);
    }

    public static Map<String,Object>singleSuccess(){
        Map<String,Object> m =new HashMap<>(2);
        m.put("success",true);
        return m;
    }
    public static ResponseEntity<Map<String,Object>> unauthorized(String massage){
        Map<String,Object> m = new HashMap<>(4);
        m.put("success",false);
        m.put("massage",massage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
    }
    public static ResponseEntity<Map<String,Object>> badRequest(String massage){
        Map<String,Object> m = new HashMap<>(4);
        m.put("success",false);
        m.put("massage",massage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(m);
    }
    private String validateUserAndMerchant(User user,Merchant merchant){
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return ("用户名不能为空");
        }
        if (user.getUsername().length() > 10) {
            return ("用户名不能超过10个字符");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ("密码不能为空");
        }
        if (user.getPassword().length() < 6 || user.getPassword().length() > 20) {
            return ("密码长度需在6~20个字符之间");
        }
        if (user.getNickname() != null && !user.getNickname().isBlank() && user.getNickname().length() > 15) {
            return ("昵称不能超过15个字符");
        }
        if (userService.selectUserByUsername(user.getUsername()) > 0) {
            return ("用户名已存在");
        }
        if (user.getRole() == 1) {
            if (merchant.getMerchantName() == null || merchant.getMerchantName().isBlank()) {
                return ("商户名称不能为空");
            }
            if (merchant.getMerchantName().length() > 15) {
                return ("商户名称不能超过15个字符");
            }
            if (merchant.getRegion() == null || merchant.getRegion().isBlank()) {
                return ("商户地区不能为空");
            }
            if (merchant.getDescription() != null && !merchant.getDescription().isBlank()
                    && merchant.getDescription().length() > 500) {
                return ("商户描述不能超过500个字符");
            }
        }
        return null;
    }
}
