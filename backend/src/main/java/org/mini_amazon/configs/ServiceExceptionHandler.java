package org.mini_amazon.configs;

import org.mini_amazon.errors.ServiceError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

  record ErrorHolder(String message) {
  }
  @ExceptionHandler(ServiceError.class)
  protected ResponseEntity<Object> handleServiceError(
          ServiceError serviceError) {
    return new ResponseEntity<>(new ErrorHolder(serviceError.getMessage()), BAD_REQUEST);
  }
//

}