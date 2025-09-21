package org.orchestrator.config.validation;

import org.orchestrator.OrchestratorPaths;
import org.orchestrator.config.OrchestratorConfig;

import java.io.IOException;
import java.nio.file.InvalidPathException;
//TODO: to rewrite
public class OrchestratorConfigValidator {

    public static void main(String[] args) {
        System.out.println("Starting Orchestrator Configuration Validation...");

        // Step 1: Get paths singleton (validates env and directory structure)
        OrchestratorPaths paths;
        try {
            paths = OrchestratorPaths.getInstance();
            System.out.println("Environment variable and directory structure verified.");
        } catch (InvalidPathException | IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
            return; // unreachable, but keeps compiler happy
        }

        // Step 2: Load and validate configuration
        try {
            OrchestratorConfig config = OrchestratorConfig.getInstance();
            System.out.println("Configuration validation passed: orchestrator_config.json is valid.");
        } catch ( Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}

