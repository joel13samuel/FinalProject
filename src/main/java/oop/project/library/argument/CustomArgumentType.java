package oop.project.library.argument;

import java.util.function.Function;

/**
 * Argument type implementation backed by a caller-provided parsing function.
 *
 * @param <T> the target type produced by the parser
 */
public class CustomArgumentType<T> implements ArgumentType<T> {

    private final Function<String, T> parser;

    /**
     * Creates a custom argument type using the given parsing function.
     *
     * <p>This is the built-in extension point for parsing types not directly provided by the
     * library, such as {@code LocalDate}.
     *
     * @param parser function that converts a raw string into a typed value
     */
    public CustomArgumentType(Function<String, T> parser) {
        this.parser = parser;
    }

    /**
     * Parses a raw string by delegating to the custom parser function.
     *
     * @param value the raw string value to parse
     * @return the parsed typed value
     * @throws ArgumentParseException if the custom parser rejects the value
     */
    public T parse(String value) throws ArgumentParseException {
        try {
            return parser.apply(value);
        } catch (ArgumentParseException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ArgumentParseException("Value is invalid.", e);
        }
    }
}
