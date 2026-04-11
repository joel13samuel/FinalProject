package oop.project.library.scenarios;

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
        Input input = new Input(arguments);
        // parse term (first positional)
        String term = switch (input.parseValue().orElse(null)) {
            case Input.Value.Literal(String v) -> v;
            case Input.Value.QuotedString(String v) -> v;
            case null, default -> throw new RuntimeException("search requires a term.");
        };
        // parse optional --case-insensitive / -i flag
        boolean caseInsensitive = false;
        Input.Value flagToken = input.parseValue().orElse(null);
        if (flagToken != null) {
            String flagName = switch (flagToken) {
                case Input.Value.DoubleFlag(String n) -> n;
                case Input.Value.SingleFlag(String n) -> n;
                default -> throw new RuntimeException("Unexpected token after term.");
            };
            if (!flagName.equals("case-insensitive") && !flagName.equals("i"))
                throw new RuntimeException("Unknown flag: " + flagName);
            // peek at next token for optional value
            Input.Value nextToken = input.parseValue().orElse(null);
            if (nextToken == null) {
                caseInsensitive = true; // flag present, no value
            } else if (nextToken instanceof Input.Value.Literal(String raw)) {
                try {
                    caseInsensitive = new BooleanArgumentType().parse(raw);
                } catch (RuntimeException e) {
                    throw new RuntimeException("case-insensitive must be true or false.");
                }
            } else {
                throw new RuntimeException("case-insensitive must be true or false.");
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