package org.orchestrator.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.orchestrator.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class BaseSetup {

    // org.orchestrator.Constants
    public static final String HOME_DIR_PROPERTY = org.orchestrator.Constants.HOME_DIR_PROPERTY;
    public static final String ORCHESTRATOR_DIR = Constants.ORCHESTRATOR_DIR;
    public static final String CONFIGS_DIR = org.orchestrator.Constants.CONFIGS_DIR;
    public static final String ORCHESTRATOR_CONFIG = org.orchestrator.Constants.ORCHESTRATOR_CONFIG;

    protected static Path homeDir;

    @BeforeAll
    public static void setupEnvironment() {

        String homePath = System.getProperty(HOME_DIR_PROPERTY);

        if (homePath == null || homePath.isEmpty()) {
            fail("System property '" + HOME_DIR_PROPERTY + "' is not set.");
        }

        homeDir = Paths.get(homePath);
        if (!Files.exists(homeDir)) {
            fail("Home directory does not exist: " + homeDir);
        }

        try {
            // Clean everything under home
            deleteDirectoryRecursively(homeDir, false);

            // Create required directories
            Path orchestratorDir = homeDir.resolve(ORCHESTRATOR_DIR);
            Path configsDir = orchestratorDir.resolve(CONFIGS_DIR);
            Files.createDirectories(configsDir);

            // Copy JSON config from test resources
            copyDefaultConfig(configsDir);

        } catch (IOException e) {
            fail("Failed to prepare test environment: " + e.getMessage());
        }
    }

    private static void copyDefaultConfig(Path configsDir) throws IOException {
        Path targetConfig = configsDir.resolve(ORCHESTRATOR_CONFIG);

        try (InputStream is = BaseSetup.class.getClassLoader()
                .getResourceAsStream(ORCHESTRATOR_CONFIG)) {
            if (is == null) {
                fail("Default config not found in test resources: " + ORCHESTRATOR_CONFIG);
            }
            Files.copy(is, targetConfig, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void deleteDirectoryRecursively(Path dir, boolean deleteRoot)
    throws RuntimeException {
        if (Files.exists(dir)) {
            try (java.util.stream.Stream<Path> paths = Files.walk(dir)) {
                paths.sorted(Comparator.reverseOrder()) // delete children first
                        .forEach(path -> {
                            try {
                                if (deleteRoot || !path.equals(dir)) {
                                    Files.deleteIfExists(path);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete " + path, e);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException("Failed to walk directory " + dir, e);
            }
        }
    }

    @AfterAll
    public static void tearDownEnvironment() {
        // Optional: cleanup after all tests
    }
}
