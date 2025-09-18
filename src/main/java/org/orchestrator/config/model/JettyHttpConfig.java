package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

public class JettyHttpConfig {

    /** Indicates whether HTTP is enabled or disabled. */
    @JsonProperty("enabled")
    private Boolean enabled;

    /** Jetty HTTP listen port. Must be between 1 and 65535. */
    @JsonProperty("port")
    private int port;


    /** Default constructor for Jackson deserialization. */
    JettyHttpConfig() {}

    /** @return true if HTTPS is enabled, false otherwise. */
    public boolean isEnabled() {
        return enabled;
    }

    public int getHttpPort() {
        return port;
    }


    /**
     * Validates the HTTP configuration.
     *
     * @throws OrchestratorConfigException if validation rules are violated
     */
    public void validate() throws OrchestratorConfigException {

        if (enabled == null) {
            throw new OrchestratorConfigException("Jetty HTTP 'enabled' field must be present");
        }

        if (enabled) {
            if (port <= 0 || port > 65535) {
                throw new OrchestratorConfigException("Jetty HTTP port must be in range 1-65535");
            }
        }
    }
}
