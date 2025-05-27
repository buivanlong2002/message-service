package com.example.message_service.infrastructure;


import com.example.message_service.components.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final HttpServletResponse httpServletResponse;
    @Value("${api.prefix}")
    private String apiPrefix;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            final String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                }
                return;
            }

            final String token = authorizationHeader.substring(7);
            final String email = jwtTokenUtil.extractUsername(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                e.printStackTrace();
            } else {
                logger.error("Response was already committed. Cannot send error.", e);
            }
        }
    }
    // Kiểm tra URL có được bỏ qua xác thực hay không
    private boolean isBypassToken(@NotNull HttpServletRequest request) {


        // Danh sách các API không yêu cầu xác thực
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/auth/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/otp/send", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/otp/verify", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/login-google", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/reset-password", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/decode", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/login", apiPrefix), "POST")
                // để tạm chưa làm yêu cầu xác thực
//                Pair.of(String.format("%s/users", apiPrefix), "GET"),
//                Pair.of(String.format("%s/users/", apiPrefix), "GET"),
//                Pair.of(String.format("%s/users/", apiPrefix), "DELETE"),
//                Pair.of(String.format("%s/users/", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/add", apiPrefix), "POST")

        );
        // Duyệt danh sách và kiểm tra đường dẫn và phương thức HTTP
        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equalsIgnoreCase(bypassToken.getSecond())) {
                return true; // Bypass xác thực
            }
        }
        return false; // Không bypass, yêu cầu xác thực
    }
}
