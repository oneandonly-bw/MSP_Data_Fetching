package org.orchestrator.logging;

import org.apache.logging.log4j.Logger;

public class LoggerAdapter implements OrchestratorLogger {

    private final Logger delegate;


    LoggerAdapter (Logger logger) {
        delegate = logger;
    }


    @Override
    public void info(String msg) {
        delegate.info(msg);
    }

    @Override
    public void warn(String msg) {
        delegate.warn(msg);
    }

    @Override
    public void error(String msg) {
        delegate.error(msg);

    }

    @Override
    public void debug(String msg) {
        delegate.debug(msg);
    }
}
