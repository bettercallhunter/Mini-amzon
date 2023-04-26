package org.mini_amazon.configs;

import org.mini_amazon.services.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Resource
  private JwtService jwtService;
  @Resource
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


//    Enumeration<String> headerNames = request.getHeaderNames();
//    while (headerNames.hasMoreElements()) {
//      String headerName = headerNames.nextElement();
//      System.out.println(headerName + " : " + request.getHeader(headerName));
//    }
//    System.out.println("-------------------");


    final String authorizationHeader;
    if (request.getHeader("Authorization") != null) {
      authorizationHeader = request.getHeader("Authorization");
    } else {
      authorizationHeader = request.getHeader("authorization");
    }
//    System.out.println(authorizationHeader);

    final String jwt;
    final String username;

    if (authorizationHeader == null) {
      filterChain.doFilter(request, response);
      return;
    } else {
//      jwt = authorizationHeader.substring(7);
      jwt = authorizationHeader.split(" ")[1].trim();
      username = jwtService.extractUsername(jwt);
//      System.out.println("username: " + username);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//        System.out.println("isvalid: " + jwtService.isTokenValid(jwt, userDetails));
        if (jwtService.isTokenValid(jwt, userDetails)) {

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
//      System.out.println(request.getMethod() + request.getRequestURL());
      filterChain.doFilter(request, response);
    }
  }
}