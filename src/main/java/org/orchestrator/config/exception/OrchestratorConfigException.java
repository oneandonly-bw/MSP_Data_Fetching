package org.orchestrator.config.exception;

public class OrchestratorConfigException extends RuntimeException {

    public OrchestratorConfigException(String message) {
        super(message);
    }

    public OrchestratorConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
