package oop.project.library.scenarios;

import oop.project.library.argument.*;
import oop.project.library.command.Command;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("left", new IntegerArgumentType())
                    .addPositional("right", new IntegerArgumentType())
                    .parse(arguments);

            int left = parsed.get("left", Integer.class);
            int right = parsed.get("right", Integer.class);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addNamed("left", new DoubleArgumentType())
                    .addNamed("right", new DoubleArgumentType())
                    .parse(arguments);

            double left = parsed.get("left", Double.class);
            double right = parsed.get("right", Double.class);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("message", new StringArgumentType(), "echo,echo,echo...")
                    .parse(arguments);

            String message = parsed.get("message", String.class);
            return Map.of("message", message);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("term", new StringArgumentType())
                    .addNamed("case-insensitive", new BooleanArgumentType(), false)
                    .addAlias("i", "case-insensitive")
                    .parse(arguments);

            String term = parsed.get("term", String.class);
            boolean caseInsensitive = parsed.get("case-insensitive", Boolean.class);
            return Map.of("term", term, "case-insensitive", caseInsensitive);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            String type = new Command()
                    .addPositional("type", new ChoiceStringArgumentType(java.util.List.of("static", "dynamic")))
                    .addPositional("value", new StringArgumentType())
                    .parse(arguments)
                    .get("type", String.class);

            if (type.equals("static")) {
                var parsed = new Command()
                        .addPositional("type", new ChoiceStringArgumentType(java.util.List.of("static", "dynamic")))
                        .addPositional("value", new IntegerArgumentType())
                        .parse(arguments);

                return Map.of(
                        "type", parsed.get("type", String.class),
                        "value", parsed.get("value", Integer.class)
                );
            } else {
                var parsed = new Command()
                        .addPositional("type", new ChoiceStringArgumentType(java.util.List.of("static", "dynamic")))
                        .addPositional("value", new StringArgumentType())
                        .parse(arguments);

                return Map.of(
                        "type", parsed.get("type", String.class),
                        "value", parsed.get("value", String.class)
                );
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
