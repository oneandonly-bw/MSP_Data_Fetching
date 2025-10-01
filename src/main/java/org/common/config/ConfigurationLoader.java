package org.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.common.exception.ConfigurationLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Singleton loader for JSON configuration files.
 * <p>
 * Supports initialization from a config file and reload (stub).
 * Throws {@link ConfigurationLoadException} on failure.
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 * ConfigurationLoader.init(Paths.get("/path/to/config.json"));
 * JsonNode config = ConfigurationLoader.getInstance().getConfig();
 * }</pre>
 */
public final class ConfigurationLoader {

    private static volatile ConfigurationLoader instance;
    private final ObjectMapper mapper;
    private JsonNode config;

    private ConfigurationLoader() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Initialize the singleton instance with the given config file.
     *
     * @param pathToConfigFile absolute path to JSON config file
     * @throws IllegalStateException          if already initialized
     * @throws ConfigurationLoadException     if file cannot be read or parsed
     */
    public static synchronized void init(Path pathToConfigFile) throws ConfigurationLoadException {
        if (instance != null) {
            throw new IllegalStateException("ConfigurationLoader is already initialized");
        }
        ConfigurationLoader loader = new ConfigurationLoader();
        loader.config = loader.loadInternal(pathToConfigFile);
        instance = loader;
    }

    /**
     * Get the singleton instance.
     *
     * @return ConfigurationLoader instance
     * @throws IllegalStateException if init() was not called
     */
    public static ConfigurationLoader getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigurationLoader is not initialized. Call init(path) first.");
        }
        return instance;
    }

    /**
     * Get the loaded configuration.
     *
     * @return JsonNode representing the configuration
     */
    public JsonNode getConfig() {
        return config;
    }

    public JsonNode getLogConfig() {
        if (config.hasNonNull("log4j2")) {
            return config.get("log4j2");
        } else {
            return null;
        }
    }

    /**
     * Stub for reloading configuration (can be extended later).
     *
     * @throws ConfigurationLoadException on load failure
     */
    public JsonNode reLoad() throws ConfigurationLoadException {
        if (config == null) {
            throw new IllegalStateException("ConfigurationLoader not initialized");
        }
        // Currently just returns existing config, can implement reloading from file later
        return config;
    }

    // ----------------------------
    // Internal helpers
    // ----------------------------

    private JsonNode loadInternal(Path path) throws ConfigurationLoadException {
        validateFile(path);
        try {
            return mapper.readTree(Files.newInputStream(path));
        } catch (IOException e) {
            throw new ConfigurationLoadException(
                    "Failed to load configuration from file: " + path, e);
        }
    }

    private void validateFile(Path path) throws ConfigurationLoadException {
        if (path == null) {
            throw new ConfigurationLoadException("Path to configuration file cannot be null");
        }
        if (!path.isAbsolute()) {
            throw new ConfigurationLoadException("Configuration file path must be absolute: " + path);
        }
        if (!Files.exists(path)) {
            throw new ConfigurationLoadException("Configuration file does not exist: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new ConfigurationLoadException("Path is not a regular file: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new ConfigurationLoadException("Configuration file is not readable: " + path);
        }
    }
}
