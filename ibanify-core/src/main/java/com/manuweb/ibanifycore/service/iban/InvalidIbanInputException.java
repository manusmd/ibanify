package com.manuweb.ibanifycore.service.iban;

public final class InvalidIbanInputException extends RuntimeException {

  private final String code;

  public InvalidIbanInputException(String code) {
    super(code);
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
