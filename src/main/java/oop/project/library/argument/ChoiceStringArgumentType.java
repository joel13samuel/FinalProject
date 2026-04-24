package oop.project.library.argument;

import java.util.List;

public class ChoiceStringArgumentType implements ArgumentType<String> {

    private final ValidatedArgumentType<String> validatedArgumentType;

    public ChoiceStringArgumentType(List<String> choices) {
        this.validatedArgumentType = new ValidatedArgumentType<>(
                new StringArgumentType(),
                new ChoiceValidationRule<>(choices)
        );
    }

    @Override
    public String parse(String value) throws ArgumentParseException {
        return validatedArgumentType.parse(value);
    }
}
