package oop.project.library.argument;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class RegexValidationRule implements ValidationRule<String> {

    private final Pattern pattern;

    public RegexValidationRule(Pattern pattern) {
        this.pattern = Objects.requireNonNull(pattern, "pattern");
    }

    @Override
    public Optional<String> validate(String value) {
        if (pattern.matcher(value).matches()) {
            return Optional.empty();
        }

        return Optional.of("Value must match regex: " + pattern.pattern() + ".");
    }
}
