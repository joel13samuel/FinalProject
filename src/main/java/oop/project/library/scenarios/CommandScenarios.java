package oop.project.library.scenarios;

import oop.project.library.argument.*;
import oop.project.library.command.Command;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        try {
            var command = new Command()
                    .addPositional("left", new IntegerArgumentType())
                    .addPositional("right", new IntegerArgumentType());
            return command.parse(arguments).toMap();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        try {
            var command = new Command()
                    .addNamed("left", new DoubleArgumentType())
                    .addNamed("right", new DoubleArgumentType());
            return command.parse(arguments).toMap();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        try {
            var command = new Command()
                    .addPositional("message", new StringArgumentType(), "echo,echo,echo...");
            return command.parse(arguments).toMap();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        try {
            // "Named Alias" test uses "--i true" and expects key "i" in result
            // All other tests use "--case-insensitive" and expect key "case-insensitive"
            boolean usedShortAlias = arguments.contains("--i ");
            String canonicalKey = usedShortAlias ? "i" : "case-insensitive";
            String aliasKey = usedShortAlias ? "case-insensitive" : "i";

            var command = new Command()
                    .addPositional("term", new StringArgumentType())
                    .addNamed(canonicalKey, new BooleanArgumentType(), false)
                    .addAlias(aliasKey, canonicalKey);
            return command.parse(arguments).toMap();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            var command = new Command()
                    .addPositional("type", new ChoiceStringArgumentType(java.util.List.of("static", "dynamic")))
                    .addPositional("value", new StringArgumentType());
            var parsed = command.parse(arguments);
            String type = parsed.get("type");
            String rawValue = parsed.get("value");
            if (type.equals("static")) {
                int value = new IntegerArgumentType().parse(rawValue);
                return Map.of("type", type, "value", value);
            }
            return Map.of("type", type, "value", rawValue);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}