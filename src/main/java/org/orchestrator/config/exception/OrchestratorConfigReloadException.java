package org.orchestrator.config.exception;

/**
 * Thrown when reinitialization of Orchestrator configuration fails.
 * The system continues running with the old configuration.
 */
public class OrchestratorConfigReloadException extends Exception {


    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     */
    public OrchestratorConfigReloadException(String message) {
        super(message);
    }
    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the original cause
     */
    public OrchestratorConfigReloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

