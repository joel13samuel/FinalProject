package oop.project.library.argument;

public class BooleanArgumentType implements ArgumentType<Boolean> {

    public Boolean parse(String value) throws ArgumentParseException {
        if (value.equals("true")) {
            return true;
        }

        if (value.equals("false")) {
            return false;
        }

        throw new ArgumentParseException("Value must be true or false.");
    }
}
