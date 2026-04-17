package oop.project.library.argument;

import java.util.regex.Pattern;

public class RegexStringArgumentType implements ArgumentType<String> {

    private final StringArgumentType stringArgumentType;
    private final Pattern pattern;

    public RegexStringArgumentType(String regex) {
        this(Pattern.compile(regex));
    }

    public RegexStringArgumentType(Pattern pattern) {
        this.stringArgumentType = new StringArgumentType();
        this.pattern = pattern;
    }

    public String parse(String value) throws RuntimeException {
        String parsedValue = stringArgumentType.parse(value);

        if (!pattern.matcher(parsedValue).matches()) {
            throw new RuntimeException("Value must match regex: " + pattern.pattern() + ".");
        }

        return parsedValue;
    }
}
