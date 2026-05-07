package com.asdf.minilog.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
// OncePerRequestFilter은 요청 한 번에 한 번만 실행되도록 보장하는 클래스
// JWT 필터는 매번 모든 요청에 대해 동작하므로 에러나 포워드 상황에서 중복 실행 가능성이 존재하기에
// 이를 방지하기 위해 OncePerRequestFilter가 사용됨
public class JwtRequestFilter extends OncePerRequestFilter {
    // private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final UserDetailsService jwtUserDetailService;
    private final JwtUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(UserDetailsService jwtUserDetailService, JwtUtil jwtTokenUtil) {
        this.jwtUserDetailService = jwtUserDetailService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // JWT 인증 표준 방식은 Bearer { JWT } 형태
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwt = requestTokenHeader.substring(7);
            try {
                // JWT에서 username 추출
                username = jwtTokenUtil.getUsernameFromToken(jwt);
            } catch (IllegalArgumentException e) { // 잘못된 인수
                logger.error("Unable to get JWT", e);
            } catch (ExpiredJwtException e) { // 엑세스 토큰 만료 예외
                logger.warn("JWT has expired", e);
            }
        } else {
            logger.warn("JWT does not begin with Bearer String");
        }

        // 이미 로그인 된 사용자인지 검사(중복 인증 방지)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 실제 존재하는 사용자 인지 이중 검증
            UserDetails userDetails = this.jwtUserDetailService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                // 인증 객체 생성
                // JWT 인증 완료 됐으니 비밀번호(credentials) 필요없음.
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // 요청 상세 정보 저장 (IP 주소, 세션 ID 등)
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 저장
                SecurityContextHolder.getContext()
                        .setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.warn("JWT is not valid");
            }
        }
        // 응답을 다음 필터로 전달 (책임 연쇄 패턴 : Chain of Responsibility Pattern)
        filterChain.doFilter(request, response);
    }
}
