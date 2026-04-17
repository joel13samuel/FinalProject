package oop.project.library.argument;

public class RangedDoubleArgumentType implements ArgumentType<Double> {

    private final ValidatedArgumentType<Double> validatedArgumentType;

    public RangedDoubleArgumentType(double min, double max) {
        this.validatedArgumentType = new ValidatedArgumentType<>(
                new DoubleArgumentType(),
                number -> number >= min && number <= max,
                "Value must be between " + min + " and " + max + "."
        );
    }

    public Double parse(String value) throws ArgumentParseException {
        return validatedArgumentType.parse(value);
    }
}
