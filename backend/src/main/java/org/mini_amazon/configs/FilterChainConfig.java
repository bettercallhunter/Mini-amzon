//package org.mini_amazon.configs;
//
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class FilterChainConfig implements Filter {
//  @Override
//  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//    if (servletResponse instanceof HttpServletResponse){
//      HttpServletResponse response = (HttpServletResponse)servletResponse;
//      HttpServletRequest request = (HttpServletRequest) servletRequest;
//      String requestOrigin = request.getHeader("Origin");
//      response.setHeader("Access-Control-Allow-Origin", requestOrigin);
//      response.setHeader("Access-Control-Allow-Credentials", "true");
//      //response.setHeader("Access-Control-Allow-Methods", "*");
//      response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
//      response.setHeader("Access-Control-Max-Age", "3600");
//      response.setHeader("Access-Control-Allow-Headers", "*");
//      filterChain.doFilter(request, response);
//    }
//  }
//}