package org.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.common.exception.JsonPathNotFoundException;
import org.common.exception.InvalidJsonValueTypeException;

import java.util.ArrayList;
import java.util.List;
/**
 * A lightweight, read-only wrapper around a Jackson {@link JsonNode} to provide
 * convenient access to configuration values using simple dot-separated paths.
 * <p>
 * Supports:
 * <ul>
 *     <li>Primitive value getters: {@code getString}, {@code getInt}, {@code getBoolean}</li>
 *     <li>Nested objects: {@code getObject}</li>
 *     <li>Lists of primitives: {@code getList(String path, Class<T> type)}</li>
 *     <li>Lists of objects: {@code getObjectList}</li>
 * </ul>
 * <p>
 * Throws explicit exceptions when a path is missing or a value has an unexpected type:
 * <ul>
 *     <li>{@link JsonPathNotFoundException}</li>
 *     <li>{@link InvalidJsonValueTypeException}</li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * JsonNodeConfigWrapper config = new JsonNodeConfigWrapper(rootNode);
 *
 * int httpPort = config.getInt("jetty.http.port");
 * String logFile = config.getString("log4j2.file.path");
 * boolean httpsEnabled = config.getBoolean("jetty.https.enabled", false);
 *
 * // Access nested object and get a value from it
 * JsonNodeConfigWrapper httpConfig = config.getObject("jetty.http");
 * int nestedPort = httpConfig.getInt("port");   // <-- example of getting a value
 *
 * // Primitive list
 * List<String> tags = config.getList("tags", String.class);
 *
 * // Object list
 * List<JsonNodeConfigWrapper> adapters = config.getObjectList("adapters");
 * }</pre>
 */
public class JsonNodeConfigWrapper {

    private final JsonNode root;

    /**
     * Create a wrapper around the given JsonNode.
     *
     * @param root the root JsonNode of the configuration
     */
    public JsonNodeConfigWrapper(JsonNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Root JsonNode cannot be null");
        }
        this.root = root;
    }

    // ----------------------------
    // Internal path resolution
    // ----------------------------

    /** Resolve a dot-separated path in the JSON tree */
    private JsonNode resolve(String path) {
        String[] keys = path.split("\\.");
        JsonNode node = root;

        for (String key : keys) {
            if (node == null) {
                return null;
            }
            node = node.get(key);
        }

        return node;
    }

    // ----------------------------
    // Single value getters
    // ----------------------------

    public JsonNode getRoot() {
        return root;
    }

    public String getString(String path) {
        JsonNode node = resolve(path);
        if (node == null || node.isNull()) {
            throw new JsonPathNotFoundException(path);
        }
        if (!node.isTextual()) {
            throw new InvalidJsonValueTypeException(path, "string", node.getNodeType().toString());
        }
        return node.asText();
    }

    public String getString(String path, String defaultValue) {
        JsonNode node = resolve(path);
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        if (!node.isTextual()) {
            throw new InvalidJsonValueTypeException(path, "string", node.getNodeType().toString());
        }
        return node.asText();
    }

    public int getInt(String path) {
        JsonNode node = resolve(path);
        if (node == null) {
            throw new JsonPathNotFoundException(path);
        }
        if (!node.isInt() && !node.isLong()) {
            throw new InvalidJsonValueTypeException(path, "int", node.getNodeType().toString());
        }
        return node.asInt();
    }

    public int getInt(String path, int defaultValue) {
        JsonNode node = resolve(path);
        if (node == null) {
            return defaultValue;
        }
        if (!node.isInt() && !node.isLong()) {
            throw new InvalidJsonValueTypeException(path, "int", node.getNodeType().toString());
        }
        return node.asInt();
    }

    public boolean getBoolean(String path) {
        JsonNode node = resolve(path);
        if (node == null) {
            throw new JsonPathNotFoundException(path);
        }
        if (!node.isBoolean()) {
            throw new InvalidJsonValueTypeException(path, "boolean", node.getNodeType().toString());
        }
        return node.asBoolean();
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        JsonNode node = resolve(path);
        if (node == null) {
            return defaultValue;
        }
        if (!node.isBoolean()) {
            throw new InvalidJsonValueTypeException(path, "boolean", node.getNodeType().toString());
        }
        return node.asBoolean();
    }

    public JsonNodeConfigWrapper getObject(String path) {
        JsonNode node = resolve(path);
        if (node == null) {
            throw new JsonPathNotFoundException(path);
        }
        if (!node.isObject()) {
            throw new InvalidJsonValueTypeException(path, "object", node.getNodeType().toString());
        }
        return new JsonNodeConfigWrapper(node);
    }

    // ----------------------------
    // List getters
    // ----------------------------

    /**
     * Get a list of primitives (String, Integer, Boolean, etc.) from a JSON array.
     *
     * @param path the dot-separated path to the array
     * @param type the class of the elements (String.class, Integer.class, Boolean.class, etc.)
     * @param <T>  element type
     * @return a list of values
     */
    public <T> List<T> getList(String path, Class<T> type) {
        JsonNode node = resolve(path);

        if (node == null) {
            throw new JsonPathNotFoundException(path);
        }
        if (!node.isArray()) {
            throw new InvalidJsonValueTypeException(path, "array of " + type.getSimpleName(), node.getNodeType().toString());
        }

        List<T> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (type == String.class && item.isTextual()) {
                result.add(type.cast(item.asText()));
            } else if (type == Integer.class && item.isInt()) {
                result.add(type.cast(item.asInt()));
            } else if (type == Long.class && item.isLong()) {
                result.add(type.cast(item.asLong()));
            } else if (type == Boolean.class && item.isBoolean()) {
                result.add(type.cast(item.asBoolean()));
            } else {
                throw new InvalidJsonValueTypeException(path, type.getSimpleName(),
                        item.getNodeType().toString());
            }
        }

        return result;
    }

    /**
     * Get a list of objects (wrapped) from a JSON array.
     *
     * @param path the dot-separated path to the array of objects
     * @return list of JsonNodeConfigWrapper objects
     */
    public List<JsonNodeConfigWrapper> getObjectList(String path) {
        JsonNode node = resolve(path);

        if (node == null) {
            throw new JsonPathNotFoundException(path);
        }
        if (!node.isArray()) {
            throw new InvalidJsonValueTypeException(path, "array of objects", node.getNodeType().toString());
        }

        List<JsonNodeConfigWrapper> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (!item.isObject()) {
                throw new InvalidJsonValueTypeException(path, "object", item.getNodeType().toString());
            }
            result.add(new JsonNodeConfigWrapper(item));
        }

        return result;
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
}
