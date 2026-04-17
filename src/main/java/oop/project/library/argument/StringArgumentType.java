package oop.project.library.argument;

public class StringArgumentType implements ArgumentType<String> {

    public String parse(String value) throws ArgumentParseException {
        return value;
    }
}
