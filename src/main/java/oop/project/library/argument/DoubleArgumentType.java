package oop.project.library.argument;

public class DoubleArgumentType implements ArgumentType<Double> {

    public Double parse(String value) throws RuntimeException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Value must be a double.");
        }
    }
}