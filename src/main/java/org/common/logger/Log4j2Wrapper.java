package org.common.logger;

import org.apache.logging.log4j.LogManager;

/**
 * Logger implementation that delegates directly to Log4j2.
 * <p>
 * Features:
 * <ul>
 *     <li>Supports DEBUG, INFO, WARN, ERROR levels.</li>
 *     <li>Uses Log4j2 native placeholders for arguments.</li>
 *     <li>Thread-safe: multiple threads can safely log using the same instance.</li>
 *     <li>Preserves multi-line messages; handled internally by Log4j2 appenders.</li>
 * </ul>
 * <p>
 * Usage is intended via {@link LoggerFactory#getLogger(Class)}.
 * This class is package-private to prevent direct usage from external packages.
 */
final class Log4j2Wrapper implements Logger {

    /** The underlying Log4j2 logger instance */
    private final org.apache.logging.log4j.Logger delegate;

    /**
     * Constructs a new logger for the specified class.
     * <p>
     * Typically called by {@link LoggerFactory} after logging system is initialized.
     *
     * @param clazz class requesting the logger
     */
    Log4j2Wrapper(Class<?> clazz) {
        this.delegate = LogManager.getLogger(clazz);
    }

    @Override
    public void debug(String msg, Object... args) {
        delegate.debug(msg, args);
    }

    @Override
    public void info(String msg, Object... args) {
        delegate.info(msg, args);
    }

    @Override
    public void warn(String msg, Object... args) {
        delegate.warn(msg, args);
    }

    @Override
    public void error(String msg, Object... args) {
        delegate.error(msg, args);
    }
}
