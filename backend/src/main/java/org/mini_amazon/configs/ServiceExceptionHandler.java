package org.mini_amazon.configs;

import org.hibernate.exception.ConstraintViolationException;
import org.mini_amazon.errors.ServiceError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

  class ErrorHolder {
    private final String message;

    public ErrorHolder(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
  @ExceptionHandler(ServiceError.class)
  protected ResponseEntity<Object> handleServiceError(
          ServiceError serviceError) {
    return new ResponseEntity<>(new ErrorHolder(serviceError.getMessage()), BAD_REQUEST);
  }
//

}