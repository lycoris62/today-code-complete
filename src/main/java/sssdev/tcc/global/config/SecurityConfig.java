package sssdev.tcc.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain setting(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
            c -> {
                c.requestMatchers("/actuator/**").permitAll();
            }
        );

        return http.build();
    }
}
