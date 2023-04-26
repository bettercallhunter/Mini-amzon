package org.mini_amazon.configs;

import org.mini_amazon.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import jakarta.annotation.Resource;

@Configuration
public class ApplicationConfig {
  @Resource
  private UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) {
        return userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not found."));
      }
    };
  }


  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Bean
//  CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(Arrays.asList("*"));
//    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
//    configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
//    configuration.setAllowCredentials(true);
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }
}
