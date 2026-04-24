package oop.project.library.argument;

public class RangedDoubleArgumentType extends RangedArgumentType<Double> {

    public RangedDoubleArgumentType(double min, double max) {
        super(new DoubleArgumentType(), min, max);
    }
}
