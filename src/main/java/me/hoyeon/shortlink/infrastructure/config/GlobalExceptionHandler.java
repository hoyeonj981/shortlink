package me.hoyeon.shortlink.infrastructure.config;

import me.hoyeon.shortlink.application.ApplicationException;
import me.hoyeon.shortlink.application.DuplicateUrlException;
import me.hoyeon.shortlink.application.MemberNotFoundException;
import me.hoyeon.shortlink.application.MismatchPasswordException;
import me.hoyeon.shortlink.application.NotAccessibleUrlException;
import me.hoyeon.shortlink.application.UrlNotFoundException;
import me.hoyeon.shortlink.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
  }

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

  @ExceptionHandler({MemberNotFoundException.class, MismatchPasswordException.class})
  public ResponseEntity<ErrorResponse> handleMemberNotFoundException(ApplicationException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse("MEMBER_NOT_FOUND", e.getMessage()));
  }

  @ExceptionHandler(NotSupportedProviderException.class)
  public ResponseEntity<ErrorResponse> handleNotSupportedProvider(NotSupportedProviderException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("NOT_SUPPORTED_PROVIDER", e.getMessage()));
  }
}
