package oop.project.library.command;

import java.util.Map;

public final class ParsedCommand {

    private final Map<String, Object> values;

    ParsedCommand(Map<String, Object> values) {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        if (!values.containsKey(name)) {
            throw new RuntimeException("No argument found with name: " + name);
        }
        return (T) values.get(name);
    }

    public Map<String, Object> toMap() {
        return values;
    }

}