package oop.project.library.argument;

public interface ArgumentType<T> {
    T parse(String value) throws ArgumentParseException;
}
