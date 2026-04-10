package oop.project.library.argument;

public class StringArgumentType implements ArgumentType<String> {

    public String parse(String value) throws RuntimeException {
        return value;
    }
}