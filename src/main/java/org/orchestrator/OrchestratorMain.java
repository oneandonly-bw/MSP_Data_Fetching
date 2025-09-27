package org.orchestrator;


import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.core.LoggerContext;
import org.common.config.ConfigurationLoader;
import org.common.config.JsonNodeConfigWrapper;
import org.common.exception.ConfigurationLoadException;
import org.common.logger.Log4j2Configurator;
import org.common.logger.Logger;
import org.common.logger.LoggerFactory;
import org.orchestrator.fs.OrchestratorPaths;

import static org.common.logger.LoggerConfigKeys.LOG4J2;

public class OrchestratorMain {

    public static void main(String[] args) throws ConfigurationLoadException {

        OrchestratorPaths.init();
        OrchestratorPaths paths = OrchestratorPaths.getInstance();

        ConfigurationLoader.init(paths.getConfigFile());
        ConfigurationLoader loader = ConfigurationLoader.getInstance();
        JsonNode configNode = loader.getConfig();

        JsonNodeConfigWrapper config = new JsonNodeConfigWrapper(configNode);
        String logConfigJsonRoot = JsonNodeConfigWrapper.getJsonPath(LOG4J2);
        JsonNodeConfigWrapper logConfig = config.getObject(logConfigJsonRoot);

        Log4j2Configurator.configureFrom(logConfig);
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
