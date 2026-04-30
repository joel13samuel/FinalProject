package oop.project.library.scenarios;

import oop.project.library.argument.*;
import oop.project.library.command.Command;

import java.util.List;
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
            var command = new Command()
                    .addPositional("term", new StringArgumentType())
                    .addNamed("case-insensitive", new BooleanArgumentType(), false)
                    .addAlias("i", "case-insensitive");
            return command.parse(arguments).toMap();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            var command = new Command()
                    .addSubcommand("static", new Command()
                            .addPositional("type", new ChoiceStringArgumentType(List.of("static")))
                            .addPositional("value", new IntegerArgumentType()))
                    .addSubcommand("dynamic", new Command()
                            .addPositional("type", new ChoiceStringArgumentType(List.of("dynamic")))
                            .addPositional("value", new StringArgumentType()));
            return command.parse(arguments).toMap();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}