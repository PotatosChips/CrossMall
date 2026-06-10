package edu.cafuc.crossmall.config;

import edu.cafuc.crossmall.pojo.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public final class SecurityAuthSupport {

    private SecurityAuthSupport() {
    }

    public static List<GrantedAuthority> authoritiesFor(User user) {
        Integer role = user.getRole();
        if (role != null && role == 1) {
            return List.of(new SimpleGrantedAuthority("ROLE_SELLER"));
        }
        if (role != null && role == 2) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_BUYER"));
    }

    public static void login(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, authoritiesFor(user));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void logout() {
        SecurityContextHolder.clearContext();
    }
}
