package org.orchestrator;

/**
 * Central place for JSON configuration keys and utilities.
 *
 * <p>Defines constants for configuration field names.
 * Each constant is prefixed with its context to avoid ambiguity.
 * Use {@link #getJsonPath(String...)} to construct full JSON paths.
 *
 * <p>Example usage:
 * <pre>{@code
 * String httpPortPath = ConfigKeys.getJsonPath(
 *         ConfigKeys.JETTY,
 *         ConfigKeys.JETTY_HTTP,
 *         ConfigKeys.JETTY_HTTP_PORT);
 * int httpPort = config.getInt(httpPortPath);
 * }</pre>
 */
public final class ConfigKeys {

    private ConfigKeys() {
        // prevent instantiation
    }

    /**
     * Build a dot-separated JSON path from given parts.
     *
     * @param parts one or more path segments
     * @return joined JSON path, e.g. {@code "jetty.http.enabled"}
     * @throws IllegalArgumentException if no parts are provided or any part is null/blank
     */
    public static String getJsonPath(String... parts) {
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("At least one path segment is required");
        }
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                throw new IllegalArgumentException("Path segment cannot be null or blank");
            }
            if (!sb.isEmpty()) {
                sb.append('.');
            }
            sb.append(part);
        }
        return sb.toString();
    }

    // ===================== JETTY =====================
    public static final String JETTY = "jetty";
    public static final String JETTY_HOST = "host";
    public static final String JETTY_ACCEPT_QUEUE_SIZE = "acceptQueueSize";

    public static final String JETTY_HTTP = "http";
    public static final String JETTY_HTTP_ENABLED = "enabled";
    public static final String JETTY_HTTP_PORT = "port";

    public static final String JETTY_HTTPS = "https";
    public static final String JETTY_HTTPS_ENABLED = "enabled";
    public static final String JETTY_HTTPS_PORT = "port";
    public static final String JETTY_HTTPS_SECRET_NAME = "httpsSecretName";

    public static final String JETTY_THREAD_POOL = "threadPool";
    public static final String JETTY_THREAD_POOL_TYPE = "type";
    public static final String JETTY_THREAD_POOL_MAX_THREADS = "maxThreads";
    public static final String JETTY_THREAD_POOL_MIN_THREADS = "minThreads";
    public static final String JETTY_THREAD_POOL_IDLE_TIMEOUT_SEC = "idleTimeoutSec";

    public static final String JETTY_WHITELIST_SECRET_NAME = "whitelistSecretName";

    // ===================== FETCHER CONTROLLER =====================
    public static final String FTR_CONTROLLER = "fetcherController";
    public static final String FTR_MAX_CONCURRENT_FETCHERS = "maxConcurrentFetchers";
    public static final String FTR_QUEUE_SIZE = "queueSize";
    public static final String FTR_HANDSHAKE_TOKEN_LENGTH = "handshakeTokenLength";
    public static final String FTR_IPC_PORT_BASE = "ipcPortBase";
    public static final String FTR_REQUEST_TIMEOUT_SEC = "requestTimeoutSec";
    public static final String FTR_RESTART_TIMEOUT_MINUTES = "restartTimeoutMinutes";
}
