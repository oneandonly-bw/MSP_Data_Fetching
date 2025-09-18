package org.orchestrator.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.config.model.FetcherControllerConfig;
import org.orchestrator.config.model.JettyConfig;
import org.orchestrator.config.model.LogConfig;
import org.orchestrator.config.model.OrchestratorConfigData;
import org.orchestrator.OrchestratorPaths;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Singleton class representing the Orchestrator configuration.
 * Loads, validates, and exposes configuration for Jetty, Log4j2, and FetcherController.
 */
public class OrchestratorConfig {

    private static volatile OrchestratorConfig instance;

    private final OrchestratorConfigData config;

    /**
     * Private constructor.
     */
    private OrchestratorConfig(OrchestratorConfigData config) {
        this.config = config;
    }

    /**
     * Returns the singleton instance of OrchestratorConfig.
     * Loads configuration from file on first call and validates it.
     *
     * @return OrchestratorConfig singleton instance
     * @throws OrchestratorConfigException if configuration cannot be read, parsed, or is invalid
     * @throws InvalidPathException        if the configuration file path is invalid
     * @throws IOException                 for I/O errors reading the configuration file
     */
    public static OrchestratorConfig getInstance() throws InvalidPathException, IOException {
        if (instance == null) {
            synchronized (OrchestratorConfig.class) {
                if (instance == null) {
                    OrchestratorPaths paths = OrchestratorPaths.getInstance();
                    Path configFilePath = paths.getConfigFilePath();
                    OrchestratorConfigData parsedConfig = loadConfig(configFilePath);
                    instance = new OrchestratorConfig(parsedConfig);
                }
            }
        }
        return instance;
    }

    public static OrchestratorConfig resetAndGetInstance()
    throws InvalidPathException, IOException {
        instance = null;
        return getInstance();
    }

    /**
     * Loads and validates the Orchestrator configuration from JSON file.
     *
     * @param configFilePath path to orchestrator_config.json
     * @return parsed and validated OrchestratorConfigData
     * @throws OrchestratorConfigException for parse, validation errors or IO failing
     *
     */
    private static OrchestratorConfigData loadConfig(Path configFilePath)
    throws OrchestratorConfigException {

        ObjectMapper mapper = new ObjectMapper();
        try {
            OrchestratorConfigData parsedConfig = mapper.readValue(
                    configFilePath.toFile(),
                    OrchestratorConfigData.class
            );

            // Delegate validation to POJOs
            parsedConfig.validate();

            return parsedConfig;

        } catch (JsonParseException e) {
            throw new OrchestratorConfigException(
                    "Malformed JSON in Orchestrator configuration file: " + configFilePath, e);

        } catch (JsonMappingException e) {
            throw new OrchestratorConfigException(
                    "Invalid Orchestrator configuration structure: " + configFilePath
                            + ". Mapping failed: " + e.getMessage(), e);

        } catch (IOException e) {
            throw new OrchestratorConfigException(
                    "Cannot read Orchestrator configuration file: " + configFilePath, e);
        }
    }

    // ===== Public getters for configuration sections =====

    /**
     * Returns Jetty configuration section.
     */
    public OrchestratorConfigData getConfig() {
        return config;
    }

    /**
     * Returns Jetty configuration.
     */
    public JettyConfig getJettyConfig() {
        return config.getJetty();
    }

    /**
     * Returns Log4j2 configuration.
     */
    public LogConfig getLog4j2Config() {
        return config.getLog4j2();
    }

    /**
     * Returns FetcherController configuration.
     */
    public FetcherControllerConfig getFetcherControllerConfig() {
        return config.getFetcherController();
    }
}
