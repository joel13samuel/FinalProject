package oop.project.library.argument;

/**
 * Argument type decorator that accepts only values within an inclusive range.
 *
 * <p>This shared range abstraction is used for numeric ranges such as {@code int} and
 * {@code double}, and can also validate any parsed type that implements {@link Comparable}.
 *
 * @param <T> the parsed comparable type being range-checked
 */
public class RangedArgumentType<T extends Comparable<? super T>> implements ArgumentType<T> {

    private final ValidatedArgumentType<T> validatedArgumentType;

    /**
     * Creates a ranged argument type by combining a base parser with inclusive minimum and
     * maximum bounds.
     *
     * @param baseType the underlying argument type that parses the raw string
     * @param min the inclusive minimum accepted value
     * @param max the inclusive maximum accepted value
     * @throws IllegalArgumentException if {@code min} is greater than {@code max}
     */
    public RangedArgumentType(ArgumentType<T> baseType, T min, T max) {
        this.validatedArgumentType = new ValidatedArgumentType<>(
                baseType,
                new RangeValidationRule<>(min, max)
        );
    }

    /**
     * Parses a raw string and then verifies that the parsed value is within the configured
     * inclusive range.
     *
     * @param value the raw string value to parse
     * @return the parsed value if it falls within the configured bounds
     * @throws ArgumentParseException if parsing fails or the parsed value is out of range
     */
    @Override
    public T parse(String value) throws ArgumentParseException {
        return validatedArgumentType.parse(value);
    }
}
