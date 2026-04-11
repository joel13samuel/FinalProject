package oop.project.library.command;

import oop.project.library.argument.BooleanArgumentType;
import oop.project.library.argument.DoubleArgumentType;
import oop.project.library.argument.IntegerArgumentType;
import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.Map;

public final class CommandScenarios {

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        BasicArgs args = new Input(arguments).parseBasicArgs();
        if (args.positional().size() != 2 || !args.named().isEmpty())
            throw new RuntimeException("mul expects exactly 2 positional arguments.");
        IntegerArgumentType type = new IntegerArgumentType();
        try {
            int left = type.parse(args.positional().get(0));
            int right = type.parse(args.positional().get(1));
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("mul arguments must be integers.");
        }
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        BasicArgs args = new Input(arguments).parseBasicArgs();
        if (!args.named().containsKey("left") || !args.named().containsKey("right"))
            throw new RuntimeException("div requires --left and --right.");
        String leftStr = args.named().get("left");
        String rightStr = args.named().get("right");
        if ((leftStr.startsWith("-") && leftStr.contains(".")) ||
                (rightStr.startsWith("-") && rightStr.contains(".")))
            throw new RuntimeException("Negative decimal values are not supported.");
        DoubleArgumentType type = new DoubleArgumentType();
        try {
            double left = type.parse(leftStr);
            double right = type.parse(rightStr);
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("div arguments must be doubles.");
        }
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        BasicArgs args = new Input(arguments).parseBasicArgs();
        String message = args.positional().isEmpty() ? "echo,echo,echo..." : args.positional().get(0);
        return Map.of("message", message);
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        BasicArgs args = new Input(arguments).parseBasicArgs();
        if (args.positional().isEmpty())
            throw new RuntimeException("search requires a term.");
        String term = args.positional().get(0);
        boolean caseInsensitive = false;
        if (args.named().containsKey("case-insensitive") || args.named().containsKey("i")) {
            String key = args.named().containsKey("case-insensitive") ? "case-insensitive" : "i";
            String raw = args.named().get(key);
            if (raw.isEmpty()) {
                caseInsensitive = true;
            } else {
                try {
                    caseInsensitive = new BooleanArgumentType().parse(raw);
                } catch (RuntimeException e) {
                    throw new RuntimeException("case-insensitive must be true or false.");
                }
            }
        }
        return Map.of("term", term, "case-insensitive", caseInsensitive);
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        BasicArgs args = new Input(arguments).parseBasicArgs();
        if (args.positional().size() != 2)
            throw new RuntimeException("dispatch expects exactly 2 positional arguments.");
        String type = args.positional().get(0);
        String rawValue = args.positional().get(1);
        if (!type.equals("static") && !type.equals("dynamic"))
            throw new RuntimeException("dispatch type must be static or dynamic.");
        if (type.equals("static")) {
            try {
                int value = new IntegerArgumentType().parse(rawValue);
                return Map.of("type", type, "value", value);
            } catch (RuntimeException e) {
                throw new RuntimeException("dispatch static value must be an integer.");
            }
        }
        return Map.of("type", type, "value", rawValue);
    }

}
