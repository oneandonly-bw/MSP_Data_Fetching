package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.util.Objects;

/**
 * Configuration for the Jetty thread pool.
 * <p>
 * Includes pool type, min/max threads, and idle timeout.
 */
public class JettyThreadPoolConfig {

    /** Type of thread pool. Valid values: "fixed" or "dynamic". */
    @JsonProperty("type")
    private final String type;

    /** Maximum number of threads in the pool. Must be >= 1. */
    @JsonProperty("maxThreads")
    private final int maxThreads;

    /** Minimum number of threads in the pool. Must be >= 1. */
    @JsonProperty("minThreads")
    private final int minThreads;

    /** Idle timeout in seconds for threads. Must be >= 1. */
    @JsonProperty("idleTimeoutSec")
    private final int idleTimeoutSec;

    private final OrchestratorLogger logger;

    /**
     * Package-private constructor for Jackson deserialization.
     * Calls private validate method.
     */
    JettyThreadPoolConfig(
            @JsonProperty(value = "type", required = true) String type,
            @JsonProperty("maxThreads") int maxThreads,
            @JsonProperty("minThreads") int minThreads,
            @JsonProperty("idleTimeoutSec") int idleTimeoutSec
    ) {
        this.type = type;
        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
        this.idleTimeoutSec = idleTimeoutSec;
        this.logger = OrchestratorLoggerManager.getLogger(this.getClass());
        validate();
    }

    public String getType() {
        return type;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTimeoutSec() {
        return idleTimeoutSec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JettyThreadPoolConfig that))
            return false;

        return maxThreads == that.maxThreads &&
                minThreads == that.minThreads &&
                idleTimeoutSec == that.idleTimeoutSec &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, maxThreads, minThreads, idleTimeoutSec);
    }

    @Override
    public String toString() {
        return "JettyThreadPoolConfig{" +
                "type='" + type + '\'' +
                ", maxThreads=" + maxThreads +
                ", minThreads=" + minThreads +
                ", idleTimeoutSec=" + idleTimeoutSec +
                '}';
    }

    /** Validates the thread pool configuration. Called from constructor. */
    private void validate() {

        String message = null;

        if (type == null || type.isBlank()) {
            message = "Jetty thread pool type must be defined and not empty.";
        } else if (!type.equals("fixed") && !type.equals("dynamic")) {
            message = "Jetty thread pool type must be 'fixed' or 'dynamic', but was: " + type;
        } else if (maxThreads < 1) {
            message = "Jetty thread pool maxThreads must be >= 1, but was: " + maxThreads;
        } else if (minThreads < 1) {
            message = "Jetty thread pool minThreads must be >= 1, but was: " + minThreads;
        } else if (minThreads > maxThreads) {
            message ="Jetty thread pool minThreads cannot be greater than maxThreads.";
        } else if (idleTimeoutSec < 1) {
            message = "Jetty thread pool idleTimeoutSec must be >= 1, but was: " + idleTimeoutSec;
        }

        if (message != null) {
            logger.error (message);
            throw new OrchestratorConfigException(message);
        }
    }
}
