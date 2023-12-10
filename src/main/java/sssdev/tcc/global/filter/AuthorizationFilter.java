package sssdev.tcc.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.util.StatusUtil;

@Slf4j(topic = "검증 및 인가")
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final StatusUtil statusUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        LoginUser loginUser = statusUtil.getLoginUser(request);
        filterChain.doFilter(request, response);
    }


}
