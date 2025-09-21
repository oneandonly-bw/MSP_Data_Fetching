package org.orchestrator;

import org.orchestrator.config.OrchestratorConfig;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.io.IOException;


//TODO: add an option to load config from custom location  => config.dir property
//TODO: add OrchestratorLoggerManager.init (OrchestratorConfig config)
//TODO: HTTP Server - the same as above
//TODO: Paths - the same
public class OrchestratorMain {

    private static OrchestratorLogger logger;
    private static OrchestratorConfig config;

    public static void main(String[] args) throws IOException {

        //Initialize and validate orchestrator configuration
        try {
            OrchestratorConfig.init();
            System.out.println("INFO: Orchestrator configuration is loaded");
        } catch (Exception e) {
            System.out.println("ERROR: Failed load orchestrator configuration. " +
                    "Message: " + e.getMessage() + " Exiting...");
            System.exit(1);
        }

        //Initialize orchestrator logger
        try {
            OrchestratorLoggerManager.init();
        } catch (Exception e) {
            System.out.println("ERROR: Failed to initialize logger. " +
                    "Message: " + e.getMessage() + " Exiting...");
            System.exit(1);
        }

        logger = OrchestratorLoggerManager.getLogger(OrchestratorMain.class);
        logger.info("Logger is initialized");

    }

    private void shutdown() {
        OrchestratorLoggerManager.shutdown();
    }
}
