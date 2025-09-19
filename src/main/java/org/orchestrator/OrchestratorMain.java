package org.orchestrator;

import org.orchestrator.config.OrchestratorConfig;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.io.IOException;


//TODO: add an option to load config from custom location  => config.dir property
//TODO: add OrchestratorLoggerManager.init (OrchestratorConfig config)
//TODO: all getters including getConfig and getLogger should throw IllegalStateException if not initialized
//TODO: HTTP Server - the same as above
public class OrchestratorMain {

    private static OrchestratorLogger logger;
    private static OrchestratorConfig config;

    public static void main(String[] args) throws IOException {

        //Initialize and Orchestrator configuration
        //This also validates that config is valid
        try {
            OrchestratorConfig.init();
            System.out.println("INFO: Orchestrator configuration is loaded");
        } catch (Exception e) {
            System.out.println("ERROR: Failed load orchestrator configuration. " +
                    "Message: " + e.getMessage() + " Exiting...");
            System.exit(1);
        }



        //Initialize and get Orchestrator logger
        try {
            logger = OrchestratorLoggerManager.getLogger(OrchestratorMain.class);
            logger.info("Logger is initialized");
        } catch (IOException e) {
            System.out.println("ERROR: Failed to initialize logger. " +
                    "Message: " + e.getMessage() + " Exiting...");
            System.exit(1);

        }

    }

    private void shutdown() {
        OrchestratorLoggerManager.shutdown();
    }
}
