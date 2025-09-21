package org.orchestrator.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orchestrator.config.exception.OrchestratorConfigException;
import org.orchestrator.config.exception.OrchestratorConfigReloadException;
import org.orchestrator.config.model.FetcherControllerConfig;
import org.orchestrator.config.model.JettyConfig;
import org.orchestrator.config.model.LogConfig;
import org.orchestrator.config.model.OrchestratorConfigData;
import org.orchestrator.OrchestratorPaths;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Singleton class representing the Orchestrator configuration.
 *
 * <p>Provides access to configuration sections: JettyConfig, LogConfig, FetcherControllerConfig.
 * Thread-safe reads and reloads via AtomicReference. Access before initialization throws IllegalStateException.
 */
public class OrchestratorConfig {

    /** Singleton instance */
    private static volatile OrchestratorConfig instance;

    /** Fixed path to the configuration file (stored once at init) */
    private final Path configFilePath;

    /** Configuration data; AtomicReference ensures thread-safe atomic swaps */
    private final AtomicReference<OrchestratorConfigData> configRef;

    /**
     * Private constructor using loaded configuration.
     *
     * @param config fully loaded and validated OrchestratorConfigData
     */
    private OrchestratorConfig(OrchestratorConfigData config, Path configFilePath) {
        this.configRef = new AtomicReference<>(config);
        this.configFilePath = configFilePath;
    }

    // ======= Initialization Methods =======

    /**
     * Initializes the singleton by loading the configuration from file.
     * Safe to call multiple times; subsequent calls do nothing.
     */
    public static synchronized void init() throws OrchestratorConfigException, IOException {
        if (instance == null) {
            Path configFilePath = OrchestratorPaths.getInstance().getConfigFilePath();
            OrchestratorConfigData parsedConfig = loadConfig(configFilePath);
            instance = new OrchestratorConfig(parsedConfig, configFilePath);
        }
    }

    /**
     * Returns the singleton instance.
     *
     * @throws IllegalStateException if init() was not called yet
     */
    public static OrchestratorConfig getInstance()
    throws IllegalStateException {
        if (instance == null || instance.configRef.get() == null) {
            throw new IllegalStateException("OrchestratorConfig not initialized. Call init() first.");
        }
        return instance;
    }

    /**
     * Reloads the configuration from the stored config file path.
     *
     * <p>If reload fails, old configuration remains active.
     */
    public static void reInit()
    throws OrchestratorConfigReloadException {
        OrchestratorConfig cfg = getInstance();

        try {
            OrchestratorConfigData newConfig = loadConfig(cfg.configFilePath);
            cfg.configRef.set(newConfig); // atomic swap
        } catch (OrchestratorConfigException | IOException e) {
            throw new OrchestratorConfigReloadException(
                    "Reinitialization of OrchestratorConfig failed. System continues with old config.", e
            );
        }
    }

    // ======= Internal Loading =======

    private static OrchestratorConfigData loadConfig(Path configFilePath)
    throws OrchestratorConfigException, IOException {

        ObjectMapper mapper = new ObjectMapper();

        try {
            return  mapper.readValue(
                    configFilePath.toFile(),
                    OrchestratorConfigData.class
            );

        } catch (JsonParseException e) {
            throw new OrchestratorConfigException(
                    "Malformed JSON in Orchestrator configuration file: " + configFilePath, e);

        } catch (JsonMappingException e) {
            throw new OrchestratorConfigException(
                    "Invalid Orchestrator configuration structure: " + configFilePath
                            + ". Mapping failed: " + e.getMessage(), e);
        }
    }

    // ======= Getters with Initialization Check =======

    private void ensureInitialized() {
        if (configRef.get() == null) {
            throw new IllegalStateException("OrchestratorConfig not initialized. Call init() first.");
        }
    }

    /** Returns the full configuration data. */
    public OrchestratorConfigData getConfig() {
        ensureInitialized();
        return configRef.get();
    }

    /** Returns the Jetty configuration section. */
    public JettyConfig getJettyConfig() {
        ensureInitialized();
        return configRef.get().getJetty();
    }

    /** Returns the Log4j2 configuration section. */
    public LogConfig getLog4j2Config() {
        ensureInitialized();
        return configRef.get().getLog4j2();
    }

    /** Returns the FetcherController configuration section. */
    public FetcherControllerConfig getFetcherControllerConfig() {
        ensureInitialized();
        return configRef.get().getFetcherController();
    }
}
