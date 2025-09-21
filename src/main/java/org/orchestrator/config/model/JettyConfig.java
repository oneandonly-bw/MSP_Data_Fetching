package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.util.Objects;

/**
 * Configuration for the embedded Jetty server used by the Orchestrator.
 * <p>
 * This class represents the "jetty" section in the Orchestrator JSON configuration.
 * It includes host, HTTP/HTTPS settings, thread pool, accept queue size, and whitelist secret.
 * <p>
 * <b>Usage:</b> Deserialized from JSON using Jackson; automatically validated on construction.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JettyConfig {

    /** Jetty bind host (e.g., "0.0.0.0"). Cannot be null or empty. */
    @JsonProperty("host")
    private final String host;

    /** Maximum accept queue size for incoming connections. Must be > 0. */
    @JsonProperty("acceptQueueSize")
    private final int acceptQueueSize;

    /** Name of the secret in GSM storing the allowed front-end whitelist. Cannot be null or empty. */
    @JsonProperty("whitelistSecretName")
    private final String whitelistSecretName;

    /** HTTP configuration. Must be present if used. */
    @JsonProperty("http")
    private final JettyHttpConfig http;

    /** HTTPS configuration. Must be present if used. */
    @JsonProperty("https")
    private final JettyHttpsConfig https;

    /** Thread pool configuration. Must be present. */
    @JsonProperty("threadPool")
    private final JettyThreadPoolConfig threadPool;

    private final OrchestratorLogger logger;

    /**
     * Package-private constructor for Jackson deserialization.
     * <p>
     * Automatically validates the configuration after creation.
     */
    JettyConfig(
            @JsonProperty("host") String host,
            @JsonProperty("acceptQueueSize") int acceptQueueSize,
            @JsonProperty("whitelistSecretName") String whitelistSecretName,
            @JsonProperty("http") JettyHttpConfig http,
            @JsonProperty("https") JettyHttpsConfig https,
            @JsonProperty("threadPool") JettyThreadPoolConfig threadPool) {

        this.host = host != null ? host.trim() : null;
        this.acceptQueueSize = acceptQueueSize;
        this.whitelistSecretName = whitelistSecretName != null ? whitelistSecretName.trim() : null;
        this.http = http;
        this.https = https;
        this.threadPool = threadPool;
        this.logger = OrchestratorLoggerManager.getLogger(this.getClass());

        validate();
    }

    public String getHost() {
        return host;
    }

    public int getAcceptQueueSize() {
        return acceptQueueSize;
    }

    public String getWhitelistSecretName() {
        return whitelistSecretName;
    }

    public JettyHttpConfig getHttp() {
        return http;
    }

    public JettyHttpsConfig getHttps() {
        return https;
    }

    public JettyThreadPoolConfig getThreadPool() {
        return threadPool;
    }

    public int getHttpPort() {
        return http != null ? http.getHttpPort() : -1;
    }

    public int getHttpsPort() {
        return https != null ? https.getHttpsPort() : -1;
    }

    /**
     * Validates the Jetty configuration.
     * <p>
     * Performs the following checks:
     * <ul>
     *     <li>Host is not null/blank</li>
     *     <li>Accept queue size is > 0</li>
     *     <li>Whitelist secret name is not null/blank</li>
     *     <li>At least one of HTTP or HTTPS configuration is defined</li>
     *     <li>HTTP and HTTPS configurations are valid if present</li>
     *     <li>HTTP and HTTPS ports do not conflict</li>
     *     <li>Thread pool configuration is present and valid</li>
     * </ul>
     *
     * @throws OrchestratorConfigException if any configuration rule is violated
     */
    private void validate() {

        String message =  null;

        if (host == null || host.isBlank()) {
            message = "Jetty host is missing or empty.";
        } else if (whitelistSecretName == null || whitelistSecretName.isBlank()) {
            message = "Jetty whitelistSecretName is missing or empty.";
        } else if (acceptQueueSize <= 0) {
            message = "Jetty acceptQueueSize must be greater than 0.";
        } else if (http == null && https == null) {
            throw new OrchestratorConfigException("At least one of 'http' or 'https' section must be defined.");
        } else if (http != null && https != null && getHttpPort() == getHttpsPort()) {
            message = "HTTP port must not be the same as HTTPS port.";
        } else if (threadPool == null) {
            message = "Jetty threadPool configuration is missing.";
        }

        if (message != null) {
            logger.error(message);
            throw new OrchestratorConfigException(message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JettyConfig that))
            return false;
        return acceptQueueSize == that.acceptQueueSize &&
                Objects.equals(host, that.host) &&
                Objects.equals(whitelistSecretName, that.whitelistSecretName) &&
                Objects.equals(http, that.http) &&
                Objects.equals(https, that.https) &&
                Objects.equals(threadPool, that.threadPool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, acceptQueueSize,
                whitelistSecretName, http, https, threadPool);
    }

    @Override
    public String toString() {
        return "JettyConfig{" +
                "host='" + host + '\'' +
                ", acceptQueueSize=" + acceptQueueSize +
                ", whitelistSecretName=" +
                    (whitelistSecretName != null ? "****" : "null") + '\'' +
                ", http=" + http.toString() +
                ", https=" + https.toString() +
                ", threadPool=" + threadPool.toString() +
                '}';
    }
}
