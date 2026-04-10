package oop.project.library.argument;

import java.util.function.Function;

public class CustomArgumentType<T> implements ArgumentType<T> {

    private final Function<String, T> parser;

    public CustomArgumentType(Function<String, T> parser) {
        this.parser = parser;
    }

    public T parse(String value) throws RuntimeException {
        try {
            return parser.apply(value);
        } catch (RuntimeException e) {
            throw new RuntimeException("Value is invalid.");
        }
    }
}