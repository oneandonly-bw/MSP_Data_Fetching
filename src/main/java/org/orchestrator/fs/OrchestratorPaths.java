package org.orchestrator.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.orchestrator.fs.FsConstants.*;

/**
 * Singleton managing all filesystem paths for the Orchestrator process.
 *
 * <p>All paths must be absolute and are initialized once via {@link #init()}.
 * Subsequent calls to {@link #init()} will throw an IllegalStateException.
 *
 * <p>Default folder structure (under <code>path.home</code>) is:
 * <pre>
 * PATH_HOME/
 * └── Orchestrator/
 *     ├── Config/
 *     │   ├── Orchestrator_config.json
 *     │   └── schema/
 *     │       └── Orchestrator_schema.json
 *     └── Logs/
 * </pre>
 *
 * <p>Optional overrides are supported via JVM properties defined in {@link FsConstants}.
 */
public final class OrchestratorPaths {

    private static OrchestratorPaths instance;

    private Path root;
    private Path configFile;
    private Path logFolder;
    private Path schemaFile;

    private OrchestratorPaths() {
        // private constructor
    }

    /**
     * Initialize singleton instance using JVM properties.
     * Must be called once before using getters.
     *
     * @throws InvalidPathException  if a path is invalid or not absolute
     * @throws IllegalStateException if called more than once
     */
    public static synchronized void init()
    throws InvalidPathException, IllegalStateException {

        if (instance != null) {
            throw new IllegalStateException("OrchestratorPaths already initialized");
        }

        OrchestratorPaths paths = new OrchestratorPaths();

        // Root path
        paths.root = checkAbsoluteAndGet(System.getProperty(PATH_HOME), PATH_HOME);

        // Config file path
        paths.configFile = resolvePath(
                System.getProperty(PATH_CONFIG_FILE),
                paths.root.resolve(ORCHESTRATOR_FOLDER)
                        .resolve(CONFIG_FOLDER)
                        .resolve(CONFIG_FILE_NAME));
        validateFile(paths.configFile);

        // Logs folder path
        paths.logFolder = resolvePath(
                System.getProperty(PATH_LOGS),
                paths.root.resolve(ORCHESTRATOR_FOLDER)
                        .resolve(LOGS_FOLDER));
        validateDir(paths.logFolder, true);

        // Schema file path
        paths.schemaFile = resolvePath(
                System.getProperty(PATH_SCHEMA_FILE),
                paths.root.resolve(ORCHESTRATOR_FOLDER)
                        .resolve(CONFIG_FOLDER)
                        .resolve(SCHEMA_FOLDER)
                        .resolve(SCHEMA_FILE_NAME));
        validateFile(paths.schemaFile);

        instance = paths;
    }

    /**
     * Get the singleton instance of OrchestratorPaths.
     *
     * @return OrchestratorPaths instance
     * @throws IllegalStateException if init() was not called yet
     */
    public static OrchestratorPaths getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("OrchestratorPaths not initialized. Call init() first.");
        }
        return instance;
    }

    // ----------------------------
    // Getters
    // ----------------------------

    /**
     * Get the absolute root folder path.
     *
     * @return Path to root folder
     */
    public Path getRoot()  {
        return root;
    }

    /**
     * Get the absolute Orchestrator configuration file path.
     *
     * @return Path to config file
     */
    public Path getConfigFile() {
        return configFile;
    }

    /**
     * Get the absolute Orchestrator logs folder path.
     *
     * @return Path to logs folder
     */
    public Path getLogFolder() {
        return logFolder;
    }

    /**
     * Get the absolute Orchestrator schema file path.
     *
     * @return Path to schema file
     */
    public Path getSchemaFile()  {
        return schemaFile;
    }

    // ----------------------------
    // Validation helpers
    // ----------------------------

    /**
     * Validate that a folder exists, is absolute, readable, writable, and optionally create it.
     *
     * @param dir    folder path to validate
     * @param create if true, create folder if missing
     * @throws InvalidPathException if folder is invalid, inaccessible, or cannot be created
     */
    public static void validateDir(Path dir, boolean create)
    throws InvalidPathException {

        Path absPath = checkAbsoluteAndGet(dir.toString(), "directory");

        try {
            if (!Files.exists(absPath)) {
                if (create) {
                    try {
                        Files.createDirectories(absPath);
                    } catch (IOException e) {
                        throw new InvalidPathException(absPath.toString(),
                                "Failed to create directory: " + e.getMessage());
                    }
                } else {
                    throw new InvalidPathException(absPath.toString(), "Directory does not exist");
                }
            }

            if (!Files.isDirectory(absPath)) {
                throw new InvalidPathException(absPath.toString(), "Not a directory");
            }

            if (!Files.isReadable(absPath) || !Files.isWritable(absPath)) {
                throw new InvalidPathException(absPath.toString(), "Directory is not accessible");
            }
        } catch (InvalidPathException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidPathException(absPath.toString(),
                    "Failed to validate directory: " + e.getMessage());
        }
    }

    /**
     * Validate that a file exists, is absolute, readable, and a regular file.
     *
     * @param file file path to validate
     * @throws InvalidPathException if file is invalid, missing, or not readable
     */
    public static void validateFile(Path file)
    throws InvalidPathException {

        Path absPath = checkAbsoluteAndGet(file.toString(), "file");

        if (!Files.exists(absPath) || !Files.isRegularFile(absPath)) {
            throw new InvalidPathException(absPath.toString(), "File does not exist");
        }
        if (!Files.isReadable(absPath)) {
            throw new InvalidPathException(absPath.toString(), "File is not readable");
        }
    }

    // ----------------------------
    // Private helpers
    // ----------------------------

    /**
     * Check that the path string is absolute and valid.
     *
     * @param pathStr     path string
     * @param description description for error messages
     * @return absolute Path
     * @throws InvalidPathException if path is invalid or not absolute
     */
    private static Path checkAbsoluteAndGet(String pathStr, String description)
    throws InvalidPathException {

        if (pathStr == null || pathStr.isBlank()) {
            throw new InvalidPathException("", description + " must be defined");
        }
        Path path = Paths.get(pathStr);;

        if (!path.isAbsolute()) {
            throw new InvalidPathException(path.toString(),
                    description + " must be an absolute path");
        }
        return path;
    }

    /**
     * Resolve property value or fallback default path.
     *
     * @param propertyValue property override
     * @param defaultPath   fallback default
     * @return resolved Path
     * @throws InvalidPathException if path is invalid or not absolute
     */
    private static Path resolvePath(String propertyValue, Path defaultPath)
    throws InvalidPathException {

        if (propertyValue != null && !propertyValue.isBlank()) {
            return checkAbsoluteAndGet(propertyValue, "path override");
        }
        return defaultPath;
    }
}
