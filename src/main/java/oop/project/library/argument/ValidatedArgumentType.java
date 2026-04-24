package oop.project.library.argument;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ValidatedArgumentType<T> implements ArgumentType<T> {

    private final ArgumentType<T> baseType;
    private final List<ValidationRule<T>> validationRules;

    public ValidatedArgumentType(ArgumentType<T> baseType, ValidationRule<T> validationRule) {
        this(baseType, List.of(validationRule));
    }

    public ValidatedArgumentType(ArgumentType<T> baseType, List<ValidationRule<T>> validationRules) {
        this.baseType = Objects.requireNonNull(baseType, "baseType");
        Objects.requireNonNull(validationRules, "validationRules");
        if (validationRules.isEmpty()) {
            throw new IllegalArgumentException("At least one validation rule is required.");
        }
        this.validationRules = List.copyOf(validationRules);
    }

    public ValidatedArgumentType(ArgumentType<T> baseType, Predicate<T> validator, String errorMessage) {
        this(baseType, ValidationRule.from(validator, errorMessage));
    }

    @Override
    public T parse(String value) throws ArgumentParseException {
        T parsedValue = baseType.parse(value);

        for (var validationRule : validationRules) {
            validationRule.validateOrThrow(parsedValue);
        }

        return parsedValue;
    }
}
