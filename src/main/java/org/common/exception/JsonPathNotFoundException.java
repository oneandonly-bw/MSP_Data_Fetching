package org.common.exception;

/**
 * Thrown when a requested JSON path does not exist in the configuration.
 */
public class JsonPathNotFoundException extends RuntimeException {

    public JsonPathNotFoundException(String path) {
        super("JSON path not found: " + path);
    }

    public JsonPathNotFoundException(String path, Throwable cause) {
        super("JSON path not found: " + path, cause);
    }
}
