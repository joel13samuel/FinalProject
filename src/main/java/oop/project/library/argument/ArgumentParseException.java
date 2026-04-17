package oop.project.library.argument;

public class ArgumentParseException extends RuntimeException {

    public ArgumentParseException(String message) {
        super(message);
    }

    public ArgumentParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
