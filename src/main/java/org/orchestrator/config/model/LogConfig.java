package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.orchestrator.OrchestratorPaths;
import org.orchestrator.config.exception.OrchestratorConfigException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents the logging configuration for the Orchestrator.
 * Includes log level, file, and console settings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogConfig {

    /**
     * Supported log levels for Orchestrator logging.
     * Case-insensitive deserialization is supported via {@link #fromString(String)}.
     */
    public enum LogLevel {
        INFO,
        WARN,
        ERROR,
        DEBUG;

        /**
         * Converts a string to a LogLevel enum value.
         * Accepts any case (e.g., "info", "INFO", "Info").
         *
         * @param key the log level string
         * @return corresponding LogLevel enum
         */
        @JsonCreator
        public static LogLevel fromString(String key) {
            if (key == null) return null;
            return LogLevel.valueOf(key.toUpperCase());
        }
    }

    @JsonProperty("level")
    private LogLevel level;

    @JsonProperty("file")
    private LogFileConfig file;

    @JsonProperty("console")
    private LogConsoleConfig console;

    LogConfig() {}

    /**
     * Returns the configured log level.
     */
    public LogLevel getLevel() {
        return level;
    }

    /**
     * Returns true if console logging is enabled.
     */
    public boolean isConsoleEnabled() {
        return console.isEnabled();
    }

    /**
     * Returns the log pattern for console output.
     */
    public String getConsolePattern() {
        return console.getPattern();
    }

    /**
     * Returns the path to the log file.
     */
    public Path getLogsDirPath() throws IOException {
        return OrchestratorPaths.getInstance().getLogsDirPath();
    }

    /**
     * Returns the log pattern for file output.
     */
    public String getFilePattern() {
        return file.getPattern();
    }

    /**
     * Returns the maximum log file size in MB.
     */
    public int getMaxFileSizeMB() {
        return file.getMaxFileSizeMB();
    }

    /**
     * Returns the number of backup log files to retain.
     */
    public int getMaxBackupFiles() {
        return file.getMaxBackupFiles();
    }

    /**
     * Validates the log configuration.
     * Ensures level, file, and console sections are present and valid.
     *
     * @throws OrchestratorConfigException if any validation fails
     */
    public void validate() throws OrchestratorConfigException {
        if (level == null) {
            throw new OrchestratorConfigException(
                    "LogConfig.level is missing or invalid. Allowed values: INFO, WARN, ERROR, DEBUG");
        }

        if (file == null) {
            throw new OrchestratorConfigException("LogConfig.file section is missing");
        }
        file.validate();

        if (console == null) {
            throw new OrchestratorConfigException("LogConfig.console section is missing");
        }
        console.validate();
    }
}
