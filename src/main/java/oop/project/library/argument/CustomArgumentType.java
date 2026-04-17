package oop.project.library.argument;

import java.util.function.Function;

public class CustomArgumentType<T> implements ArgumentType<T> {

    private final Function<String, T> parser;

    public CustomArgumentType(Function<String, T> parser) {
        this.parser = parser;
    }

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
