package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.orchestrator.OrchestratorPaths;
import org.orchestrator.config.exception.OrchestratorConfigException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents the logging configuration for the Orchestrator.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class LogConfig {

    public enum LogLevel {

        INFO, WARN, ERROR, DEBUG;

        @JsonCreator
        public static LogLevel fromString(String key) {
            if (key == null) return null;
            return LogLevel.valueOf(key.toUpperCase());
        }
    }

    @JsonProperty("level")
    private final LogLevel level;

    @JsonProperty("file")
    private final LogFileConfig file;

    @JsonProperty("console")
    private final LogConsoleConfig console;

    /**
     * Package-private constructor for Jackson deserialization.
     */
    LogConfig(
            @JsonProperty(value = "level", required = true) String level,
            @JsonProperty("file") LogFileConfig file,
            @JsonProperty("console") LogConsoleConfig console
    ) {
        this.level = LogLevel.fromString(level);
        this.file = file;
        this.console = console;
        validate();
    }

    public LogLevel getLevel() {
        return level;
    }

    public boolean isConsoleEnabled() {
        return console.isEnabled();
    }

    public String getConsolePattern() {
        return console.getPattern();
    }

    public Path getLogsDirPath() throws IOException {
        return OrchestratorPaths.getInstance().getLogsDirPath();
    }

    public String getFilePattern() {
        return file.getPattern();
    }

    public int getMaxFileSizeMB() {
        return file.getMaxFileSizeMB();
    }

    public int getMaxBackupFiles() {
        return file.getMaxBackupFiles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogConfig logConfig))
            return false;
        return level == logConfig.level &&
                Objects.equals(file, logConfig.file) &&
                Objects.equals(console, logConfig.console);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, file, console);
    }

    @Override
    public String toString() {
        return "LogConfig{" +
                "level=" + level +
                ", file=" + file.toString() +
                ", console=" + console.toString() +
                '}';
    }

    /** Validates the log configuration. Called from constructor. */
    private void validate() {

        if (level == null) {
            throw new OrchestratorConfigException(
                    "LogConfig.level is missing or invalid. Allowed values: INFO, WARN, ERROR, DEBUG.");
        }
        if (file == null) {
            throw new OrchestratorConfigException("LogConfig.file section is missing.");
        }

        if (console == null) {
            throw new OrchestratorConfigException("LogConfig.console section is missing.");
        }
    }
}
