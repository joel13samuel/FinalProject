package oop.project.library.argument;

import java.util.function.Predicate;

public class ValidatedArgumentType<T> implements ArgumentType<T> {

    private final ArgumentType<T> baseType;
    private final Predicate<T> validator;
    private final String errorMessage;

    public ValidatedArgumentType(ArgumentType<T> baseType, Predicate<T> validator, String errorMessage) {
        this.baseType = baseType;
        this.validator = validator;
        this.errorMessage = errorMessage;
    }

    public T parse(String value) throws RuntimeException {
        T parsedValue = baseType.parse(value);

        if (!validator.test(parsedValue)) {
            throw new RuntimeException(errorMessage);
        }

        return parsedValue;
    }
}
