package oop.project.library.argument;

public class RangedIntegerArgumentType extends RangedArgumentType<Integer> {

    public RangedIntegerArgumentType(int min, int max) {
        super(new IntegerArgumentType(), min, max);
    }
}
