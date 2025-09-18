package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Configuration for file-based logging in Log4j2.
 * <p>
 * Represents the "file" section under "log4j2" in the Orchestrator JSON configuration.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogFileConfig {

    /** Log message pattern. Cannot be null or empty. */
    @JsonProperty("pattern")
    private String pattern;

    /** Maximum log file size in megabytes. Must be >= 1. */
    @JsonProperty("maxFileSizeMB")
    private int maxFileSizeMB;

    /** Maximum number of backup files. Must be >= 1. */
    @JsonProperty("maxBackupFiles")
    private int maxBackupFiles;

    /** Default constructor for Jackson deserialization. */
    LogFileConfig() {}

    /** @return the log message pattern. */
    public String getPattern() {
        return pattern;
    }

    /** @return maximum log file size in MB. */
    public int getMaxFileSizeMB() {
        return maxFileSizeMB;
    }

    /** @return maximum number of backup log files. */
    public int getMaxBackupFiles() {
        return maxBackupFiles;
    }

    /**
     * Validates the log file configuration.
     * <p>
     * Checks that pattern is not null/empty, and maxFileSizeMB and maxBackupFiles are >= 1.
     * </p>
     *
     * @throws OrchestratorConfigException if any configuration rule is violated
     */
    public void validate() throws OrchestratorConfigException {
        pattern = pattern != null ? pattern.trim() : null;

        if (pattern == null || pattern.isBlank()) {
            throw new OrchestratorConfigException("LogFileConfig.pattern is missing or empty");
        }
        if (maxFileSizeMB < 1) {
            throw new OrchestratorConfigException(
                    "LogFileConfig.maxFileSizeMB must be >= 1, but was: " + maxFileSizeMB);
        }
        if (maxBackupFiles < 1) {
            throw new OrchestratorConfigException(
                    "LogFileConfig.maxBackupFiles must be >= 1, but was: " + maxBackupFiles);
        }
    }
}

