package oop.project.library.argument;

public class RangedIntegerArgumentType implements ArgumentType<Integer> {

    private final ValidatedArgumentType<Integer> validatedArgumentType;

    public RangedIntegerArgumentType(int min, int max) {
        this.validatedArgumentType = new ValidatedArgumentType<>(
                new IntegerArgumentType(),
                number -> number >= min && number <= max,
                "Value must be between " + min + " and " + max + "."
        );
    }

    public Integer parse(String value) throws ArgumentParseException {
        return validatedArgumentType.parse(value);
    }
}
