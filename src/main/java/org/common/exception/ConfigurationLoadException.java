package org.common.exception;

/**
 * Exception thrown when configuration loading or parsing fails.
 */
public class ConfigurationLoadException extends Exception {

  /**
   * Constructs a new ConfigurationLoadException with the specified detail message.
   *
   * @param message the detail message
   */
  public ConfigurationLoadException(String message) {
    super(message);
  }

  /**
   * Constructs a new ConfigurationLoadException with the specified detail message
   * and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public ConfigurationLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}

