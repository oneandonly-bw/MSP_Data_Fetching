package org.common.util;

import org.common.exception.InvalidConfigurationException;

import java.nio.file.Path;

public class FileUtils {

    /**
     * Resolves a path relative to a base directory (rootPath).
     * <pre>
     * Logic:
     * 1. If filePath is absolute → return it as-is (rootPath is ignored, may be null).
     * 2. If filePath is relative → rootPath must be non-null and absolute,
     *    then filePath is resolved against rootPath.
     * </pre>
     *
     * @param rootPath base directory, must be absolute if filePath is relative
     * @param filePath path to resolve (absolute or relative, must not be null)
     * @return resolved absolute path as String
     *
     * @throws InvalidConfigurationException if:
     *         <ul>
     *             <li>filePath is {@code null}</li>
     *             <li>filePath is relative and rootPath is {@code null}</li>
     *             <li>filePath is relative and rootPath is not absolute</li>
     *         </ul>
     */
    public static String resolveFilePath(Path rootPath, Path filePath)
            throws InvalidConfigurationException {

        if (filePath == null) {
            throw new InvalidConfigurationException("File path must not be null");
        }

        if (filePath.isAbsolute()) {
            return filePath.toString();
        }

        if (rootPath == null || !rootPath.isAbsolute()) {
            throw new InvalidConfigurationException(
                    "Root path must be non-null and absolute when filePath is relative"
            );
        }

        return rootPath.resolve(filePath).toAbsolutePath().toString();
    }

    /**
     * Extracts the file extension from a file path string.
     * <pre>
     * Examples:
     *   "orchestrator.log"   → "log"
     *   "archive.tar.gz"     → "gz"
     *   "orchestrator"       → ""   (no extension)
     *   ".hidden"            → ""   (treated as hidden file, no extension)
     * </pre>
     *
     * @param filePath full file name or path (must not be null)
     * @return the extension without the dot, or empty string if none
     *
     * @throws InvalidConfigurationException if {@code filePath} is null
     */
    public static String getFileExtension(String filePath)
    throws  InvalidConfigurationException {

        if (filePath == null) {
            throw new InvalidConfigurationException("File path must be non-null path to file");
        }

        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            return filePath.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * Removes the extension (last dot and following characters) from a file path string.
     * <pre>
     * Examples:
     *   "orchestrator.log"   → "orchestrator"
     *   "archive.tar.gz"     → "archive.tar"
     *   "orchestrator"       → "orchestrator"
     *   ".hidden"            → ".hidden"
     * </pre>
     *
     * @param filePath full file name or path (must not be null)
     * @return the file name without its last extension
     *
     * @throws InvalidConfigurationException if {@code filePath} is null
     */
    public static String removeExtension(String filePath)
    throws InvalidConfigurationException {

        if (filePath == null) {
            throw new InvalidConfigurationException("File path must be non-null path to file");
        }

        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            return filePath.substring(0, dotIndex);
        }

        return filePath;
    }

}
