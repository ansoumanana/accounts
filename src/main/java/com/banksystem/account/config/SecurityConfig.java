package com.banksystem.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloackRoleConverter());

        http.authorizeRequests(authorize -> authorize.antMatchers("/api/v1/accounts/sayHello").authenticated()
                        .anyRequest().authenticated())

                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter);
        http.csrf().disable();
        return http.build();
    }
}