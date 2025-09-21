package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.util.Objects;

/**
 * Represents the configuration for the Fetcher Controller.
 * <p>
 * Controls concurrency, IPC ports, request queue size, handshake tokens,
 * request timeouts, and restart timeouts.
 * <p>
 * <b>Usage:</b> This object is validated automatically during construction.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetcherControllerConfig {

    /**
     * Maximum number of fetchers that can run concurrently. Must be >= 1.
     */
    @JsonProperty("maxConcurrentFetchers")
    private final int maxConcurrentFetchers;

    /**
     * Size of the request queue. Must be >= 1.
     */
    @JsonProperty("queueSize")
    private final int queueSize;

    /**
     * Length of the handshake token. Must be >= 1.
     */
    @JsonProperty("handshakeTokenLength")
    private final int handshakeTokenLength;

    /**
     * Base port for IPC communication. Must be 1024–65535.
     */
    @JsonProperty("ipcPortBase")
    private final int ipcPortBase;

    /**
     * Request timeout in seconds. Must be >= 1.
     */
    @JsonProperty("requestTimeoutSec")
    private final int requestTimeoutSec;

    /**
     * Restart timeout in minutes. Must be >= 1.
     */
    @JsonProperty("restartTimeoutMinutes")
    private final int restartTimeoutMinutes;

    private final OrchestratorLogger logger;

    /**
     * Package-private constructor for Jackson deserialization.
     * <p>
     * Automatically validates the configuration after creation.
     */
    FetcherControllerConfig(@JsonProperty("maxConcurrentFetchers") int maxConcurrentFetchers,
                            @JsonProperty("queueSize") int queueSize,
                            @JsonProperty("handshakeTokenLength") int handshakeTokenLength,
                            @JsonProperty("ipcPortBase") int ipcPortBase,
                            @JsonProperty("requestTimeoutSec") int requestTimeoutSec,
                            @JsonProperty("restartTimeoutMinutes") int restartTimeoutMinutes)
    {
        this.maxConcurrentFetchers = maxConcurrentFetchers;
        this.queueSize = queueSize;
        this.handshakeTokenLength = handshakeTokenLength;
        this.ipcPortBase = ipcPortBase;
        this.requestTimeoutSec = requestTimeoutSec;
        this.restartTimeoutMinutes = restartTimeoutMinutes;
        this.logger = OrchestratorLoggerManager.getLogger(this.getClass());
        validate();
    }

    /**
     * @return maximum number of concurrent fetchers
     */
    public int getMaxConcurrentFetchers() {
        return maxConcurrentFetchers;
    }

    /**
     * @return size of the request queue
     */
    public int getQueueSize() {
        return queueSize;
    }

    /**
     * @return handshake token length
     */
    public int getHandshakeTokenLength() {
        return handshakeTokenLength;
    }

    /**
     * @return base IPC port
     */
    public int getIpcPortBase() {
        return ipcPortBase;
    }

    /**
     * @return request timeout in seconds
     */
    public int getRequestTimeoutSec() {
        return requestTimeoutSec;
    }

    /**
     * @return restart timeout in minutes
     */
    public int getRestartTimeoutMinutes() {
        return restartTimeoutMinutes;
    }

    /**
     * Validates the Fetcher Controller configuration.
     * <p>
     * Checks numeric fields for allowed ranges.
     * <p>
     * Called automatically from the constructor.
     *
     * @throws OrchestratorConfigException if any configuration value is invalid
     */
    private void validate() {

        String message = null;
        if (maxConcurrentFetchers < 1) {
            message = "FetcherControllerConfig.maxConcurrentFetchers must be >= 1.";
        } else if (queueSize < 1) {
            message = "FetcherControllerConfig.queueSize must be >= 1.";
        } else if (handshakeTokenLength < 1) {
            message = "FetcherControllerConfig.handshakeTokenLength must be >= 1.";
        } else if (ipcPortBase < 1024 || ipcPortBase > 65535) {
            message = "FetcherControllerConfig.ipcPortBase must be in range 1024–65535.";
        } else if (requestTimeoutSec < 1) {
            message = "FetcherControllerConfig.requestTimeoutSec must be >= 1.";
        } else if (restartTimeoutMinutes < 1) {
            message = "FetcherControllerConfig.requestTimeoutSec must be >= 1.";
        }

        if (message != null) {
            logger.error(message);
            throw new OrchestratorConfigException(message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FetcherControllerConfig that)) return false;
        return maxConcurrentFetchers == that.maxConcurrentFetchers &&
                queueSize == that.queueSize &&
                handshakeTokenLength == that.handshakeTokenLength &&
                ipcPortBase == that.ipcPortBase &&
                requestTimeoutSec == that.requestTimeoutSec &&
                restartTimeoutMinutes == that.restartTimeoutMinutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxConcurrentFetchers, queueSize, handshakeTokenLength,
                ipcPortBase, requestTimeoutSec, restartTimeoutMinutes);
    }

    @Override
    public String toString() {
        return "FetcherControllerConfig{" +
                "maxConcurrentFetchers=" + maxConcurrentFetchers +
                ", queueSize=" + queueSize +
                ", handshakeTokenLength=" + handshakeTokenLength +
                ", ipcPortBase=" + ipcPortBase +
                ", requestTimeoutSec=" + requestTimeoutSec +
                ", restartTimeoutMinutes=" + restartTimeoutMinutes + '}';
    }
}
