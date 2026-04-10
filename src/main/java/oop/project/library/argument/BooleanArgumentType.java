package oop.project.library.argument;

public class BooleanArgumentType implements ArgumentType<Boolean> {

    public Boolean parse(String value) throws RuntimeException {
        if (value.equals("true")) {
            return true;
        }

        if (value.equals("false")) {
            return false;
        }

        throw new RuntimeException("Value must be true or false.");
    }
}