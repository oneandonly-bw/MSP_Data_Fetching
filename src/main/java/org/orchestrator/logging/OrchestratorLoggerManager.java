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
 * <p>Explicit initialization via init(), thread-safe with AtomicReference.
 * Future reInit() can implement lock-free configuration reload.
 */
public final class OrchestratorLoggerManager {

    private static final AtomicReference<LoggerContext> contextRef = new AtomicReference<>();
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private OrchestratorLoggerManager() {}

    // ======= Public API =======

    /**
     * Initializes the logging system.
     *
     * @throws LoggerInitializationFailedException if logger cannot be configured
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
     * <p>Requires that init() has been called first.
     */
    public static OrchestratorLogger getLogger(Class<?> clazz) {
        if (!initialized.get()) {
            throw new IllegalStateException("OrchestratorLoggerManager not initialized. Call init() first.");
        }
        LoggerContext context = contextRef.get(); // atomic read
        if (context == null) {
            throw new IllegalStateException("LoggerContext is null despite initialization.");
        }
        Logger log4jLogger = LogManager.getLogger(clazz);
        return new LoggerAdapter(log4jLogger);
    }

    /** Shuts down the logging system. */
    public static void shutdown() {
        LoggerContext context = contextRef.getAndSet(null);
        if (context != null) {
            context.stop();
        }
        initialized.set(false);
    }

    /** Stub for future re-initialization of logging configuration. */
    public static void reInit() {
        // TODO: implement lock-free reloading using contextRef.set(newContext)
    }

    // ======= Internal Configuration =======

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
            throw new LoggerInitializationFailedException( "Logger initialization failed.", e);
        }
    }

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
