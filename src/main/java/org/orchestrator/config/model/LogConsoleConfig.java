package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

import java.util.Objects;

/**
 * Configuration for console-based logging in Log4j2.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogConsoleConfig {

    @JsonProperty("enabled")
    private final boolean enabled;

    @JsonProperty("pattern")
    private final String pattern;

    /**
     * Package-private constructor for Jackson deserialization.
     */
    LogConsoleConfig(
            @JsonProperty("enabled") boolean enabled,
            @JsonProperty("pattern") String pattern
    ) {
        this.enabled = enabled;
        this.pattern = pattern;
        validate();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogConsoleConfig that))
            return false;

        return enabled == that.enabled && Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, pattern);
    }

    @Override
    public String toString() {
        return "LogConsoleConfig{" +
                "enabled=" + enabled +
                ", pattern='" + pattern + '\'' +
                '}';
    }

    /** Validates the console log configuration. Called from constructor. */
    private void validate() {
        if (enabled) {
            String trimmedPattern = pattern != null ? pattern.trim() : null;
            if (trimmedPattern == null || trimmedPattern.isBlank()) {
                throw new OrchestratorConfigException(
                        "LogConsoleConfig.pattern is required when console logging is enabled.");
            }
        }
    }
}
