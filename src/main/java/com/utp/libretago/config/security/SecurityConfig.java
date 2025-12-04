package com.utp.libretago.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import com.vaadin.hilla.route.RouteUtil;

@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, RouteUtil routeUtil) throws Exception {
        // Set default security policy that permits Hilla internal requests and denies all other
        http.authorizeHttpRequests(registry -> {
            registry.requestMatchers(routeUtil::isRouteAllowed).permitAll();
            registry.requestMatchers("/descargar/**").authenticated();
            registry.requestMatchers("/images/**").permitAll();
        });

        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            // use a custom login view and redirect to root on logout
            configurer.loginView("/login", "/");
        });
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
