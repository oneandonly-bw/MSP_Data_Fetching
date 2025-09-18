package org.orchestrator.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.orchestrator.config.exception.OrchestratorConfigException;

/**
 * Represents the full Orchestrator configuration.
 * Contains sections for Jetty, Log4j2, and FetcherController.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrchestratorConfigData {

    @JsonProperty("jetty")
    private JettyConfig jetty;

    @JsonProperty("log4j2")
    private LogConfig log4j2;

    @JsonProperty("fetcherController")
    private FetcherControllerConfig fetcherController;

    /**
     * Package-private constructor required by Jackson for deserialization.
     */
    OrchestratorConfigData() {}

    /**
     * Returns the Jetty configuration.
     */
    public JettyConfig getJetty() {
        return jetty;
    }

    /**
     * Returns the Log4j2 configuration.
     */
    public LogConfig getLog4j2() {
        return log4j2;
    }

    /**
     * Returns the Fetcher Controller configuration.
     */
    public FetcherControllerConfig getFetcherController() {
        return fetcherController;
    }

    /**
     * Validates the full Orchestrator configuration.
     * Ensures all sections are present and delegates validation to sub-POJOs.
     *
     * @throws OrchestratorConfigException if any configuration section is missing or invalid
     */
    public void validate() {
        if (jetty == null) {
            throw new OrchestratorConfigException("Jetty configuration section is missing");
        }
        jetty.validate();

        if (log4j2 == null) {
            throw new OrchestratorConfigException("Log4j2 configuration section is missing");
        }
        log4j2.validate();

        if (fetcherController == null) {
            throw new OrchestratorConfigException("FetcherController configuration section is missing");
        }
        fetcherController.validate();
    }
}
