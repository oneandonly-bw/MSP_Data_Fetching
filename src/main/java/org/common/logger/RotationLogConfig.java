package org.common.logger;

import org.common.exception.InvalidConfigurationException;

import java.nio.file.Path;

public interface RotationLogConfig {


    /**
     * Returns the root logger level (e.g. "INFO", "DEBUG", "ERROR").
     *
     * @return root logger level as a string
     */
    public  String getRootLevel();

    /**
     * Indicates whether console logging is enabled.
     *
     * @return true if console appender should be active
     */
    public boolean isConsoleEnabled();

    /**
     * Returns the pattern for console log
     * messages (Log4j2 PatternLayout syntax).
     *
     * @return console log pattern string
     */
    public String getConsolePattern();

    /**
     * Indicates whether file logging is enabled.
     *
     * @return true if file appender should be active
     */
    public boolean isFileEnabled();

    /**
     * Returns the absolute path to the current log file.
     * <p>
     * When rotation occurs, this file is renamed (e.g., "app.log" → "app_1.log"),
     * and a new current log file is created.
     *
     * @return absolute path to the current log file
     */
    public Path getFilePath();

    /**
     * Returns the pattern for file log messages (Log4j2 PatternLayout syntax).
     *
     * @return file log pattern string
     */
    public String getFilePattern();

    /**
     * Returns the maximum file size in megabytes before triggering rotation
     * (rotation policy).
     *
     * @return maximum file size in MB
     */
    public int getMaxFileSizeMb();

    /**
     * Returns the maximum number of rotated log files to keep (rotation policy).
     *
     * @return maximum number of rollover files
     */
    public int getMaxRollovers();

    /**
     * Validates the logging configuration and throws an exception if required
     * values are missing, invalid, or cannot be converted.
     *
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    public void validate() throws InvalidConfigurationException;

}
