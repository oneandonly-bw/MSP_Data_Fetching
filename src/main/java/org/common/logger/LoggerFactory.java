package org.common.logger;

/**
 * Central logger factory for the orchestrator.
 * <p>
 * Provides logger instances per class. All loggers are backed by
 * {@link Log4j2Wrapper}, which delegates to Log4j2.
 * </p>
 * <p>
 * Early startup logging is automatically supported by Log4j2's default root logger,
 * so logging can be used even before the main configuration is applied.
 * </p>
 * <p>
 * Modules should always obtain loggers via {@link #getLogger(Class)}
 * to keep logging consistent, allow central control of the logging implementation,
 * and facilitate future changes to the underlying logging framework without
 * affecting client code.
 * </p>
 */
public final class LoggerFactory {

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is a utility; all methods are static.
     */
    private LoggerFactory() {
        // Utility class; no instances allowed
    }

    /**
     * Returns a logger for the given class.
     * <p>
     * The returned logger is a {@link Log4j2Wrapper} instance that delegates
     * all calls to Log4j2. Each call to this method creates a new wrapper,
     * but the underlying Log4j2 logger is shared by class name.
     *
     * @param clazz the class requesting the logger (must not be null)
     * @return a non-null logger instance
     * @throws IllegalArgumentException if clazz is null
     */
    public static Logger getLogger(Class<?> clazz)
            throws IllegalArgumentException {

        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return new Log4j2Wrapper(clazz);
    }
}
