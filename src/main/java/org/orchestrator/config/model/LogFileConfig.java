package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

import java.util.Objects;

/**
 * Configuration for file-based logging in Log4j2.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogFileConfig {

    @JsonProperty("pattern")
    private final String pattern;

    @JsonProperty("maxFileSizeMB")
    private final int maxFileSizeMB;

    @JsonProperty("maxBackupFiles")
    private final int maxBackupFiles;

    /**
     * Package-private constructor for Jackson deserialization.
     */
    LogFileConfig(
            @JsonProperty("pattern") String pattern,
            @JsonProperty("maxFileSizeMB") int maxFileSizeMB,
            @JsonProperty("maxBackupFiles") int maxBackupFiles
    ) {
        this.pattern = pattern;
        this.maxFileSizeMB = maxFileSizeMB;
        this.maxBackupFiles = maxBackupFiles;
        validate();
    }

    public String getPattern() {
        return pattern;
    }

    public int getMaxFileSizeMB() {
        return maxFileSizeMB;
    }

    public int getMaxBackupFiles() {
        return maxBackupFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogFileConfig that))
            return false;

        return maxFileSizeMB == that.maxFileSizeMB &&
                maxBackupFiles == that.maxBackupFiles &&
                Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, maxFileSizeMB, maxBackupFiles);
    }

    @Override
    public String toString() {
        return "LogFileConfig{" +
                "pattern='" + pattern + '\'' +
                ", maxFileSizeMB=" + maxFileSizeMB +
                ", maxBackupFiles=" + maxBackupFiles +
                '}';
    }

    /** Validates the file log configuration. Called from constructor. */
    private void validate() {
        String trimmedPattern = pattern != null ? pattern.trim() : null;
        if (trimmedPattern == null || trimmedPattern.isBlank()) {
            throw new OrchestratorConfigException("LogFileConfig.pattern is required and cannot be empty.");
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
