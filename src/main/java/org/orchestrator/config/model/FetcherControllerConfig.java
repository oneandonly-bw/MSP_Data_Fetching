package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Represents the configuration for the Fetcher Controller.
 * Controls concurrency, IPC ports, queue size, and request handling timeouts.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetcherControllerConfig {

    @JsonProperty("maxConcurrentFetchers")
    private int maxConcurrentFetchers;

    @JsonProperty("queueSize")
    private int queueSize;

    @JsonProperty("handshakeTokenLength")
    private int handshakeTokenLength;

    @JsonProperty("ipcPortBase")
    private int ipcPortBase;

    @JsonProperty("requestTimeoutSec")
    private int requestTimeoutSec;

    @JsonProperty("restartTimeoutMinutes")
    private int restartTimeoutMinutes;

    /**
     * Package-private constructor required by Jackson for deserialization.
     */
    FetcherControllerConfig() {}

    /**
     * Returns the maximum number of fetchers that can run concurrently.
     */
    public int getMaxConcurrentFetchers() {
        return maxConcurrentFetchers;
    }

    /**
     * Returns the size of the request queue.
     */
    public int getQueueSize() {
        return queueSize;
    }

    /**
     * Returns the length of the handshake token.
     */
    public int getHandshakeTokenLength() {
        return handshakeTokenLength;
    }

    /**
     * Returns the base port for IPC communication.
     */
    public int getIpcPortBase() {
        return ipcPortBase;
    }

    /**
     * Returns the request timeout in seconds.
     */
    public int getRequestTimeoutSec() {
        return requestTimeoutSec;
    }

    /**
     * Returns the restart timeout in minutes.
     */
    public int getRestartTimeoutMinutes() {
        return restartTimeoutMinutes;
    }

    /**
     * Validates the Fetcher Controller configuration.
     * Ensures all numeric fields are within acceptable ranges.
     *
     * @throws OrchestratorConfigException if any configuration value is invalid
     */
    public void validate() {
        if (maxConcurrentFetchers < 1) {
            throw new OrchestratorConfigException(
                    "FetcherControllerConfig.maxConcurrentFetchers must be >= 1");
        }
        if (queueSize < 1) {
            throw new OrchestratorConfigException(
                    "FetcherControllerConfig.queueSize must be >= 1");
        }
        if (handshakeTokenLength < 1) {
            throw new OrchestratorConfigException(
                    "FetcherControllerConfig.handshakeTokenLength must be >= 1");
        }
        if (ipcPortBase < 1024 || ipcPortBase > 65535) {
            throw new OrchestratorConfigException(
                    "FetcherControllerConfig.ipcPortBase must be in range 1024-65535");
        }
        if (requestTimeoutSec < 1) {
            throw new OrchestratorConfigException(
                    "FetcherControllerConfig.requestTimeoutSec must be >= 1");
        }
        if (restartTimeoutMinutes < 1) {
            throw new OrchestratorConfigException(
                    "FetcherControllerConfig.restartTimeoutMinutes must be >= 1");
        }
    }
}
