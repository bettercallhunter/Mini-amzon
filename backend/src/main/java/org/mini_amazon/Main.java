package org.mini_amazon;

import org.mini_amazon.services.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

import java.io.IOException;

/**
 * @Configuration: Tags the class as a source of bean definitions for the application context.
 * @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 * other beans, and various property settings. For example, if spring-webmvc is on the classpath,
 * this annotation flags the application as a web application and activates key behaviors, such as
 * setting up a DispatcherServlet.
 * @ComponentScan: Tells Spring to look for other components, configurations, and services in the
 * com/example package, letting it find the controllers.
 */
@SpringBootApplication
public class Main {

//  @Bean
//  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
//    return new ProtobufHttpMessageConverter();
//  }

    public static void main(String[] args) {
      System.out.println("Hello World");


//    SpringApplication.run(Main.class, args);
  }

}