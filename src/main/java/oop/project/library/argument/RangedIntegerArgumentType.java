package oop.project.library.argument;

/**
 * Convenience wrapper for inclusive integer range validation.
 */
public class RangedIntegerArgumentType extends RangedArgumentType<Integer> {

    /**
     * Creates an integer argument type that accepts values between {@code min} and {@code max},
     * inclusive.
     *
     * @param min the inclusive minimum integer value
     * @param max the inclusive maximum integer value
     */
    public RangedIntegerArgumentType(int min, int max) {
        super(new IntegerArgumentType(), min, max);
    }
}
