package oop.project.library.scenarios;

import oop.project.library.argument.CustomArgumentType;
import oop.project.library.argument.DoubleArgumentType;
import oop.project.library.argument.EnumArgumentType;
import oop.project.library.argument.IntegerArgumentType;
import oop.project.library.argument.RangedArgumentType;
import oop.project.library.command.Command;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public final class ArgumentScenarios {

    private enum Difficulty {
        PEACEFUL, EASY, NORMAL, HARD
    }

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("left", new IntegerArgumentType())
                    .addPositional("right", new IntegerArgumentType())
                    .parse(arguments);

            int left = parsed.get("left", Integer.class);
            int right = parsed.get("right", Integer.class);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("add arguments must be integers.", e);
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("left", new DoubleArgumentType())
                    .addPositional("right", new DoubleArgumentType())
                    .parse(arguments);

            double left = parsed.get("left", Double.class);
            double right = parsed.get("right", Double.class);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("sub arguments must be doubles.", e);
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("number", new RangedArgumentType<>(new IntegerArgumentType(), 1, 100))
                    .parse(arguments);

            int number = parsed.get("number", Integer.class);
            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("fizzbuzz argument must be a positive integer from 1 to 100.", e);
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("difficulty", new EnumArgumentType<>(Difficulty.class))
                    .parse(arguments);

            Difficulty difficulty = parsed.get("difficulty", Difficulty.class);
            return Map.of("difficulty", difficulty.name().toLowerCase(Locale.ROOT));
        } catch (RuntimeException e) {
            throw new RuntimeException("difficulty must be peaceful, easy, normal, or hard.", e);
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            var parsed = new Command()
                    .addPositional("date", new CustomArgumentType<>(LocalDate::parse))
                    .parse(arguments);

            LocalDate date = parsed.get("date", LocalDate.class);
            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("date argument must be a valid date.", e);
        }
    }

}
