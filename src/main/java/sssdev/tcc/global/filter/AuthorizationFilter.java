package sssdev.tcc.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.util.StatusUtil;

@Slf4j(topic = "검증 및 인가")
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private static final RequestMatcher userIgnoredPath = new AntPathRequestMatcher("/api/users/**",
        HttpMethod.GET.name());
    private static final RequestMatcher postIgnoredPath = new AntPathRequestMatcher(
        "/api/posts",
        HttpMethod.GET.name());
    private static final RequestMatcher postIdIgnoredPath = new AntPathRequestMatcher(
        "/api/posts/{id}",
        HttpMethod.GET.name());
    private static final RequestMatcher commentIgnoredPath = new AntPathRequestMatcher(
        "/api/comments", HttpMethod.GET.name());
    

    private final StatusUtil statusUtil;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        LoginUser loginUser = statusUtil.getLoginUser(request);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, List.of(
            UserRole.USER)));
        SecurityContextHolder.setContext(context);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return userIgnoredPath.matches(request) || postIgnoredPath.matches(request)
            || postIdIgnoredPath.matches(request) || commentIgnoredPath.matches(request);
    }
}
