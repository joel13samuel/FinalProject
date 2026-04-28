package oop.project.library.argument;

/**
 * Convenience wrapper for inclusive double range validation.
 */
public class RangedDoubleArgumentType extends RangedArgumentType<Double> {

    /**
     * Creates a double argument type that accepts values between {@code min} and {@code max},
     * inclusive.
     *
     * @param min the inclusive minimum double value
     * @param max the inclusive maximum double value
     */
    public RangedDoubleArgumentType(double min, double max) {
        super(new DoubleArgumentType(), min, max);
    }
}
