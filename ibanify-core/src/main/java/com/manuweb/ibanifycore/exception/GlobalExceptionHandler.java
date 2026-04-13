package com.manuweb.ibanifycore.exception;

import com.manuweb.ibanifycore.entities.ApiErrorResponse;
import com.manuweb.ibanifycore.service.iban.InvalidIbanInputException;
import com.manuweb.ibanifycore.service.openiban.IbanUpstreamException;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private final MessageSource messageSource;

  public GlobalExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, Locale locale) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + resolveFieldErrorMessage(fe, locale))
            .collect(Collectors.joining("; "));
    return ResponseEntity.badRequest()
        .body(new ApiErrorResponse("VALIDATION_ERROR", message));
  }

  private String resolveFieldErrorMessage(FieldError fe, Locale locale) {
    try {
      return messageSource.getMessage(fe, locale);
    } catch (NoSuchMessageException e) {
      if (fe.getDefaultMessage() != null) {
        return fe.getDefaultMessage();
      }
      return fe.getCode() != null ? fe.getCode() : "invalid";
    }
  }

  @ExceptionHandler(InvalidIbanInputException.class)
  ResponseEntity<ApiErrorResponse> handleInvalidIban(
      InvalidIbanInputException ex, Locale locale) {
    String key = "iban.error." + ex.getCode().toLowerCase(Locale.ROOT);
    String text = messageSource.getMessage(key, null, ex.getCode(), locale);
    return ResponseEntity.badRequest().body(new ApiErrorResponse(ex.getCode(), text));
  }

  @ExceptionHandler(IbanUpstreamException.class)
  ResponseEntity<ApiErrorResponse> handleUpstream(IbanUpstreamException ex, Locale locale) {
    String text =
        messageSource.getMessage(
            ex.getMessageKey(), ex.resolvedArgs(), ex.getMessageKey(), locale);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new ApiErrorResponse("UPSTREAM_ERROR", text));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex, Locale locale) {
    log.error("Unhandled exception", ex);
    String text =
        messageSource.getMessage("errors.internal", null, "Something went wrong", locale);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiErrorResponse("INTERNAL_ERROR", text));
  }
}
