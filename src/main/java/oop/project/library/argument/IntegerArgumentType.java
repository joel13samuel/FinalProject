package oop.project.library.argument;

public class IntegerArgumentType implements ArgumentType<Integer> {

    public Integer parse(String value) throws ArgumentParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Value must be an integer.", e);
        }
    }
}
