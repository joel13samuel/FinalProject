package oop.project.library.argument;

public class RangedIntegerArgumentType implements ArgumentType<Integer> {

    private final IntegerArgumentType integerArgumentType;
    private final int min;
    private final int max;

    public RangedIntegerArgumentType(int min, int max) {
        this.integerArgumentType = new IntegerArgumentType();
        this.min = min;
        this.max = max;
    }

    public Integer parse(String value) throws RuntimeException {
        int number = integerArgumentType.parse(value);

        if (number < min || number > max) {
            throw new RuntimeException("Value must be between " + min + " and " + max + ".");
        }

        return number;
    }
}