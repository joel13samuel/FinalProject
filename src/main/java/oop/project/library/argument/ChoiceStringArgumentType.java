package oop.project.library.argument;

import java.util.List;

public class ChoiceStringArgumentType implements ArgumentType<String> {

    private final StringArgumentType stringArgumentType;
    private final List<String> choices;

    public ChoiceStringArgumentType(List<String> choices) {
        this.stringArgumentType = new StringArgumentType();
        this.choices = choices;
    }

    public String parse(String value) throws RuntimeException {
        String parsedValue = stringArgumentType.parse(value);

        if (!choices.contains(parsedValue)) {
            throw new RuntimeException("Value must be one of: " + choices);
        }

        return parsedValue;
    }
}