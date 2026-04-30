package oop.project.library.command;

import java.util.Map;

public final class ParsedCommand {

    private final Map<String, Object> values;

    ParsedCommand(Map<String, Object> values) {
        this.values = Map.copyOf(values);
    }

    /**
     * Retrieves the parsed value for the given argument name without a type check.
     * The caller is responsible for knowing the correct type; an incorrect type
     * assumption will result in a {@link ClassCastException} at runtime.
     *
     * @param name the argument name to look up
     * @param <T>  the expected return type
     * @return the parsed value associated with the given name
     * @throws IllegalArgumentException if no argument with the given name exists
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("No argument found with name: " + name + ".");
        }
        return (T) values.get(name);
    }

    /**
     * Retrieves the parsed value for the given argument name with a runtime type check.
     * This is the preferred method for typed extraction, as it validates that the stored
     * value is actually an instance of the expected type before returning it.
     *
     * @param name the argument name to look up
     * @param type the expected {@link Class} of the value
     * @param <T>  the expected return type
     * @return the parsed value cast to the given type
     * @throws IllegalArgumentException if no argument with the given name exists,
     *                                  or if the stored value is not an instance of {@code type}
     */
    public <T> T get(String name, Class<T> type) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("No argument found with name: " + name + ".");
        }

        Object value = values.get(name);
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Argument '" + name + "' is a " + value.getClass().getSimpleName()
                            + ", not a " + type.getSimpleName() + "."
            );
        }

        return type.cast(value);
    }

    /**
     * Returns an unmodifiable view of all parsed argument values as a map.
     *
     * @return an unmodifiable {@link Map} of argument names to parsed values
     */
    public Map<String, Object> toMap() {
        return values;
    }

}