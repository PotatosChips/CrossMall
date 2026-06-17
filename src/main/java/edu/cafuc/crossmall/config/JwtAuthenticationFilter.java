package edu.cafuc.crossmall.config;

import edu.cafuc.crossmall.mapper.UserMapper;
import edu.cafuc.crossmall.pojo.User;
import edu.cafuc.crossmall.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.validate(token)) {
                Long userId = jwtTokenProvider.getUserId(token);
                User user = userMapper.selectById(userId);
                if (user != null && (user.getStatus() == null || user.getStatus() == 1)) {
                    user.setPassword(null); // 不要把密码放进 SecurityContext
                    SecurityAuthSupport.login(user);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}