# Orchestrator Logging (`org.orchestrator.logging`)

## Overview

The `org.orchestrator.logging` package provides a robust, production-ready logging framework for Orchestrator and fetcher processes.

Features:
- Single-root logger per JVM
- Full production logging with Log4j2Wrapper
- Thread-safe logger creation
- Consistent formatted messages using placeholders (`{}`) supported by Log4j2

## Package Structure

org.orchestrator.logging  
├── Logger.java             # Logging interface  
├── Log4j2Wrapper.java      # Log4j2 implementation  
├── LoggerFactory.java      # Central logger provider  
└── Log4j2Configurator.java # Programmatic Log4j2 configurator

## Logger Interface

Methods:
- debug(String msg, Object... args)
- info(String msg, Object... args)
- warn(String msg, Object... args)
- error(String msg, Object... args)

Parameters:
- msg → message template containing `{}` placeholders
- args → values to replace placeholders

Example usage:
Logger log = LoggerFactory.getLogger(MyClass.class);
String name = "Alice";
int count = 5;
log.info("User {} has {} messages", name, count);
// Output: User Alice has 5 messages

double price = 12.3456;
log.debug("Price: {} USD", price);
// Output: Price: 12.3456 USD

## Log4j2Wrapper & Log4j2Configurator

- Full production logger with console + rolling file appenders
- Rolling files with max size and backup files
- Customizable patterns and log levels
- Configured programmatically via a Configuration object

Initialization example:
Configuration logConfig = ConfigurationManager.load("orchestrator/config/config.json");
Log4j2Configurator.configureFrom(logConfig);
Logger log = LoggerFactory.getLogger(MyClass.class);
log.info("Logging fully initialized");

Log4j2Configurator Fields / Constants:
- FIELD_LEVEL → configuration key for log level
- FIELD_CONSOLE → configuration key for console settings
- FIELD_ENABLED → console enabled flag
- FIELD_PATTERN → log message pattern
- FIELD_FILE → configuration key for file settings
- FIELD_PATH → file path
- FIELD_MAX_FILE_SIZE_MB → max file size in MB
- FIELD_MAX_BACKUP_FILES → max backup files
- initialized → tracks whether logging is initialized
- context → Log4j2 LoggerContext reference

## LoggerFactory

- Central provider for loggers per class
- Each call returns a new Log4j2Wrapper, but underlying Log4j2 loggers are shared by class
- Thread-safe and non-null guaranteed

All LoggerFactory.getLogger() calls return loggers using the configured provider

## Best Practices

1. Always use LoggerFactory.getLogger(Class<?>) to obtain a logger. Direct usage of Log4j2 is discouraged.
2. Initialize Log4j2 early in main() via Log4j2Configurator.configureFrom().
3. Use placeholders {} in messages instead of manual string concatenation.

## Quick Start

Logger log = LoggerFactory.getLogger(MyClass.class);
log.info("Starting Orchestrator...");

Configuration logConfig = ConfigurationManager.load("orchestrator/config/log.json");
Log4j2Configurator.configureFrom(logConfig);

log = LoggerFactory.getLogger(MyClass.class);
log.info("Logging is fully initialized and ready");

## Initialization Flow (ASCII diagram)

Log4j2Configurator --> Log4j2Wrapper --> LoggerFactory --> Modules

- Log4j2Configurator sets up console/file appenders and root logger levels
- LoggerFactory provides class-specific loggers
- Modules use LoggerFactory.getLogger() for logging
