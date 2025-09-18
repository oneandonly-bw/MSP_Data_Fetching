package org.orchestrator;

import org.orchestrator.config.OrchestratorConfig;
import org.orchestrator.logging.OrchestratorLogger;
import org.orchestrator.logging.OrchestratorLoggerManager;

import java.io.IOException;


public class OrchestratorMain {

    private static OrchestratorLogger logger;

    public static void main(String[] args) throws IOException {

        //OrchestratorConfig config = OrchestratorConfig.getInstance();
        System.out.println("INFO: Orchestrator home, directory structure configuration are verified.");

        try {
            logger = OrchestratorLoggerManager.getLogger(OrchestratorMain.class);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to initialize logger. " +
                    "Message: " + e.getMessage() + " Exiting...");
        }
       logger.info("Logger is initialized");
    }

    private void shutdown() {
        OrchestratorLoggerManager.shutdown();
    }
}
