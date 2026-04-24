package oop.project.library.command;

import java.util.Map;

public final class ParsedCommand {

    private final Map<String, Object> values;

    ParsedCommand(Map<String, Object> values) {
        this.values = Map.copyOf(values);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("No argument found with name: " + name + ".");
        }
        return (T) values.get(name);
    }

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

    public Map<String, Object> toMap() {
        return values;
    }

}
