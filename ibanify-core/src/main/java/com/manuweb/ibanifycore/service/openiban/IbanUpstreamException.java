package com.manuweb.ibanifycore.service.openiban;

import java.util.Arrays;

public final class IbanUpstreamException extends RuntimeException {

  private final String messageKey;
  private final transient Object[] args;

  public IbanUpstreamException(String messageKey, Object... args) {
    super(messageKey + Arrays.toString(args));
    this.messageKey = messageKey;
    this.args = args != null ? args.clone() : new Object[0];
  }

  public IbanUpstreamException(String messageKey, Throwable cause, Object... args) {
    super(messageKey + Arrays.toString(args), cause);
    this.messageKey = messageKey;
    this.args = args != null ? args.clone() : new Object[0];
  }

  public String getMessageKey() {
    return messageKey;
  }

  public Object[] resolvedArgs() {
    return args.clone();
  }
}
