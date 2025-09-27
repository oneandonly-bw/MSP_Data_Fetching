package org.common.exception;

/**
 * Thrown when a configuration file is invalid or fails validation.
 */
public class InvalidConfigurationException extends RuntimeException {

  public InvalidConfigurationException(String message) {
    super(message);
  }

  public InvalidConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
