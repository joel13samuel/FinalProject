package oop.project.library.argument;

public class IntegerArgumentType implements ArgumentType<Integer> {

    public Integer parse(String value) throws RuntimeException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Value must be an integer.");
        }
    }
}