package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Configuration for Jetty thread pool used by the Orchestrator.
 * <p>
 * This class represents the thread pool configuration section in the
 * orchestrator JSON configuration.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JettyThreadPoolConfig {

    /**
     * Type of thread pool. Valid values: "fixed" or "dynamic".
     */
    @JsonProperty("type")
    private String type;

    /**
     * Maximum number of threads in the pool. Must be >= 1.
     */
    @JsonProperty("maxThreads")
    private int maxThreads;

    /**
     * Minimum number of threads in the pool. Must be >= 1.
     */
    @JsonProperty("minThreads")
    private int minThreads;

    /**
     * Idle timeout in seconds for threads. Must be >= 1.
     */
    @JsonProperty("idleTimeoutSec")
    private int idleTimeoutSec;

    /**
     * Default constructor for Jackson deserialization.
     */
    JettyThreadPoolConfig() {
    }

    /**
     * @return the type of thread pool ("fixed" or "dynamic").
     */
    public String getType() {
        return type;
    }

    /**
     * @return the maximum number of threads.
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * @return the minimum number of threads.
     */
    public int getMinThreads() {
        return minThreads;
    }

    /**
     * @return the idle timeout in seconds.
     */
    public int getIdleTimeoutSec() {
        return idleTimeoutSec;
    }

    /**
     * Validates the configuration values.
     * <p>
     * Checks that all fields have valid values:
     * - type must be "fixed" or "dynamic"
     * - minThreads and maxThreads must be >= 1
     * - minThreads <= maxThreads
     * - idleTimeoutSec >= 1
     * </p>
     *
     * @throws OrchestratorConfigException if any validation rule is violated
     */
    public void validate() throws OrchestratorConfigException {
        // Check type
        if (type == null || type.isBlank()) {
            throw new OrchestratorConfigException("JettyThreadPoolConfig.type is missing or empty");
        }
        if (!type.equals("fixed") && !type.equals("dynamic")) {
            throw new OrchestratorConfigException("JettyThreadPoolConfig.type must be 'fixed' or 'dynamic', but was: " + type);
        }

        // Check maxThreads
        if (maxThreads < 1) {
            throw new OrchestratorConfigException("JettyThreadPoolConfig.maxThreads must be >= 1, but was: " + maxThreads);
        }

        // Check minThreads
        if (minThreads < 1) {
            throw new OrchestratorConfigException("JettyThreadPoolConfig.minThreads must be >= 1, but was: " + minThreads);
        }

        // Ensure minThreads <= maxThreads
        if (minThreads > maxThreads) {
            throw new OrchestratorConfigException("JettyThreadPoolConfig.minThreads cannot be greater than maxThreads");
        }

        // Check idleTimeoutSec
        if (idleTimeoutSec < 1) {
            throw new OrchestratorConfigException("JettyThreadPoolConfig.idleTimeoutSec must be >= 1, but was: " + idleTimeoutSec);
        }
    }
}
