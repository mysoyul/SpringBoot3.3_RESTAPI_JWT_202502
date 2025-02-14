package com.boot3.myrestapi.security.jwt.filter;

import com.boot3.myrestapi.security.jwt.service.JwtService;
import com.boot3.myrestapi.security.userinfo.UserInfoUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        //요청헤더 중에서 Authorization 헤더 값을 가져오기
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        //Authorization 헤더가 있다면 "Bearer eyJhbGciOiJIUzI1" 토큰 문자열 추출하기
        //토큰을 디코딩해서 subject에 포함된 username(email 주소) 추출하기
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        //SecurityContext에 저장된 Authentication 객체의 존재여부 체크
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Entity 에 저장된 인증정보를 UserDetails 로 리턴해 준다
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                //UsernamePasswordAuthenticationToken 생성 => Authentication
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //SecurityContext에 Authentication 를 저장해줍니다.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}