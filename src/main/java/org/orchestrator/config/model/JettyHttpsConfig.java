package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.util.Objects;

/**
 * Configuration for the HTTPS interface in the Jetty server.
 * <p>
 * Includes enabled flag, listening port, and GSM secret name for HTTPS credentials.
 */
public class JettyHttpsConfig {

    /** Indicates whether HTTPS is enabled. Must be defined. */
    @JsonProperty("enabled")
    private final Boolean enabled;

    /** Jetty HTTPS listening port. Must be between 1 and 65535 if enabled. */
    @JsonProperty("port")
    private final int port;

    /** Name of the secret in GSM containing HTTPS credentials. Required if enabled. */
    @JsonProperty("httpsSecretName")
    private final String httpsSecretName;

    private final OrchestratorLogger logger;

    /**
     * Package-private constructor for Jackson deserialization.
     * Calls private validate method to enforce constraints.
     */
    JettyHttpsConfig(
            @JsonProperty(value = "enabled", required = true) Boolean enabled,
            @JsonProperty("port") int port,
            @JsonProperty("httpsSecretName") String httpsSecretName
    ) {
        this.enabled = enabled;
        this.port = port;
        this.httpsSecretName = httpsSecretName;
        this.logger = OrchestratorLoggerManager.getLogger(this.getClass());
        validate();
    }

    /** @return true if HTTPS is enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /** @return HTTPS listening port */
    public int getHttpsPort() {
        return port;
    }

    /** @return HTTPS secret name */
    public String getHttpsSecretName() {
        return httpsSecretName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JettyHttpsConfig that))
            return false;

        return port == that.port
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(httpsSecretName, that.httpsSecretName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, port, httpsSecretName);
    }

    @Override
    public String toString() {
        return "JettyHttpsConfig{" +
                "enabled=" + enabled +
                ", port=" + port +
                ", httpsSecretName='" + (httpsSecretName != null ? "****" : null) + '\'' +
                '}';
    }

    /** Validates the configuration. Called from constructor. */
    private void validate() {

         String message = null;

        if (enabled == null) {
            message = "Jetty HTTPS 'enabled' property must be defined.";
        } else if (enabled) {
            if (port <= 0 || port > 65535) {
                message = "Jetty HTTPS port must be between 1 and 65535.";
            } else if (httpsSecretName == null || httpsSecretName.isBlank()) {
                message = "HTTPS secret name must be provided when HTTPS is enabled.";
            }
        }

        if (message != null) {
            logger.error(message);
            throw new OrchestratorConfigException (message);
        }
    }
}
