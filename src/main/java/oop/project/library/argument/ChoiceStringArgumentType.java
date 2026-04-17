package oop.project.library.argument;

import java.util.List;

public class ChoiceStringArgumentType implements ArgumentType<String> {

    private final ValidatedArgumentType<String> validatedArgumentType;

    public ChoiceStringArgumentType(List<String> choices) {
        var allowedChoices = List.copyOf(choices);
        this.validatedArgumentType = new ValidatedArgumentType<>(
                new StringArgumentType(),
                allowedChoices::contains,
                "Value must be one of: " + allowedChoices + "."
        );
    }

    public String parse(String value) throws RuntimeException {
        return validatedArgumentType.parse(value);
    }
}
