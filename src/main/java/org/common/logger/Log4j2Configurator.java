package org.common.logger;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.common.config.JsonNodeConfigWrapper;
import org.common.exception.InvalidConfigurationException;
import org.common.exception.LoggerInitializationException;
import org.orchestrator.fs.OrchestratorPaths;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.common.logger.LoggerConfigKeys.*;

/**
 * Fully working Log4j2 configurator for JSON config.
 * Supports console and rolling file appenders with root logger.
 */
public final class Log4j2Configurator {

    private static volatile boolean initialized = false;

    private Log4j2Configurator() {}

    public static synchronized void configureFrom(JsonNodeConfigWrapper logConfig) {
        if (initialized) {
            throw new IllegalStateException("Log4j2 is already initialized");
        }

        try {
            // Stop any previous config
            LoggerContext oldContext = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
            oldContext.stop();

            // Root level
            String levelStr = logConfig.getString(LOG4J2_LEVEL, "INFO");
            Level rootLevel = Level.toLevel(levelStr, Level.INFO);

            ConfigurationBuilder<BuiltConfiguration> builder =
                    org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
                            .newConfigurationBuilder();
            builder.setStatusLevel(Level.ERROR);
            builder.setConfigurationName("DynamicLogConfig");

            // --- Console Appender ---
            JsonNodeConfigWrapper consoleConfig = logConfig.getObject(LOG4J2_CONSOLE);
            String consoleAppenderName = "ConsoleAppender";
            if (consoleConfig != null && consoleConfig.getBoolean(LOG4J2_CONSOLE_ENABLED, false)) {
                LayoutComponentBuilder layout = builder.newLayout("PatternLayout")
                        .addAttribute("pattern", consoleConfig.getString(LOG4J2_CONSOLE_PATTERN, "%d{HH:mm:ss} [%t] %-5level %msg%n"));
                AppenderComponentBuilder consoleAppender = builder.newAppender(consoleAppenderName, "CONSOLE")
                        .add(layout);
                builder.add(consoleAppender);
            }

            // --- File Appender ---
            JsonNodeConfigWrapper fileConf = logConfig.getObject(LOG4J2_FILE);
            String fileAppenderName = "FileAppender";
            if (fileConf != null) {
                String path = fileConf.getString(LOG4J2_FILE_PATH);
                path = buildAbsolutePath(path);
                if (path == null || path.isBlank()) {
                    throw new InvalidConfigurationException("File path cannot be null or blank");
                }

                Path absolutePath = Paths.get(path);
                File parent = absolutePath.toFile().getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new InvalidConfigurationException("Cannot create log directory: " + parent);
                }

                LayoutComponentBuilder layout = builder.newLayout("PatternLayout")
                        .addAttribute("pattern", fileConf.getString(LOG4J2_FILE_PATTERN, "%d [%t] %-5level %msg%n"));

                ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
                        .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                                .addAttribute("size", fileConf.getInt(LOG4J2_FILE_MAX_FILE_SIZE_MB, 50) + "MB"));

                ComponentBuilder<?> strategy = builder.newComponent("DefaultRolloverStrategy")
                        .addAttribute("max", fileConf.getInt(LOG4J2_FILE_MAX_BACKUP_FILES, 5));

                AppenderComponentBuilder fileAppender = builder.newAppender(fileAppenderName, "RollingFile")
                        .addAttribute("fileName", absolutePath)
                        .addAttribute("filePattern", absolutePath + "_%i.log")
                        .add(layout)
                        .addComponent(triggeringPolicy)
                        .addComponent(strategy);

                builder.add(fileAppender);
            }

            // --- Root Logger ---
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(rootLevel);
            if (consoleConfig != null && consoleConfig.getBoolean(LOG4J2_CONSOLE_ENABLED, false)) {
                rootLogger.add(builder.newAppenderRef(consoleAppenderName));
            }
            if (fileConf != null) {
                rootLogger.add(builder.newAppenderRef(fileAppenderName));
            }
            builder.add(rootLogger);

            // Initialize
            LoggerContext context = Configurator.initialize(builder.build());
            context.start();

            initialized = true;

        } catch (Exception e) {
            throw new LoggerInitializationException("Failed to initialize Log4j2", e);
        }
    }

    static String  buildAbsolutePath(String relativePath) {
        return String.valueOf(Paths.get(OrchestratorPaths.getInstance().getRoot().toString(), relativePath ));
    }
}
