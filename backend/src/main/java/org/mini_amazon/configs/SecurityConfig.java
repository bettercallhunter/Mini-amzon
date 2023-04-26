package org.mini_amazon.configs;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter  ;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {

  @Resource
  private JwtAuthenticationFilter jwtAuthenticationFilter;
  @Resource
  private AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http = http.csrf().disable().cors().and();
    http = http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
//    http = http.exceptionHandling()
//            .authenticationEntryPoint((request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())).and();
    http = http.authorizeHttpRequests()
            .requestMatchers(HttpMethod.OPTIONS).permitAll().and();

    http.authorizeHttpRequests()
            .requestMatchers("/api/register", "/api/login", "/api/items").permitAll()
//            .requestMatchers("/api/**").permitAll()
            .anyRequest().authenticated();
    http.authenticationProvider(authenticationProvider);
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    http//.cors(Customizer.withDefaults())
////            .logout(logout -> logout
////                    .logoutUrl("/api/logout")
////                    .addLogoutHandler(new SecurityContextLogoutHandler())
////            )
//            .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
////            .and()
////            .authorizeHttpRequests()
////            .requestMatchers("/api/register", "/api/login", "/api/items")
////            .permitAll()
////            .anyRequest()
////            .authenticated()
//            .and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
//
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//
//    http.csrf().disable().headers().frameOptions().disable();
    return http.build();
  }

//  @Bean
//  public FilterRegistrationBean corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(true);
//    config.addAllowedOrigin("*");
//    config.addAllowedHeader("*");
//    config.addAllowedMethod("*");
//    config.addAllowedHeader("Authorization");
//    config.addAllowedHeader("authorization");
//    config.addAllowedHeader("content-type");
//    source.registerCorsConfiguration("/**", config);
//    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//    bean.setOrder(0);
//    return bean;
//  }

//  @Bean
//  public CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.addAllowedOrigin("*");
//    configuration.addAllowedMethod("*");
//    configuration.addAllowedHeader("*");
//    configuration.setAllowCredentials(true);
//    configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));
//    configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type"));
//
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }
}
