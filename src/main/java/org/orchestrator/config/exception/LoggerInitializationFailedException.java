package org.orchestrator.config.exception;
/**
 * Thrown when OrchestratorLoggerManager fails to initialize.
 * Indicates a problem during logger configuration or setup.
 */
public class LoggerInitializationFailedException extends Exception {

    public LoggerInitializationFailedException(String message) {
        super(message);
    }

    public LoggerInitializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}