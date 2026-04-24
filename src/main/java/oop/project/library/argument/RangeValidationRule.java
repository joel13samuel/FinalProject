package oop.project.library.argument;

import java.util.Objects;
import java.util.Optional;

public class RangeValidationRule<T extends Comparable<? super T>> implements ValidationRule<T> {

    private final T min;
    private final T max;

    public RangeValidationRule(T min, T max) {
        this.min = Objects.requireNonNull(min, "min");
        this.max = Objects.requireNonNull(max, "max");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum must not exceed maximum.");
        }
    }

    @Override
    public Optional<String> validate(T value) {
        if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
            return Optional.empty();
        }

        return Optional.of("Value must be between " + min + " and " + max + ".");
    }
}
