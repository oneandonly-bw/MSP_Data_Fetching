package org.common.exception;

/**
 * Thrown when logger initialization fails.
 */
public class LoggerInitializationException extends RuntimeException {

  public LoggerInitializationException(String message) {
    super(message);
  }

  public LoggerInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}

