package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.util.Objects;

/**
 * Represents the full Orchestrator configuration.
 * <p>
 * This is the root configuration object that maps directly from
 * the {@code orchestrator_config.json} file.
 * It contains sections for:
 * <ul>
 *     <li>Jetty server configuration</li>
 *     <li>Log4j2 logging configuration</li>
 *     <li>Fetcher Controller configuration</li>
 * </ul>
 * <p>
 * <b>Usage:</b> Deserialize from JSON using Jackson, then call {@link #validate()}
 * to ensure the configuration is valid and complete.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrchestratorConfigData {

    /** Configuration for the embedded Jetty server. */
    @JsonProperty("jetty")
    private final JettyConfig jetty;

    /** Configuration for Log4j2 logging. */
    @JsonProperty("log4j2")
    private final LogConfig log4j2;

    /** Configuration for the Fetcher Controller subsystem. */
    @JsonProperty("fetcherController")
    private final FetcherControllerConfig fetcherController;

    private final OrchestratorLogger logger;

    /**
     * Package-private no-argument constructor required by Jackson
     * for JSON deserialization.
     */
    OrchestratorConfigData(
            @JsonProperty("jetty") JettyConfig jettyConfig,
            @JsonProperty("log4j2") LogConfig log4j2,
            @JsonProperty("fetcherController") FetcherControllerConfig fetcherController
    ) {
        this.jetty = jettyConfig;
        this.log4j2 = log4j2;
        this.fetcherController = fetcherController;
        this.logger = OrchestratorLoggerManager.getLogger(this.getClass());
        validate();
    }



    /**
     * Returns the Jetty server configuration.
     *
     * @return the {@link JettyConfig} instance
     */
    public JettyConfig getJetty() {
        return jetty;
    }

    /**
     * Returns the Log4j2 logging configuration.
     *
     * @return the {@link LogConfig} instance
     */
    public LogConfig getLog4j2() {
        return log4j2;
    }

    /**
     * Returns the Fetcher Controller configuration.
     *
     * @return the {@link FetcherControllerConfig} instance
     */
    public FetcherControllerConfig getFetcherController() {
        return fetcherController;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrchestratorConfigData that = (OrchestratorConfigData) o;
        return Objects.equals(jetty, that.jetty)
                && Objects.equals(log4j2, that.log4j2)
                && Objects.equals(fetcherController, that.fetcherController);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jetty, log4j2, fetcherController);
    }

    @Override
    public String toString() {
        return "OrchestratorConfigData{" +
                "jetty=" + jetty.toString() +
                ", log4j2=" + log4j2.toString() +
                ", fetcherController=" + fetcherController.toString() +
                '}';
    }

    /**
     * Validates the Orchestrator configuration.
     * <p>
     * Ensures that all required configuration sections are present
     * and delegates validation to the sub-configuration objects.
     *
     * @throws OrchestratorConfigException if any required section is missing
     *                                      or if sub-config validation fails
     */
    private void validate() {

        String message = null;

        if (jetty == null) {
            message = "Jetty configuration section is missing.";
        } else  if (log4j2 == null) {
            message = "Log4j2 configuration section is missing.";
        } else if (fetcherController == null) {
           message = "FetcherController configuration section is missing.";
        }

        if (message != null) {
            logger.error(message);
            throw new OrchestratorConfigException(message);
        }

    }
}
