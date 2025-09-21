package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.util.Objects;

/**
 * Configuration for the Jetty HTTP interface.
 * <p>
 * Represents the "http" section of the Jetty configuration in the
 * Orchestrator JSON configuration.
 * </p>
 * <p>
 * Includes the enabled flag and listening port.
 * </p>
 */
public class JettyHttpConfig {

    /** Indicates whether HTTP is enabled. Must be defined. */
    @JsonProperty("enabled")
    private final Boolean enabled;

    /** Jetty HTTP listening port. Must be between 1 and 65535 if enabled. */
    @JsonProperty("port")
    private final int port;

    private final OrchestratorLogger logger;

    /**
     * Package-private constructor for Jackson deserialization.
     * Calls private validate method to enforce constraints.
     */
    JettyHttpConfig(
            @JsonProperty(value = "enabled", required = true) Boolean enabled,
            @JsonProperty("port") int port
    ) {
        this.enabled = enabled;
        this.port = port;
        this.logger = OrchestratorLoggerManager.getLogger(this.getClass());
        validate();
    }

    /** @return true if HTTP is enabled, false otherwise */
    public boolean isEnabled() {
        return enabled;
    }

    /** @return the HTTP listening port */
    public int getHttpPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JettyHttpConfig that))
            return false;
        return port == that.port && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, port);
    }

    @Override
    public String toString() {
        return "JettyHttpConfig{" +
                "enabled=" + enabled +
                ", port=" + port +
                '}';
    }

    /** Validates the configuration. Called from constructor. */
    private void validate() {

        String message = null;
        if (enabled == null) {
            message = "Jetty HTTP 'enabled' property must be defined.";
        } else {
            if (enabled && (port <= 0 || port > 65535)) {
                message = "Jetty HTTP port must be between 1 and 65535.";
            }
        }

        if (message != null) {
            logger.error(message);
            throw new OrchestratorConfigException(message);
        }
    }
}
