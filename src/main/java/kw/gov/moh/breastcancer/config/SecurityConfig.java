package kw.gov.moh.breastcancer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String SAHEL_SURVEY_ENDPOINT = "/api/v1/sahel";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SAHEL_SURVEY_ENDPOINT).permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(SAHEL_SURVEY_ENDPOINT))
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }
}
