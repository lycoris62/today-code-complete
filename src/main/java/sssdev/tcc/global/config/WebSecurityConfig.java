package sssdev.tcc.global.config;

import static sssdev.tcc.domain.user.domain.UserRole.ADMIN;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

        http.authorizeHttpRequests((authorizeHttpRequests) ->
            authorizeHttpRequests
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers("/api/users/login").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                .requestMatchers("/api/admin/**").hasAuthority(ADMIN.getAuthority())
                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );
        // 필터 관리
        http.addFilterBefore(AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(ExceptionHandleFilter(), AuthorizationFilter.class);
        //접근 불가 페이지

        return http.build();
    }
}