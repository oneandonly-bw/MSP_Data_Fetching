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
import org.orchestrator.config.model.LogConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Centralized logger for the Orchestrator.
 * Lazy initialization on first getLogger call.
 */
public final class OrchestratorLoggerManager {

    private static final Object lock = new Object();
    private static volatile OrchestratorLoggerManager instance;
    private static LoggerContext context;
    private volatile static boolean initialized = false;

    private OrchestratorLoggerManager() {}


    public static OrchestratorLogger getLogger(Class<?> clazz)
    throws IOException {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    configureLogging();
                    initialized = true;
                }
            }
        }
        Logger log4jLogger = LogManager.getLogger(clazz);
        return new LoggerAdapter(log4jLogger);
    }

    public static void shutdown() {
        if (context != null) {
            context.stop();    // stops Log4j2 logging context
            context = null;    // release reference
        }
        initialized = false;   // reset initialization flag
    }


    static private void configureLogging()
    throws IOException {

        OrchestratorConfig orchestratorConfig = OrchestratorConfig.getInstance();
        LogConfig logConfig = orchestratorConfig.getLog4j2Config();

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("OrchestratorLogging");

        Level rootLevel = toLog4jLevel(logConfig.getLevel());

        // Console appender (optional)
        if (logConfig.isConsoleEnabled()) {
            AppenderComponentBuilder console = builder.newAppender("Console", "CONSOLE").addAttribute("target", "SYSTEM_OUT");
            console.add(builder.newLayout("PatternLayout").addAttribute("pattern", logConfig.getConsolePattern()));
            builder.add(console);
        }

        // File appender (always enabled)
        OrchestratorPaths paths = OrchestratorPaths.getInstance();
        Path logFile = paths.getLogsDirPath().resolve("orchestrator.log");

        if (!Files.exists(paths.getLogsDirPath())) {
            throw new RuntimeException("Log directory does not exist: " + paths.getLogsDirPath());
        }

        AppenderComponentBuilder fileAppender = builder.newAppender("File", "RollingFile").addAttribute("fileName", logFile.toAbsolutePath().toString()).addAttribute("filePattern", logFile.toAbsolutePath() + ".%i");

        fileAppender.add(builder.newLayout("PatternLayout").addAttribute("pattern", logConfig.getFilePattern()));

        fileAppender.addComponent(builder.newComponent("Policies").addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", logConfig.getMaxFileSizeMB() + "MB")));

        fileAppender.addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", logConfig.getMaxBackupFiles()));

        builder.add(fileAppender);

        // Single root logger with references to all appenders
        var rootLogger = builder.newRootLogger(rootLevel);
        if (logConfig.isConsoleEnabled()) {
            rootLogger.add(builder.newAppenderRef("Console"));
        }
        rootLogger.add(builder.newAppenderRef("File"));
        builder.add(rootLogger);

        // Initialize Log4j2 for application lifetime
        context = Configurator.initialize(builder.build());
        context.updateLoggers();
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
