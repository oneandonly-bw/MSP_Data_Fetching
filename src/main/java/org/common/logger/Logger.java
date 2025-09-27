package org.common.logger;

/**
 * Central logging interface for all modules in the orchestrator.
 * <p>
 * A {@code Logger} provides methods for writing log messages at different
 * severity levels (INFO, DEBUG, WARN, ERROR). All modules should obtain
 * their logger instances exclusively via
 * {@link LoggerFactory#getLogger(Class)}.
 * </p>
 *
 * <h3>Placeholder support</h3>
 * <p>
 * Log messages may contain placeholders in the form of <code>{}</code>.
 * Each placeholder is replaced by the string representation of the
 * corresponding argument. This avoids unnecessary string concatenation
 * when the log level is disabled.
 * </p>
 *
 * <h4>Examples</h4>
 * <pre>{@code
 * Logger log = LoggerFactory.getLogger(MyService.class);
 *
 * // Simple message
 * log.info("Service started");
 *
 * // Message with placeholders
 * log.debug("User {} logged in from {}", userName, ipAddress);
 *
 * // Warning with numeric values
 * log.warn("Low disk space: {} MB remaining", remainingMb);
 *
 * // Error with multiple placeholders
 * log.error("Failed to process request {} for user {}", requestId, userName);
 * }</pre>
 *
 */
public interface Logger {

    /**
     * Logs a message at INFO level.
     *
     * @param msg  the message with optional placeholders ({})
     * @param args values to substitute into placeholders
     */
    void info(String msg, Object... args);

    /**
     * Logs a message at DEBUG level.
     *
     * @param msg  the message with optional placeholders ({})
     * @param args values to substitute into placeholders
     */
    void debug(String msg, Object... args);

    /**
     * Logs a message at WARN level.
     *
     * @param msg  the message with optional placeholders ({})
     * @param args values to substitute into placeholders
     */
    void warn(String msg, Object... args);

    /**
     * Logs a message at ERROR level.
     *
     * @param msg  the message with optional placeholders ({})
     * @param args values to substitute into placeholders
     */
    void error(String msg, Object... args);
}
