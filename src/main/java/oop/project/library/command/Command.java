package oop.project.library.command;

import oop.project.library.argument.ArgumentType;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Command {

    private final List<String> positionalNames = new ArrayList<>();
    private final List<ArgumentType<?>> positionalTypes = new ArrayList<>();
    private final Map<String, ArgumentType<?>> namedTypes = new LinkedHashMap<>();
    private final Map<String, Object> namedDefaults = new LinkedHashMap<>();
    private final Map<String, String> namedAliases = new LinkedHashMap<>();

    public <T> Command addPositional(String name, ArgumentType<T> type) {
        positionalNames.add(name);
        positionalTypes.add(type);
        return this;
    }

    public <T> Command addPositional(String name, ArgumentType<T> type, T defaultValue) {
        positionalNames.add(name);
        positionalTypes.add(type);
        namedDefaults.put("__positional__" + name, defaultValue);
        return this;
    }

    public <T> Command addNamed(String name, ArgumentType<T> type) {
        namedTypes.put(name, type);
        return this;
    }

    public <T> Command addNamed(String name, ArgumentType<T> type, T defaultValue) {
        namedTypes.put(name, type);
        namedDefaults.put(name, defaultValue);
        return this;
    }

    public Command addAlias(String alias, String canonicalName) {
        namedAliases.put(alias, canonicalName);
        return this;
    }

    public ParsedCommand parse(String arguments) {
        var input = new Input(arguments);
        var result = new LinkedHashMap<String, Object>();

        var positionals = new ArrayList<String>();
        var named = new LinkedHashMap<String, String>();

        Input.Value token;
        while ((token = input.parseValue().orElse(null)) != null) {
            switch (token) {
                case Input.Value.Literal(String v) -> positionals.add(v);
                case Input.Value.QuotedString(String v) -> positionals.add(v);
                case Input.Value.SingleFlag(String name) -> {
                    // treat -N or -N.N as a negative number literal, not a flag
                    if (name.matches("\\d+(\\.\\d+)?")) {
                        positionals.add("-" + name);
                    } else {
                        String canonical = namedAliases.getOrDefault(name, name);
                        Input.Value next = input.parseValue().orElse(null);
                        if (next instanceof Input.Value.Literal(String v)) {
                            named.put(canonical, v);
                        } else if (next == null) {
                            named.put(canonical, "");
                        } else {
                            throw new RuntimeException("Unexpected token after -" + name + ".");
                        }
                    }
                }
                case Input.Value.DoubleFlag(String name) -> {
                    String canonical = namedAliases.getOrDefault(name, name);
                    Input.Value next = input.parseValue().orElse(null);
                    if (next instanceof Input.Value.Literal(String v)) {
                        named.put(canonical, v);
                    } else if (next instanceof Input.Value.SingleFlag(String flagName)
                            && flagName.matches("\\d+(\\.\\d+)?")) {
                        // e.g. --left -1.5: the -1.5 was parsed as SingleFlag("1.5")
                        named.put(canonical, "-" + flagName);
                    } else if (next == null) {
                        named.put(canonical, "");
                    } else {
                        throw new RuntimeException("Unexpected token after --" + name + ".");
                    }
                }
            }
        }

        if (positionals.size() > positionalNames.size()) {
            throw new RuntimeException(
                    "Expected " + positionalNames.size() + " positional argument(s), got " + positionals.size() + "."
            );
        }
        for (int i = 0; i < positionalNames.size(); i++) {
            String name = positionalNames.get(i);
            String defaultKey = "__positional__" + name;
            if (i < positionals.size()) {
                result.put(name, positionalTypes.get(i).parse(positionals.get(i)));
            } else if (namedDefaults.containsKey(defaultKey)) {
                result.put(name, namedDefaults.get(defaultKey));
            } else {
                throw new RuntimeException(
                        "Expected " + positionalNames.size() + " positional argument(s), got " + positionals.size() + "."
                );
            }
        }

        for (var entry : namedTypes.entrySet()) {
            String name = entry.getKey();
            if (named.containsKey(name)) {
                String raw = named.get(name);
                if (raw.isEmpty()) {
                    if (namedDefaults.get(name) instanceof Boolean) {
                        result.put(name, true);
                    } else {
                        throw new RuntimeException("Missing value for --" + name + ".");
                    }
                } else {
                    result.put(name, entry.getValue().parse(raw));
                }
            } else if (namedDefaults.containsKey(name)) {
                result.put(name, namedDefaults.get(name));
            } else {
                throw new RuntimeException("Missing required named argument: --" + name + ".");
            }
        }

        return new ParsedCommand(result);
    }

}