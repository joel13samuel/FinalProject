package oop.project.library.argument;

public interface ArgumentType<T> {

    /**
     * Parses a raw string argument into a typed value.
     *
     * @param value the raw string value to parse
     * @return the parsed typed value
     * @throws ArgumentParseException if the value cannot be parsed into this argument type
     */
    T parse(String value) throws ArgumentParseException;
}
