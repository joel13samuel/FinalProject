package oop.project.library.argument;

public class DoubleArgumentType implements ArgumentType<Double> {

    public Double parse(String value) throws ArgumentParseException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Value must be a double.", e);
        }
    }
}
