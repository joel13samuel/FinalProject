package oop.project.library.scenarios;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

public final class ArgumentScenarios {

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 2 || !args.named().isEmpty()) {
            throw new RuntimeException("add only takes exactly 2 positional arguments.");
        }

        try {
            String leftString = args.positional().get(0);
            String rightString = args.positional().get(1);

            int left = Integer.parseInt(leftString);
            int right = Integer.parseInt(rightString);

            return Map.of("left", left, "right", right);
        } catch (NumberFormatException e) {
            throw new RuntimeException("add arguments must be integers.");
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (PoC)");
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 1 || !args.named().isEmpty()) {
            throw new RuntimeException("fizzbuzz only takes exactly 1 positional argument.");
        }

        try {
            String numberString = args.positional().get(0);
            int number = Integer.parseInt(numberString);

            if (number <= 0) {
                throw new RuntimeException("The number must be bigger than 0.");
            }

            return Map.of("number", number);
        } catch (NumberFormatException e) {
            throw new RuntimeException("fizzbuzz argument must be an integer.");
        }

    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        Input input = new Input(arguments);
        BasicArgs args = input.parseBasicArgs();

        if (args.positional().size() != 1 || !args.named().isEmpty()) {
            throw new RuntimeException("difficulty only takes exactly 1 positional argument.");
        }

        String difficulty = args.positional().get(0);

        if (!difficulty.equals("easy") && !difficulty.equals("medium") && !difficulty.equals("hard")) {
            throw new RuntimeException("difficulty must be easy, medium, or hard.");
        }

        return Map.of("difficulty", difficulty);
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO (PoC)");
    }

}
