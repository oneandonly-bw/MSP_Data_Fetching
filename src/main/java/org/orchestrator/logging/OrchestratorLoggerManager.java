package org.orchestrator.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.orchestrator.OrchestratorPaths;
import org.orchestrator.config.OrchestratorConfig;
import org.orchestrator.config.exception.LoggerInitializationFailedException;
import org.orchestrator.config.model.LogConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Centralized logger manager for the Orchestrator.
 *
 * <p>This class controls initialization, retrieval, and shutdown of the Log4j2 logging system.
 * It ensures that logging is configured consistently across the entire application.
 *
 * <ul>
 *   <li>Thread-safe initialization using {@link AtomicBoolean} and {@link AtomicReference}.</li>
 *   <li>Supports returning a default fallback logger (root logger) if {@link #init()} has not been called.</li>
 *   <li>Designed for future lock-free reinitialization of logging configuration.</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>{@code
 * OrchestratorLoggerManager.init();
 * OrchestratorLogger log = OrchestratorLoggerManager.getLogger(MyClass.class);
 * log.info("Service started");
 * }</pre>
 */
public final class OrchestratorLoggerManager {

    /** Holds the current {@link LoggerContext}. Updated atomically during init or shutdown. */
    private static final AtomicReference<LoggerContext> contextRef = new AtomicReference<>();

    /** Tracks whether the logging system has been initialized. */
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    /** Private constructor to prevent instantiation. */
    private OrchestratorLoggerManager() {}

    // ======= Public API =======

    /**
     * Initializes the logging system with configuration loaded from {@link OrchestratorConfig}.
     *
     * <p>If already initialized, this method does nothing.
     *
     * @throws LoggerInitializationFailedException if logging configuration fails to load
     */
    public static void init() throws LoggerInitializationFailedException {
        if (!initialized.get()) {
            synchronized (OrchestratorLoggerManager.class) {
                if (!initialized.get()) {
                    LoggerContext context = configureLogging();
                    contextRef.set(context); // atomic swap
                    initialized.set(true);
                }
            }
        }
    }

    /**
     * Returns a logger for the specified class.
     *
     * <p>If the logging system has not been initialized via {@link #init()}, a logger
     * wrapping the Log4j2 root logger will be returned instead. This ensures that
     * logging calls never throw errors even if initialization is missing.
     *
     * @param clazz the class for which a logger is requested
     * @return a wrapped {@link OrchestratorLogger} instance
     */

    public static OrchestratorLogger getLogger(Class<?> clazz) {

        if (!initialized.get()) {
            // Fallback: default root logger
            return new OrchestratorLogger(null);
        }

        LoggerContext context = contextRef.get(); // atomic read
        if (context == null) {
            throw new IllegalStateException("LoggerContext is null despite initialization.");
        }

        Logger log4jLogger = LogManager.getLogger(clazz);
        return new OrchestratorLogger(log4jLogger);
    }

    /**
     * Shuts down the logging system.
     *
     * <p>This stops the {@link LoggerContext} and resets initialization state.
     * After shutdown, calls to {@link #getLogger(Class)} will return loggers
     * that delegate to the Log4j2 root logger.
     */
    public static void shutdown() {
        LoggerContext context = contextRef.getAndSet(null);
        if (context != null) {
            context.stop();
        }
        initialized.set(false);
    }

    /**
     * Placeholder for future logging reconfiguration.
     *
     * <p>Intended for supporting hot-reload of logging settings without
     * application restart. Will replace the context in {@link #contextRef}.
     */
    public static void reInit() {
        // TODO: implement lock-free reloading using contextRef.set(newContext)
    }

    /**
     * Indicates whether the logging system has been initialized.
     *
     * @return {@code true} if initialized, {@code false} otherwise
     */
    public static boolean isInitialized() {
        return initialized.get();
    }

    // ======= Internal Configuration =======

    /**
     * Builds and initializes a new {@link LoggerContext} based on application configuration.
     *
     * @return the initialized {@link LoggerContext}
     * @throws LoggerInitializationFailedException if configuration fails (e.g., missing log directory)
     */
    private static LoggerContext configureLogging() throws LoggerInitializationFailedException {
        try {
            OrchestratorConfig orchestratorConfig = OrchestratorConfig.getInstance();
            LogConfig logConfig = orchestratorConfig.getLog4j2Config();

            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setStatusLevel(Level.ERROR);
            builder.setConfigurationName("OrchestratorLogging");

            Level rootLevel = toLog4jLevel(logConfig.getLevel());

            // Console appender
            if (logConfig.isConsoleEnabled()) {
                AppenderComponentBuilder console = builder.newAppender("Console", "CONSOLE")
                        .addAttribute("target", "SYSTEM_OUT");
                console.add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", logConfig.getConsolePattern()));
                builder.add(console);
            }

            // File appender
            OrchestratorPaths paths = OrchestratorPaths.getInstance();
            Path logFile = paths.getLogsDirPath().resolve("orchestrator.log");

            if (!Files.exists(paths.getLogsDirPath())) {
                throw new LoggerInitializationFailedException(
                        "Log directory does not exist: " + paths.getLogsDirPath());
            }

            AppenderComponentBuilder fileAppender = builder.newAppender("File", "RollingFile")
                    .addAttribute("fileName", logFile.toAbsolutePath().toString())
                    .addAttribute("filePattern", logFile.toAbsolutePath() + ".%i");

            fileAppender.add(builder.newLayout("PatternLayout")
                    .addAttribute("pattern", logConfig.getFilePattern()));

            fileAppender.addComponent(builder.newComponent("Policies")
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                            .addAttribute("size", logConfig.getMaxFileSizeMB() + "MB")));

            fileAppender.addComponent(builder.newComponent("DefaultRolloverStrategy")
                    .addAttribute("max", logConfig.getMaxBackupFiles()));

            builder.add(fileAppender);

            // Root logger
            var rootLogger = builder.newRootLogger(rootLevel);
            if (logConfig.isConsoleEnabled()) {
                rootLogger.add(builder.newAppenderRef("Console"));
            }
            rootLogger.add(builder.newAppenderRef("File"));
            builder.add(rootLogger);

            // Initialize Log4j2 context
            LoggerContext context = Configurator.initialize(builder.build());
            context.updateLoggers();
            return context;

        } catch (Exception e) {
            throw new LoggerInitializationFailedException("Logger initialization failed.", e);
        }
    }

    /**
     * Converts the internal {@link LogConfig.LogLevel} enum into a Log4j2 {@link Level}.
     *
     * @param levelEnum application-defined log level
     * @return corresponding Log4j2 {@link Level}, defaults to {@link Level#INFO} if null
     */
    private static Level toLog4jLevel(LogConfig.LogLevel levelEnum) {
        if (levelEnum == null) return Level.INFO;
        return switch (levelEnum) {
            case DEBUG -> Level.DEBUG;
            case WARN -> Level.WARN;
            case ERROR -> Level.ERROR;
            default -> Level.INFO;
        };
    }
}
