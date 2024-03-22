package br.com.devduo.viverbemapi.filter;

import br.com.devduo.viverbemapi.service.v1.security.JwtTokenService;
import br.com.devduo.viverbemapi.service.v1.security.UserService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        token = jwtTokenService.formatToken(token);

        if (token != null && jwtTokenService.validateToken(token)){
            DecodedJWT decodedJWT = jwtTokenService.decodedJWT(token);
            UserDetails user = userService.loadUserByUsername(decodedJWT.getSubject());
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
            if (authentication != null)
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
