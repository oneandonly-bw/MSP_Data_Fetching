package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Configuration for the embedded Jetty server used by the Orchestrator.
 * <p>
 * This class represents the "jetty" section in the Orchestrator JSON configuration.
 * It includes host, port, thread pool, HTTPS settings, accept queue size, and whitelist.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JettyConfig {

    /** Jetty bind host (e.g., "0.0.0.0"). Cannot be null or empty. */
    @JsonProperty("host")
    private String host;

    /** Jetty listen port. Must be between 1 and 65535. */
    @JsonProperty("port")
    private int port;

    /** Maximum accept queue size for incoming connections. */
    @JsonProperty("acceptQueueSize")
    private int acceptQueueSize;

    /** Name of the secret in GSM storing the allowed front-end whitelist. Cannot be null or empty. */
    @JsonProperty("whitelistSecretName")
    private String whitelistSecretName;

    /** HTTPS configuration. Must be present, but may be disabled via `enabled` flag. */
    @JsonProperty("https")
    private JettyHttpsConfig https;

    /** Thread pool configuration. Must be present. */
    @JsonProperty("threadPool")
    private JettyThreadPoolConfig threadPool;

    /** Default constructor for Jackson deserialization. */
    JettyConfig() {}

    /** @return the Jetty host. */
    public String getHost() {
        return host;
    }

    /** @return the Jetty port. */
    public int getPort() {
        return port;
    }

    /** @return the maximum accept queue size. */
    public int getAcceptQueueSize() {
        return acceptQueueSize;
    }

    /** @return the HTTPS configuration object. */
    public JettyHttpsConfig getHttps() {
        return https;
    }

    /** @return the thread pool configuration object. */
    public JettyThreadPoolConfig getThreadPool() {
        return threadPool;
    }

    /** @return the name of the whitelist secret in GSM. */
    public String getWhitelistSecretName() {
        return whitelistSecretName;
    }

    /**
     * Validates the Jetty configuration.
     * <p>
     * Performs the following checks:
     * <ul>
     *     <li>Host is not null/blank (trimmed).</li>
     *     <li>Port is within 1–65535.</li>
     *     <li>Whitelist secret name is not null/blank (trimmed).</li>
     *     <li>Thread pool configuration is present and valid.</li>
     *     <li>HTTPS configuration is present and valid.</li>
     * </ul>
     * </p>
     *
     * @throws OrchestratorConfigException if any configuration rule is violated
     */
    public void validate() throws OrchestratorConfigException {

        // Trim string fields
        host = host != null ? host.trim() : null;
        whitelistSecretName = whitelistSecretName != null ? whitelistSecretName.trim() : null;

        if (host == null || host.isBlank()) {
            throw new OrchestratorConfigException("Jetty host is missing or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new OrchestratorConfigException("Jetty port must be in range 1-65535");
        }
        if (whitelistSecretName == null || whitelistSecretName.isBlank()) {
            throw new OrchestratorConfigException("Jetty whitelistSecretName is missing or empty");
        }
        if (threadPool == null) {
            throw new OrchestratorConfigException("Jetty threadPool configuration is missing");
        }
        threadPool.validate();

        if (https == null) {
            throw new OrchestratorConfigException("Jetty https configuration is missing");
        }
        https.validate();
    }
}
