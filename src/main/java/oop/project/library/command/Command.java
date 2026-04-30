package oop.project.library.command;

import oop.project.library.argument.ArgumentType;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

public final class Command {

    private record PositionalArgument(String name, ArgumentType<?> type, Optional<Object> defaultValue) {}
    private record NamedArgument(String name, ArgumentType<?> type, Optional<Object> defaultValue) {}

    private final ArrayList<PositionalArgument> positionals = new ArrayList<>();
    private final LinkedHashMap<String, NamedArgument> named = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> aliases = new LinkedHashMap<>();
    private final LinkedHashMap<String, Command> subcommands = new LinkedHashMap<>();

    /**
     * Adds a required positional argument to this command.
     * Positional arguments are parsed in the order they are added.
     *
     * @param name the name used as the key in the parsed result
     * @param type the {@link ArgumentType} used to parse the value
     * @param <T>  the parsed type
     * @return this {@code Command} for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addPositional(String name, ArgumentType<T> type) {
        requireValidNewName(name);
        Objects.requireNonNull(type, "type");
        positionals.add(new PositionalArgument(name, type, Optional.empty()));
        return this;
    }

    /**
     * Adds an optional positional argument with a default value.
     * If not provided in the input, the default is used in the result.
     *
     * @param name         the name used as the key in the parsed result
     * @param type         the {@link ArgumentType} used to parse the value
     * @param defaultValue the value to use if the argument is absent
     * @param <T>          the parsed type
     * @return this {@code Command} for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addPositional(String name, ArgumentType<T> type, T defaultValue) {
        requireValidNewName(name);
        Objects.requireNonNull(type, "type");
        positionals.add(new PositionalArgument(name, type, Optional.ofNullable(defaultValue)));
        return this;
    }

    /**
     * Adds a required named argument to this command.
     * Named arguments use {@code --name value} syntax in the input.
     *
     * @param name the name used as the key in the parsed result
     * @param type the {@link ArgumentType} used to parse the value
     * @param <T>  the parsed type
     * @return this {@code Command} for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addNamed(String name, ArgumentType<T> type) {
        requireValidNewName(name);
        Objects.requireNonNull(type, "type");
        named.put(name, new NamedArgument(name, type, Optional.empty()));
        return this;
    }

    /**
     * Adds an optional named argument with a default value.
     * Named arguments use {@code --name value} or {@code -name} (boolean flag) syntax.
     * Boolean arguments support no-value flag syntax which sets the value to {@code true}.
     *
     * @param name         the name used as the key in the parsed result
     * @param type         the {@link ArgumentType} used to parse the value
     * @param defaultValue the value to use if the argument is absent
     * @param <T>          the parsed type
     * @return this {@code Command} for chaining
     * @throws IllegalArgumentException if the name is blank or already in use
     */
    public <T> Command addNamed(String name, ArgumentType<T> type, T defaultValue) {
        requireValidNewName(name);
        Objects.requireNonNull(type, "type");
        named.put(name, new NamedArgument(name, type, Optional.ofNullable(defaultValue)));
        return this;
    }

    /**
     * Adds an alias for an existing named argument.
     * The alias can be used in place of the canonical name; the result map always uses the canonical key.
     *
     * @param alias         the alias to register
     * @param canonicalName the canonical named argument this alias maps to
     * @return this {@code Command} for chaining
     * @throws IllegalArgumentException if alias is blank/taken, or canonical is not a registered named argument
     */
    public Command addAlias(String alias, String canonicalName) {
        Objects.requireNonNull(alias, "alias");
        Objects.requireNonNull(canonicalName, "canonicalName");
        if (alias.isBlank()) {
            throw new IllegalArgumentException("Argument name must not be blank.");
        }
        if (!named.containsKey(canonicalName)) {
            throw new IllegalArgumentException("Cannot add alias for unknown named argument: " + canonicalName + ".");
        }
        if (isNameTaken(alias)) {
            throw new IllegalArgumentException("Argument name already exists: " + alias + ".");
        }
        aliases.put(alias, canonicalName);
        return this;
    }

    /**
     * Adds a named subcommand with its own argument structure.
     * When subcommands are registered, the first input token selects which subcommand to delegate to.
     *
     * @param name       the subcommand name matched against the first token
     * @param subcommand the {@code Command} defining the subcommand's arguments
     * @return this {@code Command} for chaining
     * @throws IllegalArgumentException if the name is blank
     */
    public Command addSubcommand(String name, Command subcommand) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(subcommand, "subcommand");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Argument name must not be blank.");
        }
        subcommands.put(name, subcommand);
        return this;
    }

    /**
     * Parses the given argument string according to this command's registered arguments.
     * Uses {@link Input} for tokenization. Positional arguments are matched by position;
     * named arguments by {@code --name value} or {@code -name} syntax with alias resolution.
     * If subcommands are registered, the first token selects the subcommand.
     *
     * @param arguments the raw argument string to parse
     * @return a {@link ParsedCommand} containing the typed argument values
     * @throws CommandParseException if arguments are invalid, missing, or unexpected
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
        var positionalTokens = new ArrayList<String>();
        var namedTokens = new LinkedHashMap<String, String>();

        Input.Value token;
        while ((token = input.parseValue().orElse(null)) != null) {
            switch (token) {
                case Input.Value.Literal(String v) -> positionalTokens.add(v);
                case Input.Value.QuotedString(String v) -> positionalTokens.add(v);
                case Input.Value.SingleFlag(String flagName) -> {
                    if (flagName.matches("\\d+(\\.\\d+)?")) {
                        positionalTokens.add("-" + flagName);
                    } else {
                        String canonical = aliases.getOrDefault(flagName, flagName);
                        Input.Value next = input.parseValue().orElse(null);
                        if (next instanceof Input.Value.Literal(String v)) {
                            namedTokens.put(canonical, v);
                        } else if (next == null) {
                            namedTokens.put(canonical, "");
                        } else {
                            throw new CommandParseException("Unexpected token after -" + flagName + ".");
                        }
                    }
                }
                case Input.Value.DoubleFlag(String flagName) -> {
                    String canonical = aliases.getOrDefault(flagName, flagName);
                    Input.Value next = input.parseValue().orElse(null);
                    if (next instanceof Input.Value.Literal(String v)) {
                        namedTokens.put(canonical, v);
                    } else if (next instanceof Input.Value.SingleFlag(String f) && f.matches("\\d+(\\.\\d+)?")) {
                        namedTokens.put(canonical, "-" + f);
                    } else if (next == null) {
                        namedTokens.put(canonical, "");
                    } else {
                        throw new CommandParseException("Unexpected token after --" + flagName + ".");
                    }
                }
            }
        }

        if (positionalTokens.size() > positionals.size()) {
            throw new CommandParseException(
                    "Expected " + positionals.size() + " positional argument(s), got " + positionalTokens.size() + "."
            );
        }
        for (int i = 0; i < positionals.size(); i++) {
            var arg = positionals.get(i);
            if (i < positionalTokens.size()) {
                result.put(arg.name(), arg.type().parse(positionalTokens.get(i)));
            } else if (arg.defaultValue().isPresent()) {
                result.put(arg.name(), arg.defaultValue().get());
            } else {
                throw new CommandParseException(
                        "Expected " + positionals.size() + " positional argument(s), got " + positionalTokens.size() + "."
                );
            }
        }

        for (String tokenName : namedTokens.keySet()) {
            if (!named.containsKey(tokenName)) {
                throw new CommandParseException("Unexpected named argument: --" + tokenName + ".");
            }
        }

        for (var arg : named.values()) {
            if (namedTokens.containsKey(arg.name())) {
                String raw = namedTokens.get(arg.name());
                if (raw.isEmpty()) {
                    if (arg.defaultValue().isPresent() && arg.defaultValue().get() instanceof Boolean) {
                        result.put(arg.name(), true);
                    } else {
                        throw new CommandParseException("Missing value for --" + arg.name() + ".");
                    }
                } else {
                    result.put(arg.name(), arg.type().parse(raw));
                }
            } else if (arg.defaultValue().isPresent()) {
                result.put(arg.name(), arg.defaultValue().get());
            } else {
                throw new CommandParseException("Missing required named argument: --" + arg.name() + ".");
            }
        }

        return new ParsedCommand(result);
    }

    private void requireValidNewName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Argument name must not be blank.");
        }
        if (isNameTaken(name)) {
            throw new IllegalArgumentException("Argument name already exists: " + name + ".");
        }
    }

    private boolean isNameTaken(String name) {
        return positionals.stream().anyMatch(p -> p.name().equals(name))
                || named.containsKey(name)
                || aliases.containsKey(name);
    }

}