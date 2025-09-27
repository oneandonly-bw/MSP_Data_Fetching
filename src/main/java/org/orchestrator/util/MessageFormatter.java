package org.orchestrator.util;

/**
 * Utility class to format messages with placeholders and special tokens.
 * <p>
 * Implements a subset of Log4j2 formatting functionality:
 * <ul>
 *     <li>Exact '{}' sequences as placeholders replaced by provided arguments.</li>
 *     <li>Special token '%t' automatically replaced by the current thread name.</li>
 * </ul>
 * Primarily designed to be used for logger messages, but can also be used
 * in other contexts where similar formatting is needed.
 * </p>
 *
 * <h3>Examples</h3>
 * <pre>{@code
 * String msg1 = MessageFormatter.format("User {} logged in from {}", "Alice", "127.0.0.1");
 * // returns "User Alice logged in from 127.0.0.1"
 *
 * String msg2 = MessageFormatter.format("[%t] Service {} started", "MyService");
 * // if thread name is "Fetcher-1", returns "[Fetcher-1] Service MyService started"
 *
 * String msg3 = MessageFormatter.format("Hello {}, your thread is %t", "Bob");
 * // returns "Hello Bob, your thread is Fetcher-1"
 * }</pre>
 */
public final class MessageFormatter {

    private MessageFormatter() {
        // utility class, no instances
    }

    /**
     * Formats the message by replacing '{}' placeholders with provided arguments
     * and '%t' tokens with the current thread name.
     *
     * @param message the message containing '{}' placeholders and/or '%t' tokens
     * @param args    arguments to replace '{}' placeholders
     * @return formatted message with placeholders and tokens replaced
     */
    public static String format(String message, Object... args) {
        if (message == null) return null;

        // replace %t with current thread name
        message = message.replace("%t", Thread.currentThread().getName());

        if (args == null || args.length == 0) return message;

        // split by literal '{}'
        String[] parts = message.split("\\{}", -1);
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (; i < parts.length - 1; i++) {
            sb.append(parts[i]);
            if (i < args.length) {
                sb.append(args[i] == null ? "null" : args[i].toString());
            } else {
                sb.append("{}"); // no argument left
            }
        }
        sb.append(parts[parts.length - 1]); // append last part
        return sb.toString();
    }

    //For sanity
    //TODO: To remove
    public static void main(String[] args) {
        String user = "Alice";
        String ip = "127.0.0.1";

        // Test basic {} placeholders
        String msg1 = MessageFormatter.format("User {} logged in from {}", user, ip);
        System.out.println(msg1);
        System.out.println("User Alice logged in from 127.0.0.1");

        // Test %t replacement with thread name
        String msg2 = MessageFormatter.format("[%t] Service {} started", "MyService");
        System.out.println(msg2);
        System.out.println( "[main] Service MyService started (if run on main thread)");

        // Test both {} and %t
        String msg3 = MessageFormatter.format("Hello {}, your thread is %t", "Bob");
        System.out.println(msg3);
        System.out.println("Hello Bob, your thread is main");

        // Test more {} than arguments
        String msg4 = MessageFormatter.format("Values: {} {} {}", 1, 2);
        System.out.println(msg4);
        System.out.println("Values: 1 2 {}");

        // Test null arguments
        String msg5 = MessageFormatter.format("Null test: {}", (Object) null);
        System.out.println(msg5);
        System.out.println( "Null test: null");

        // Test no arguments
        String msg6 = MessageFormatter.format("No placeholders here");
        System.out.println(msg6);
        System.out.println("No placeholders here");
    }
}
