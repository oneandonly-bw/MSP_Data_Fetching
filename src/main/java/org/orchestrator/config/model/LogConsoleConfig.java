package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Configuration for console-based logging in Log4j2.
 * <p>
 * Represents the "console" section under "log4j2" in the Orchestrator JSON configuration.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogConsoleConfig {

    /** Whether console logging is enabled. */
    @JsonProperty("enabled")
    private boolean enabled;

    /** Log message pattern. Cannot be null or empty when enabled. */
    @JsonProperty("pattern")
    private String pattern;

    /** Default constructor for Jackson deserialization. */
    LogConsoleConfig() {}

    /** @return true if console logging is enabled. */
    public boolean isEnabled() {
        return enabled;
    }

    /** @return the log message pattern. */
    public String getPattern() {
        return pattern;
    }

    /**
     * Validates the console log configuration.
     * <p>
     * If console logging is enabled, the pattern must not be null or empty.
     * </p>
     *
     * @throws OrchestratorConfigException if configuration is invalid
     */
    public void validate() throws OrchestratorConfigException {
        if (enabled) {
            pattern = pattern != null ? pattern.trim() : null;
            if (pattern == null || pattern.isBlank()) {
                throw new OrchestratorConfigException(
                        "LogConsoleConfig.pattern is missing or empty while console logging is enabled");
            }
        }
    }
}

