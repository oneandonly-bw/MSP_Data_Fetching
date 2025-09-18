package org.orchestrator.logging;

public interface OrchestratorLogger {
    void info(String msg);
    void warn(String msg);
    void error(String msg);
    void debug(String msg);
}
