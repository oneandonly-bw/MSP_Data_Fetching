package org.orchestrator.config;

import org.junit.jupiter.api.BeforeEach;
import org.orchestrator.config.model.FetcherControllerConfig;
import org.orchestrator.config.model.JettyConfig;
import org.orchestrator.config.model.LogConfig;
import org.orchestrator.test.BaseSetup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.orchestrator.Constants.LOGS_DIR;

public class ConfigTest extends BaseSetup {

    @BeforeEach
    public void removeLogFolder() throws IOException {
        Path logFolder = homeDir.resolve(ORCHESTRATOR_DIR).resolve(LOGS_DIR);
        deleteDirectoryRecursively(logFolder, true);
    }

    @Test
    public void testConfigFileExists() {
        Path configFile = homeDir.resolve(ORCHESTRATOR_DIR)
                .resolve(CONFIGS_DIR)
                .resolve(ORCHESTRATOR_CONFIG);

        assertTrue(Files.exists(configFile), "Config file should exist");
        assertTrue(Files.isRegularFile(configFile), "Config file should be a regular file");
    }

    @Test
    public void testConfigFileNotEmpty() throws Exception {
        Path configFile = homeDir.resolve(ORCHESTRATOR_DIR)
                .resolve(CONFIGS_DIR)
                .resolve(ORCHESTRATOR_CONFIG);

        assertTrue(Files.size(configFile) > 0, "Config file should not be empty");
    }

    @Test
    public void testConfigFileReadable() throws Exception {
        Path configFile = homeDir.resolve(ORCHESTRATOR_DIR)
                .resolve(CONFIGS_DIR)
                .resolve(ORCHESTRATOR_CONFIG);

        String content = Files.readString(configFile);
        assertTrue(content.contains("\"jetty\""), "Config should contain 'jetty' section");
        assertTrue(content.contains("\"fetcherController\""), "Config should contain 'fetcherController' section");
    }

    @Test
    public void testCorrectConfig() throws Exception {

        OrchestratorConfig config = OrchestratorConfig.getInstance();
        assertNotNull(config, "OrchestratorConfig instance should not be null");

        // --- Jetty validation ---
        JettyConfig jettyConfig = config.getJettyConfig();
        assertNotNull(jettyConfig, "Jetty config should not be null");
        assertEquals("0.0.0.0", jettyConfig.getHost(), "Jetty host should match");
        assertEquals(8080, jettyConfig.getPort(), "Jetty port should match");
        assertFalse(jettyConfig.getHttps().isEnabled(), "HTTPS should be disabled");

        // --- FetcherController validation ---
        FetcherControllerConfig fetcherConfig = config.getFetcherControllerConfig();
        assertNotNull(fetcherConfig, "Fetcher Controller config should not be null");
        assertEquals(15, fetcherConfig.getMaxConcurrentFetchers(),
                "Max concurrent fetchers should match");
        assertEquals(36, fetcherConfig.getHandshakeTokenLength(),
                "Handshake token length should match");

        // --- Log4j2 validation ---
        LogConfig logConfig = config.getLog4j2Config();
        assertNotNull(logConfig, "Log4j2 config should not be null");

        // Log level
        assertEquals(LogConfig.LogLevel.INFO, logConfig.getLevel(), "Log level should be INFO");

        // File settings (without validating path)
        Path expectedLogFolder = homeDir.resolve(ORCHESTRATOR_DIR).resolve(LOGS_DIR);
        Path logDirPath = logConfig.getLogsDirPath();
        assertEquals(expectedLogFolder, logDirPath, "Invalid path of the log directory");
        assertEquals(50, logConfig.getMaxFileSizeMB(), "Max file size should be 50 MB");
        assertEquals(5, logConfig.getMaxBackupFiles(), "Max backup files should be 5");

        // Console settings
        assertTrue(logConfig.isConsoleEnabled(), "Console logging should be enabled");
        assertNotNull(logConfig.getConsolePattern(), "Console pattern should not be null");

        // Validate dynamic log folder exists
        assertTrue(Files.exists(logDirPath), "Log folder should exist");
        assertTrue(Files.isDirectory(logDirPath), "Log folder should be a directory");
    }
}



