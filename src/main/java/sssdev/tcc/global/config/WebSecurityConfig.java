package sssdev.tcc.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.global.filter.AuthorizationFilter;
import sssdev.tcc.global.filter.ExceptionHandleFilter;
import sssdev.tcc.global.util.StatusUtil;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final StatusUtil statusUtil;
    private final ObjectMapper objectMapper;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthorizationFilter AuthorizationFilter() {
        return new AuthorizationFilter(statusUtil, objectMapper);
    }

    @Bean
    public ExceptionHandleFilter ExceptionHandleFilter() {
        return new ExceptionHandleFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());
        http.headers(header -> header.frameOptions(options -> options.sameOrigin()));

        http.authorizeHttpRequests(
            (authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(HttpMethod.GET)
                .permitAll()
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.getAuthority())
                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 필터 관리
        http.addFilterBefore(AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(ExceptionHandleFilter(), AuthorizationFilter.class);
        //접근 불가 페이지

        return http.build();
    }
}
