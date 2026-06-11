package edu.cafuc.crossmall.config;

import edu.cafuc.crossmall.pojo.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 自定义登录只把 user 写入 HttpSession；Spring Security 的 authenticated() 依赖 SecurityContext。
 * 每个请求从 Session 恢复登录态，避免「导航已登录、业务接口 401」。
 */
public class SessionUserAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (needsSessionRestore(current)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    SecurityAuthSupport.login(user);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private static boolean needsSessionRestore(Authentication current) {
        if (current == null) {
            return true;
        }
        if (current instanceof AnonymousAuthenticationToken) {
            return true;
        }
        return !(current.getPrincipal() instanceof User);
    }
}
