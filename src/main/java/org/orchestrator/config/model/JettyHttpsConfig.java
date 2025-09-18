package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Configuration for HTTPS in Jetty server used by the Orchestrator.
 * <p>
 * Represents the HTTPS configuration section of the orchestrator JSON configuration.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JettyHttpsConfig {

    /** Indicates whether HTTPS is enabled or disabled. */
    @JsonProperty("enabled")
    private boolean enabled;

    /** Name of the secret in GSM containing HTTPS credentials. Required if enabled. */
    @JsonProperty("httpsSecretName")
    private String httpsSecretName;

    /** Default constructor for Jackson deserialization. */
    JettyHttpsConfig() {}

    /** @return true if HTTPS is enabled, false otherwise. */
    public boolean isEnabled() {
        return enabled;
    }

    /** @return the name of the secret containing HTTPS credentials. */
    public String getHttpsSecretName() {
        return httpsSecretName;
    }

    /**
     * Validates the HTTPS configuration.
     * <p>
     * If HTTPS is enabled, httpsSecretName must not be null or blank.
     * </p>
     *
     * @throws OrchestratorConfigException if validation rules are violated
     */
    public void validate() throws OrchestratorConfigException{
        if (enabled) {
            if (httpsSecretName == null || httpsSecretName.isBlank()) {
                throw new OrchestratorConfigException(
                        "JettyHttpsConfig.httpsSecretName must be provided when HTTPS is enabled");
            }
        }
    }
}

