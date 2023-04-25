package org.mini_amazon.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;

import jakarta.annotation.Resource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Resource
  private JwtAuthenticationFilter jwtAuthenticationFilter;
  @Resource
  private AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf()
            .disable()
            .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .addLogoutHandler(new SecurityContextLogoutHandler())
            )
            .authorizeHttpRequests()
            .requestMatchers("/api/register", "/api/login","/api/items")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .cors()
            .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
    return http.build();
  }
}
