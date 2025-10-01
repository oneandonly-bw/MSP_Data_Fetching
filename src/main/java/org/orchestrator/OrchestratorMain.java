package org.orchestrator;


import com.fasterxml.jackson.databind.JsonNode;

import org.common.config.ConfigurationLoader;;
import org.common.exception.ConfigurationLoadException;
import org.common.logger.Log4j2Configurator;
import org.common.logger.LogConfiguration;
import org.common.logger.Logger;
import org.common.logger.LoggerFactory;
import org.orchestrator.fs.OrchestratorPaths;


public class OrchestratorMain {

    public static void main(String[] args) throws ConfigurationLoadException {

        OrchestratorPaths.init();
        OrchestratorPaths paths = OrchestratorPaths.getInstance();

        ConfigurationLoader.init(paths.getConfigFile());
        ConfigurationLoader loader = ConfigurationLoader.getInstance();
        JsonNode configNode = loader.getConfig();

        JsonNode logNode = loader.getLogConfig();
        LogConfiguration logConfiguration=  new LogConfiguration(logNode, paths.getLogFolder(),
                "orchestrator.log");

        Log4j2Configurator.configureFrom(logConfiguration);

        Logger logger = LoggerFactory.getLogger(OrchestratorMain.class);
        logger.info("firstTest");
        logger.error("firstTest");

        int loops = 10000;
        while (loops >= 0) {
            logger.info("firstTest_" + loops);
            logger.error("firstTest_" + loops);
            loops--;
        }
    }

}
