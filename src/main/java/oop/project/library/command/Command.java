package oop.project.library.command;

import oop.project.library.argument.ArgumentType;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Command {

    private final List<String> positionalNames = new ArrayList<>();
    private final List<ArgumentType<?>> positionalTypes = new ArrayList<>();
    private final Map<String, ArgumentType<?>> namedTypes = new LinkedHashMap<>();
    private final Map<String, Object> namedDefaults = new LinkedHashMap<>();
    private final Map<String, String> namedAliases = new LinkedHashMap<>();
    private final Map<String, Command> subcommands = new LinkedHashMap<>();

    public <T> Command addPositional(String name, ArgumentType<T> type) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        positionalNames.add(name);
        positionalTypes.add(type);
        return this;
    }

    public <T> Command addPositional(String name, ArgumentType<T> type, T defaultValue) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        positionalNames.add(name);
        positionalTypes.add(type);
        namedDefaults.put("__positional__" + name, defaultValue);
        return this;
    }

    public <T> Command addNamed(String name, ArgumentType<T> type) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        namedTypes.put(name, type);
        return this;
    }

    public <T> Command addNamed(String name, ArgumentType<T> type, T defaultValue) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        namedTypes.put(name, type);
        namedDefaults.put(name, defaultValue);
        return this;
    }

    public Command addAlias(String alias, String canonicalName) {
        validateArgumentName(alias);
        validateArgumentName(canonicalName);
        if (!namedTypes.containsKey(canonicalName)) {
            throw new IllegalArgumentException("Cannot add alias for unknown named argument: " + canonicalName + ".");
        }
        requireUnusedName(alias);
        namedAliases.put(alias, canonicalName);
        return this;
    }

    public Command addSubcommand(String name, Command subcommand) {
        validateArgumentName(name);
        Objects.requireNonNull(subcommand, "subcommand");
        subcommands.put(name, subcommand);
        return this;
    }

    public ParsedCommand parse(String arguments) {
        // if subcommands are registered, delegate to the matching one
        if (!subcommands.isEmpty()) {
            var input = new Input(arguments);
            Input.Value first = input.parseValue().orElse(null);
            if (first instanceof Input.Value.Literal(String name) && subcommands.containsKey(name)) {
                return subcommands.get(name).parse(arguments);
            }
            throw new CommandParseException("Unknown subcommand: " + (first == null ? "<none>" : first) + ".");
        }

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
                            throw new CommandParseException("Unexpected token after -" + name + ".");
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
                        named.put(canonical, "-" + flagName);
                    } else if (next == null) {
                        named.put(canonical, "");
                    } else {
                        throw new CommandParseException("Unexpected token after --" + name + ".");
                    }
                }
            }
        }

        if (positionals.size() > positionalNames.size()) {
            throw new CommandParseException(
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
                throw new CommandParseException(
                        "Expected " + positionalNames.size() + " positional argument(s), got " + positionals.size() + "."
                );
            }
        }

        for (String name : named.keySet()) {
            if (!namedTypes.containsKey(name)) {
                throw new CommandParseException("Unexpected named argument: --" + name + ".");
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
                        throw new CommandParseException("Missing value for --" + name + ".");
                    }
                } else {
                    result.put(name, entry.getValue().parse(raw));
                }
            } else if (namedDefaults.containsKey(name)) {
                result.put(name, namedDefaults.get(name));
            } else {
                throw new CommandParseException("Missing required named argument: --" + name + ".");
            }
        }

        return new ParsedCommand(result);
    }

    private void validateArgumentName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Argument name must not be blank.");
        }
    }

    private void requireUnusedName(String name) {
        if (positionalNames.contains(name) || namedTypes.containsKey(name) || namedAliases.containsKey(name)) {
            throw new IllegalArgumentException("Argument name already exists: " + name + ".");
        }
    }

}