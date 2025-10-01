package org.common.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.LogManager;

import org.common.exception.LoggerInitializationException;
import org.common.util.FileUtils;

import java.io.File;

/**
 * Log4j2 configurator using {@link RotationLogConfig}.
 * Supports console and rolling file appenders with root logger.
 */
public final class Log4j2Configurator {

    //Attributes
    private static final String PATTERN_ATTR = "pattern";
    private static final String SIZE_ATTR = "size";
    private static final String MAX_ATTR = "max";
    private static final String FILE_NAME_ATTR = "fileName";
    private static final String FILE_PATTERN_ATTR = "filePattern";

    //Plugins
    private static final String PATTERN_LAYOUT = "PatternLayout";
    private static final String POLICIES = "Policies";
    private static final String SIZE_BASED_POLICY = "SizeBasedTriggeringPolicy";
    private static final String ROLLOVER_STRATEGY = "DefaultRolloverStrategy";
    private static final String ROLLING_FILE = "RollingFile";
    private static final String CONSOLE = "CONSOLE";

    //Names
    private static final String DEFAULT_CONFIG_NAME = "DynamicLogConfig"; // configuration name
    private static final String CONSOLE_APPENDER = "ConsoleAppender";
    private static final String FILE_APPENDER = "FileAppender";

    private static volatile boolean initialized = false;
    private static LoggerContext context;

    private Log4j2Configurator() {
        // prevent instantiation
    }

    /**
     * Configures Log4j2 using a given {@link RotationLogConfig}.
     * <p>
     * The configuration is first validated by calling {@link RotationLogConfig#validate()}.
     *
     * @param config the logging configuration
     */
    public static synchronized void configureFrom(RotationLogConfig config)
    throws IllegalStateException, LoggerInitializationException {

        if (initialized) {
            throw new IllegalStateException("Log4j2 is already initialized");
        }

        try {
            // Validate configuration first
            config.validate();

            // Stop previous context
            LoggerContext oldContext = (LoggerContext) LogManager.getContext(false);
            oldContext.stop();

            // Root logger level
            Level rootLevel = Level.toLevel(config.getRootLevel(), Level.INFO);

            // Build configuration
            ConfigurationBuilder<BuiltConfiguration> builder =
                    ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setStatusLevel(Level.ERROR);
            builder.setConfigurationName(DEFAULT_CONFIG_NAME);

            // --- Console Appender ---
            if (config.isConsoleEnabled()) {
                LayoutComponentBuilder layout = builder.newLayout(PATTERN_LAYOUT)
                        .addAttribute(PATTERN_ATTR, config.getConsolePattern());

                AppenderComponentBuilder consoleAppender =
                        builder.newAppender(CONSOLE_APPENDER, CONSOLE).add(layout);
                builder.add(consoleAppender);
            }

            // --- File Appender ---
            if (config.isFileEnabled()) {

                String resolvedFilePath = config.getFilePath().toAbsolutePath().toString();
                String resolvedFileNoExtension = FileUtils.removeExtension(resolvedFilePath);
                String extension = FileUtils.getFileExtension(resolvedFilePath);
                if (extension.isBlank()) {
                    extension = "log";
                    resolvedFilePath = resolvedFilePath + "." + extension;
                }

                File parent = new File(resolvedFilePath).getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new LoggerInitializationException("Cannot create log directory: " + parent);
                }

                LayoutComponentBuilder layout = builder.newLayout(PATTERN_LAYOUT)
                        .addAttribute(PATTERN_ATTR, config.getFilePattern());

                ComponentBuilder<?> triggeringPolicy = builder.newComponent(POLICIES)
                        .addComponent(builder.newComponent(SIZE_BASED_POLICY)
                                .addAttribute(SIZE_ATTR, config.getMaxFileSizeMb() + "MB"));

                ComponentBuilder<?> strategy = builder.newComponent(ROLLOVER_STRATEGY)
                        .addAttribute(MAX_ATTR, config.getMaxRollovers());

                AppenderComponentBuilder fileAppender = builder.newAppender(FILE_APPENDER, ROLLING_FILE)
                        .addAttribute(FILE_NAME_ATTR, resolvedFilePath)
                        .addAttribute(FILE_PATTERN_ATTR, resolvedFileNoExtension + "_%i." + extension)
                        .add(layout)
                        .addComponent(triggeringPolicy)
                        .addComponent(strategy);

                builder.add(fileAppender);
            }

            // --- Root Logger ---
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(rootLevel);
            if (config.isConsoleEnabled()) {
                rootLogger.add(builder.newAppenderRef(CONSOLE_APPENDER));
            }
            if (config.isFileEnabled()) {
                rootLogger.add(builder.newAppenderRef(FILE_APPENDER));
            }
            builder.add(rootLogger);

            // Initialize Log4j2
            context = Configurator.initialize(builder.build());
            context.start();

            initialized = true;

        } catch (Exception e) {
            throw new LoggerInitializationException("Failed to initialize Log4j2", e);
        }
    }

    /**
     * Shuts down the Log4j2 logging context managed by this configurator.
     * <p>
     * This method stops the {@link LoggerContext} and releases all resources associated with it.
     * After calling this method, the configurator can be re-initialized using
     * {@link #configureFrom(RotationLogConfig)}.
     * <p>
     * It is recommended to call this method during application shutdown to ensure
     * all loggers are properly flushed and resources are cleaned up.
     */
    public static synchronized void shutdown() {
        if (context != null) {
            context.stop();
            context = null;
            initialized = false;
        }
    }

}
