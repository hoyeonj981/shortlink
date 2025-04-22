package me.hoyeon.shortlink.config.exception;

import me.hoyeon.shortlink.application.ApplicationException;
import me.hoyeon.shortlink.application.DuplicateUrlException;
import me.hoyeon.shortlink.application.NotAccessibleUrlException;
import me.hoyeon.shortlink.application.UrlNotFoundException;
import me.hoyeon.shortlink.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /*
  * 예외 구조를 어떻게 만들까?
  * 도메인에서 발생하는 예외는?
  * 인프라 예외는 애플리케이션에서 감싸도록 만들었다
  *
  * 1. 도메인에서 발생하는 예외를 굳이 애플리케이션에서 래핑할 필요가 없는 경우라면?
  * 2. 애플리케이션에서 발생하는 예외는 http 상태 코드로 어떻게 변환할까?
  * 3. 공통된 예외처리? 특별하게 나눠서 예외처리?
  *
   */
  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("APP_ERROR", e.getMessage()));
  }

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ErrorResponse> handleException(DomainException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
  }

  @ExceptionHandler(UrlNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("URL_NOT_FOUND", e.getMessage()));
  }

  @ExceptionHandler(DuplicateUrlException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUrlException(DuplicateUrlException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("DUPLICATED_URL", e.getMessage()));
  }

  @ExceptionHandler(NotAccessibleUrlException.class)
  public ResponseEntity<ErrorResponse> handleNotAccessibleUrlException(
      NotAccessibleUrlException e
  ) {
    return ResponseEntity.status(HttpStatus.GONE)
        .body(new ErrorResponse("NOT_ACCSIBLE_URL", e.getMessage()));
  }
}
