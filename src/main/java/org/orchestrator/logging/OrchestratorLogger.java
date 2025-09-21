package org.orchestrator.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Adapter for Log4j2 Logger used in the Orchestrator.
 * <p>
 * This class wraps a Log4j2 Logger delegate. If the logger passed in the constructor
 * is null (for example, when the logging system is not initialized yet),
 * it uses the root logger of Log4j2 Logger instead.
 * <p>
 * This ensures that logging calls (info, warn, error, debug) are always safe
 * and will never throw exceptions due to an uninitialized logger.
 */
public class OrchestratorLogger {

    /** Root logger used when no specific logger is available */
   private static final Logger DEFAULT_LOGGER = LogManager.getRootLogger();

    /** The actual logger delegate */
    private final Logger delegate;

    /**
     * Constructor.
     *
     * @param logger the Log4j2 Logger delegate, may be null
     *               (if null, root logger of Log4j2 Logger is used)
     */
    OrchestratorLogger(Logger logger) {

        // Explicitly enforce non-null to make IDE happy :(
        this.delegate = Objects.requireNonNull(
                logger != null ? logger : DEFAULT_LOGGER,
                "Logger delegate must never be null"
        );
    }

    /** Logs an INFO message */
    public void info(String msg) {
        delegate.info(msg);
    }

    /** Logs a WARN message */
    public void warn(String msg) {
        delegate.warn(msg);
    }

    /** Logs an ERROR message */
    public void error(String msg) {
        delegate.error(msg);
    }

    /** Logs a DEBUG message */
    public void debug(String msg) {
        delegate.debug(msg);
    }
}
