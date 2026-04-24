package oop.project.library.argument;

public class RangedArgumentType<T extends Comparable<? super T>> implements ArgumentType<T> {

    private final ValidatedArgumentType<T> validatedArgumentType;

    public RangedArgumentType(ArgumentType<T> baseType, T min, T max) {
        this.validatedArgumentType = new ValidatedArgumentType<>(
                baseType,
                new RangeValidationRule<>(min, max)
        );
    }

    @Override
    public T parse(String value) throws ArgumentParseException {
        return validatedArgumentType.parse(value);
    }
}
