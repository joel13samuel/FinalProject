package oop.project.library.argument;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ValidationRule<T> {

    Optional<String> validate(T value);

    default void validateOrThrow(T value) throws ArgumentParseException {
        var errorMessage = validate(value);
        if (errorMessage.isPresent()) {
            throw new ArgumentParseException(errorMessage.get());
        }
    }

    static <T> ValidationRule<T> from(Predicate<? super T> predicate, String errorMessage) {
        Objects.requireNonNull(errorMessage, "errorMessage");
        return from(predicate, ignored -> errorMessage);
    }

    static <T> ValidationRule<T> from(
            Predicate<? super T> predicate,
            Function<? super T, String> errorMessageFactory
    ) {
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(errorMessageFactory, "errorMessageFactory");
        return value -> predicate.test(value)
                ? Optional.empty()
                : Optional.of(errorMessageFactory.apply(value));
    }
}
