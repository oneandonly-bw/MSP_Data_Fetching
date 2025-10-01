package org.common.logger;

import com.fasterxml.jackson.databind.JsonNode;
import org.common.exception.InvalidConfigurationException;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Concrete implementation of {@link RotationLogConfig} for the Orchestrator and fetcher processes.
 * <p>
 * This class reads its configuration from a {@link JsonNode}, following these rules:
 * <ul>
 *     <li>If a value is present and valid → it is used.</li>
 *     <li>If a value is missing or invalid → the corresponding field is set to {@code null}
 *         and will later be validated in {@link #validate()}.</li>
 * </ul>
 *
 * <h3>Path Resolution Rules</h3>
 * The {@code file.path} field is resolved as follows:
 * <ul>
 *   <li>If {@code file.path} is absolute → used as-is.</li>
 *   <li>If {@code file.path} is relative → resolved against {@code logHomeDir}.</li>
 *   <li>If {@code file.path} contains only a file name → placed inside {@code logHomeDir}.</li>
 *   <li>If {@code file.path} is missing → {@code defaultLogFileName} is used inside {@code logHomeDir}.</li>
 * </ul>
 */
public final class LogConfiguration implements RotationLogConfig {

    // JSON field constants
    private static final String LEVEL = "level";
    private static final String FILE = "file";
    private static final String FILE_PATH = "path";
    private static final String FILE_PATTERN = "pattern";
    private static final String FILE_MAX_FILE_SIZE_MB = "maxFileSizeMB";
    private static final String FILE_MAX_BACKUP_FILES = "maxBackupFiles";
    private static final String CONSOLE = "console";
    private static final String CONSOLE_ENABLED = "enabled";
    private static final String CONSOLE_PATTERN = "pattern";

    // Supported log levels
    private static final String[] VALID_LEVELS = {"INFO", "DEBUG", "ERROR", "WARNING"};

    // Configuration fields (nullable until validated)
    private final String rootLevel;
    private final Boolean consoleEnabled;
    private final String consolePattern;
    private final Boolean fileEnabled;
    private final Path filePath;
    private final String filePattern;
    private final Integer maxFileSizeMb;
    private final Integer maxRollovers;

    /**
     * Creates a new {@code LogConfiguration} instance from JSON configuration.
     *
     * @param jsonNode JSON configuration node (must not be null)
     * @param logHomeDir absolute directory where logs should be stored
     * @param defaultLogFileName default log file name if none is provided
     * @throws InvalidConfigurationException if {@code jsonNode} is null or {@code logHomeDir} is null/not absolute
     */
    public LogConfiguration(JsonNode jsonNode, Path logHomeDir, String defaultLogFileName) {
        if (jsonNode == null) {
            throw new InvalidConfigurationException("Configuration JSON must not be null");
        }
        if (logHomeDir == null || !logHomeDir.isAbsolute()) {
            throw new InvalidConfigurationException("logHomeDir must be an absolute path");
        }

        // Root level
        this.rootLevel = jsonNode.hasNonNull(LEVEL) ?
                jsonNode.get(LEVEL).asText().toUpperCase(Locale.ROOT) : null;

        // Console
        JsonNode consoleNode = jsonNode.path(CONSOLE);
        Boolean cEnabled = null;

        try {
            if (consoleNode.has(CONSOLE_ENABLED)) {
                cEnabled = consoleNode.get(CONSOLE_ENABLED).booleanValue();
            }
        } catch (Exception ignored) {}

        this.consoleEnabled = cEnabled;
        this.consolePattern = consoleNode.hasNonNull(CONSOLE_PATTERN)
                ? consoleNode.get(CONSOLE_PATTERN).asText()
                : null;

        // File
        JsonNode fileNode = jsonNode.path(FILE);
        this.filePath = resolveFilePath(fileNode, logHomeDir, defaultLogFileName);
        this.filePattern = fileNode.hasNonNull(FILE_PATTERN)
                ? fileNode.get(FILE_PATTERN).asText()
                : null;
        this.maxFileSizeMb = fileNode.hasNonNull(FILE_MAX_FILE_SIZE_MB)
                ? fileNode.get(FILE_MAX_FILE_SIZE_MB).asInt()
                : null;
        this.maxRollovers = fileNode.hasNonNull(FILE_MAX_BACKUP_FILES)
                ? fileNode.get(FILE_MAX_BACKUP_FILES).asInt()
                : null;

        this.fileEnabled = (this.filePath != null);
    }

    /**
     * Resolves the log file path according to rules described in the class JavaDoc.
     *
     * @param fileNode JSON node containing file configuration
     * @param logHomeDir absolute logging home directory
     * @param defaultLogFileName fallback file name when path is missing
     * @return resolved absolute path, or null if resolution failed
     * @throws InvalidConfigurationException if {@code defaultLogFileName} is required but missing
     */
    private Path resolveFilePath(JsonNode fileNode, Path logHomeDir, String defaultLogFileName)
    throws InvalidConfigurationException {

        if (fileNode.hasNonNull(FILE_PATH)) {
            Path providedPath = Path.of(fileNode.get(FILE_PATH).asText());
            if (providedPath.isAbsolute()) {
                return providedPath;
            } else if (providedPath.getParent() == null) {
                // only file name
                return logHomeDir.resolve(providedPath).normalize();
            } else {
                // relative path
                return logHomeDir.resolve(providedPath).normalize();
            }
        } else {
            if (defaultLogFileName == null || defaultLogFileName.isBlank()) {
                throw new InvalidConfigurationException("Default log file name must be provided if 'file.path' is missing");
            }
            return logHomeDir.resolve(defaultLogFileName);
        }
    }

    // --- Getters ---

    @Override
    public String getRootLevel() {
        return rootLevel;
    }

    @Override
    public boolean isConsoleEnabled() {
        return Boolean.TRUE.equals(consoleEnabled);
    }

    @Override
    public String getConsolePattern() {
        return consolePattern;
    }

    @Override
    public boolean isFileEnabled() {
        return Boolean.TRUE.equals(fileEnabled);
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public String getFilePattern() {
        return filePattern;
    }

    @Override
    public int getMaxFileSizeMb() {
        return maxFileSizeMb != null ? maxFileSizeMb : -1;
    }

    @Override
    public int getMaxRollovers() {
        return maxRollovers != null ? maxRollovers : -1;
    }

    /**
     * Validates the configuration. Ensures that required fields are present and contain valid values.
     *
     * @throws InvalidConfigurationException if validation fails
     */
    @Override
    public void validate() throws InvalidConfigurationException {
        // Root level
        if (rootLevel == null) {
            throw new InvalidConfigurationException("Root logging level must be provided");
        }
        boolean validLevel = false;
        for (String lvl : VALID_LEVELS) {
            if (lvl.equals(rootLevel)) {
                validLevel = true;
                break;
            }
        }
        if (!validLevel) {
            throw new InvalidConfigurationException("Invalid log level: " + rootLevel +
                    " (valid values: INFO, DEBUG, ERROR, WARNING)");
        }

        // Console
        if (consoleEnabled == null) {
            throw new InvalidConfigurationException("'console.enabled' must be a boolean value");
        }
        if (isConsoleEnabled() && (consolePattern == null || consolePattern.isBlank())) {
            throw new InvalidConfigurationException("Console logging enabled but no pattern provided");
        }

        // File
        if (fileEnabled == null || !fileEnabled) {
            throw new InvalidConfigurationException("File logging must be enabled with a valid path");
        }
        if (filePattern == null || filePattern.isBlank()) {
            throw new InvalidConfigurationException("File logging pattern must be provided");
        }
        if (maxFileSizeMb == null || maxFileSizeMb <= 0) {
            throw new InvalidConfigurationException("maxFileSizeMB must be a positive integer");
        }
        if (maxRollovers == null || maxRollovers <= 0) {
            throw new InvalidConfigurationException("maxBackupFiles must be a positive integer");
        }
    }
}

