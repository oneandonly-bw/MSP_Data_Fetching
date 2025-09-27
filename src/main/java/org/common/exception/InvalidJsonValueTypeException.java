package org.common.exception;

/**
 * Thrown when a JSON value exists but has an unexpected type.
 */
public class InvalidJsonValueTypeException extends RuntimeException {

    public InvalidJsonValueTypeException(String path, String expectedType, String actualType) {
        super("Invalid type for JSON path '" + path  +
                "; Expected: " + expectedType + ", actual: " + actualType);
    }

    public InvalidJsonValueTypeException(String path, String expectedType,
                                         String actualType, Throwable cause) {
        super("Invalid type for JSON path '" + path +
                "; Expected: " + expectedType + ", actual: " + actualType, cause);
    }
}
