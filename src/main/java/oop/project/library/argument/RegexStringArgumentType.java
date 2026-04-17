package oop.project.library.argument;

import java.util.regex.Pattern;

public class RegexStringArgumentType implements ArgumentType<String> {

    private final ValidatedArgumentType<String> validatedArgumentType;

    public RegexStringArgumentType(String regex) {
        this(Pattern.compile(regex));
    }

    public RegexStringArgumentType(Pattern pattern) {
        this.validatedArgumentType = new ValidatedArgumentType<>(
                new StringArgumentType(),
                value -> pattern.matcher(value).matches(),
                "Value must match regex: " + pattern.pattern() + "."
        );
    }

    public String parse(String value) throws RuntimeException {
        return validatedArgumentType.parse(value);
    }
}
