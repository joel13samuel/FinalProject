package oop.project.library.scenarios;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import oop.project.library.argument.*;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public final class ArgumentScenarios {

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 2 || !args.named().isEmpty()) {
            throw new RuntimeException("add expects exactly 2 positional arguments.");
        }

        IntegerArgumentType integerArgumentType = new IntegerArgumentType();

        try {
            String leftString = args.positional().get(0);
            String rightString = args.positional().get(1);

            int left = integerArgumentType.parse(leftString);
            int right = integerArgumentType.parse(rightString);

            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("add arguments must be integers.");
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 2 || !args.named().isEmpty()) {
            throw new RuntimeException("sub expects exactly 2 positional arguments.");
        }

        DoubleArgumentType doubleArgumentType = new DoubleArgumentType();

        String leftString = args.positional().get(0);
        String rightString = args.positional().get(1);

        if ((leftString.startsWith("-") && leftString.contains(".")) ||
                (rightString.startsWith("-") && rightString.contains("."))) {
            throw new RuntimeException("Negative decimal values are not supported.");
        }

        try {
            double left = doubleArgumentType.parse(leftString);
            double right = doubleArgumentType.parse(rightString);

            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("sub arguments must be doubles.");
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 1 || !args.named().isEmpty()) {
            throw new RuntimeException("fizzbuzz only takes exactly 1 positional argument.");
        }

        RangedIntegerArgumentType rangedIntegerArgumentType =
                new RangedIntegerArgumentType(1, Integer.MAX_VALUE);

        String numberString = args.positional().get(0);

        try {
            int number = rangedIntegerArgumentType.parse(numberString);
            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("fizzbuzz argument must be a positive integer.");
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 1 || !args.named().isEmpty()) {
            throw new RuntimeException("difficulty only takes exactly 1 positional argument.");
        }

        ChoiceStringArgumentType choiceStringArgumentType =
                new ChoiceStringArgumentType(List.of("easy", "medium", "hard"));

        String difficultyString = args.positional().get(0);

        try {
            String difficulty = choiceStringArgumentType.parse(difficultyString);
            return Map.of("difficulty", difficulty);
        } catch (RuntimeException e) {
            throw new RuntimeException("difficulty must be easy, medium, or hard.");
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 1 || !args.named().isEmpty()) {
            throw new RuntimeException("date expects exactly 1 positional argument.");
        }

        CustomArgumentType<LocalDate> dateArgumentType = new CustomArgumentType<>(LocalDate::parse);

        String dateString = args.positional().get(0);

        try {
            LocalDate date = dateArgumentType.parse(dateString);
            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("date argument must be a valid date.");
        }
    }

}
