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

    /**
     * Adds a required positional argument to this command.
     * Positional arguments are parsed in the order they are added.
     * The argument value is parsed using the provided {@link ArgumentType}.
     *
     * @param name the name of the argument, used as the key in the parsed result map
     * @param type the {@link ArgumentType} used to parse and validate the argument value
     * @param <T>  the type produced by parsing
     * @return this {@code Command} instance for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addPositional(String name, ArgumentType<T> type) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        positionalNames.add(name);
        positionalTypes.add(type);
        return this;
    }

    /**
     * Adds an optional positional argument with a default value to this command.
     * If the argument is not provided, the default value is used in the parsed result.
     *
     * @param name         the name of the argument, used as the key in the parsed result map
     * @param type         the {@link ArgumentType} used to parse and validate the argument value
     * @param defaultValue the value to use if the argument is not provided
     * @param <T>          the type produced by parsing
     * @return this {@code Command} instance for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addPositional(String name, ArgumentType<T> type, T defaultValue) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        positionalNames.add(name);
        positionalTypes.add(type);
        namedDefaults.put("__positional__" + name, defaultValue);
        return this;
    }

    /**
     * Adds a required named argument to this command.
     * Named arguments are passed using {@code --name value} syntax.
     * The argument value is parsed using the provided {@link ArgumentType}.
     *
     * @param name the name of the argument, used as the key in the parsed result map
     * @param type the {@link ArgumentType} used to parse and validate the argument value
     * @param <T>  the type produced by parsing
     * @return this {@code Command} instance for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addNamed(String name, ArgumentType<T> type) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        namedTypes.put(name, type);
        return this;
    }

    /**
     * Adds an optional named argument with a default value to this command.
     * Named arguments are passed using {@code --name value} syntax.
     * If the argument is not provided, the default value is used in the parsed result.
     * Boolean named arguments support no-value flag syntax (e.g. {@code --flag} or {@code -flag})
     * which sets the value to {@code true}.
     *
     * @param name         the name of the argument, used as the key in the parsed result map
     * @param type         the {@link ArgumentType} used to parse and validate the argument value
     * @param defaultValue the value to use if the argument is not provided
     * @param <T>          the type produced by parsing
     * @return this {@code Command} instance for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addNamed(String name, ArgumentType<T> type, T defaultValue) {
        validateArgumentName(name);
        requireUnusedName(name);
        Objects.requireNonNull(type, "type");
        namedTypes.put(name, type);
        namedDefaults.put(name, defaultValue);
        return this;
    }

    /**
     * Adds an alias for an existing named argument.
     * The alias can be used interchangeably with the canonical name when parsing.
     * The result map always uses the canonical name as the key.
     *
     * @param alias         the alias to register
     * @param canonicalName the canonical named argument this alias maps to
     * @return this {@code Command} instance for chaining
     * @throws IllegalArgumentException if either name is blank, the canonical name is not registered,
     *                                  or the alias is already in use
     */
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

    /**
     * Adds a named subcommand to this command.
     * When subcommands are registered, the first token of the input is used to select
     * which subcommand to delegate parsing to. Each subcommand can have its own
     * argument structure.
     *
     * @param name       the subcommand name, matched against the first input token
     * @param subcommand the {@code Command} defining the subcommand's argument structure
     * @return this {@code Command} instance for chaining
     * @throws IllegalArgumentException if the name is blank
     */
    public Command addSubcommand(String name, Command subcommand) {
        validateArgumentName(name);
        Objects.requireNonNull(subcommand, "subcommand");
        subcommands.put(name, subcommand);
        return this;
    }

    /**
     * Parses the given argument string according to this command's registered arguments.
     * Positional arguments are matched by position; named arguments are matched by
     * {@code --name value} or {@code -name} syntax. Aliases are resolved to their
     * canonical names. If subcommands are registered, the first token selects the
     * subcommand and the rest of the string is delegated to it.
     *
     * @param arguments the raw argument string to parse (not including the command name)
     * @return a {@link ParsedCommand} containing the parsed and typed argument values
     * @throws CommandParseException if the arguments are invalid, missing, or unexpected
     */
    public ParsedCommand parse(String arguments) {
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