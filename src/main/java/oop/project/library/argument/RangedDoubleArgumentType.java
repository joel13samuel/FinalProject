package oop.project.library.argument;

public class RangedDoubleArgumentType implements ArgumentType<Double> {

    private final DoubleArgumentType doubleArgumentType;
    private final double min;
    private final double max;

    public RangedDoubleArgumentType(double min, double max) {
        this.doubleArgumentType = new DoubleArgumentType();
        this.min = min;
        this.max = max;
    }

    public Double parse(String value) throws RuntimeException {
        double number = doubleArgumentType.parse(value);

        if (number < min || number > max) {
            throw new RuntimeException("Value must be between " + min + " and " + max + ".");
        }

        return number;
    }
}