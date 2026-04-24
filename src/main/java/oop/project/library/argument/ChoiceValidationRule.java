package oop.project.library.argument;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChoiceValidationRule<T> implements ValidationRule<T> {

    private final List<T> allowedChoices;

    public ChoiceValidationRule(List<T> allowedChoices) {
        Objects.requireNonNull(allowedChoices, "allowedChoices");
        if (allowedChoices.isEmpty()) {
            throw new IllegalArgumentException("Choices must not be empty.");
        }
        this.allowedChoices = List.copyOf(allowedChoices);
    }

    @Override
    public Optional<String> validate(T value) {
        if (allowedChoices.contains(value)) {
            return Optional.empty();
        }

        return Optional.of("Value must be one of: " + allowedChoices + ".");
    }
}
