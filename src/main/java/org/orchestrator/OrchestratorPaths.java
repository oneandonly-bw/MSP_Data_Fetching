package org.orchestrator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.orchestrator.Constants.*;

/**
 * Singleton that resolves and validates orchestrator paths once at startup.
 */
public final class OrchestratorPaths {

    private static volatile OrchestratorPaths instance;

    private final Path homePath;
    private final Path orchestratorPath;
    private final Path configDirPath;
    private final Path configFilePath;
    private final Path logsDirPath;

    private OrchestratorPaths(Path homePath, Path orchestratorPath, Path configDirPath, Path configFilePath, Path logsDirPath) {
        this.homePath = homePath;
        this.orchestratorPath = orchestratorPath;
        this.configDirPath = configDirPath;
        this.configFilePath = configFilePath;
        this.logsDirPath = logsDirPath;
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     */
    public static OrchestratorPaths getInstance()
    throws IOException, InvalidPathException {

        if (instance == null) {
            synchronized (OrchestratorPaths.class) {
                if (instance == null) {
                    instance = build();
                }
            }
        }
        return instance;
    }

    public static Path getHomeDir() throws InvalidPathException {

        String home = System.getProperty(HOME_DIR_PROPERTY);
        if (home == null || home.isBlank()) {
            throw new InvalidPathException("null", "System property " +
                    HOME_DIR_PROPERTY + " must be defined");
        }

        return Paths.get(home.trim()).normalize();
    }

    private static OrchestratorPaths build() throws IOException, InvalidPathException {

        Path homePath = getHomeDir();

        if (!homePath.isAbsolute())
            throw new InvalidPathException(homePath.toString(), "HOME path must be absolute path");

        if (!Files.exists(homePath) || !Files.isDirectory(homePath))
            throw new InvalidPathException(homePath.toString(), "HOME path does not exist or is not a directory");

        Path orchestratorPath = homePath.resolve(ORCHESTRATOR_DIR);
        if (!Files.exists(orchestratorPath) || !Files.isDirectory(orchestratorPath))
            throw new InvalidPathException(orchestratorPath.toString(), "Orchestrator directory not found or not a directory");

        Path configDirPath = orchestratorPath.resolve(CONFIGS_DIR);
        if (!Files.exists(configDirPath) || !Files.isDirectory(configDirPath))
            throw new InvalidPathException(configDirPath.toString(), "Configuration directory not found or not a directory");

        Path configFilePath = configDirPath.resolve(ORCHESTRATOR_CONFIG);
        if (!Files.exists(configFilePath) || Files.isDirectory(configFilePath))
            throw new InvalidPathException(configFilePath.toString(), "Orchestrator configuration file not found or is a directory");
        if (!Files.isReadable(configFilePath)) throw new IOException("Orchestrator configuration file is unreadable");

        Path logsDirPath = orchestratorPath.resolve(LOGS_DIR);
        if (!Files.exists(logsDirPath)) {
            Files.createDirectories(logsDirPath);
        }

        return new OrchestratorPaths(homePath, orchestratorPath, configDirPath, configFilePath, logsDirPath);
    }

    // === Getters ===
    public Path getHomePath() {
        return homePath;
    }

    public Path getOrchestratorPath() {
        return orchestratorPath;
    }

    public Path getConfigDirPath() {
        return configDirPath;
    }

    public Path getConfigFilePath() {
        return configFilePath;
    }

    public Path getLogsDirPath() {
        return logsDirPath;
    }
}

