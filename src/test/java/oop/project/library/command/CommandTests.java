package oop.project.library.command;

import oop.project.library.argument.IntegerArgumentType;
import oop.project.library.argument.StringArgumentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandTests {

    @Test
    void commandRejectsDuplicateArgumentNames() {
        var command = new Command().addPositional("value", new IntegerArgumentType());

        var exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.addNamed("value", new StringArgumentType())
        );

        Assertions.assertEquals("Argument name already exists: value.", exception.getMessage());
    }

    @Test
    void commandRejectsUnexpectedNamedArguments() {
        var command = new Command().addPositional("term", new StringArgumentType());

        var exception = Assertions.assertThrows(
                CommandParseException.class,
                () -> command.parse("apple --extra value")
        );

        Assertions.assertEquals("Unexpected named argument: --extra.", exception.getMessage());
    }

    @Test
    void commandRejectsAliasesForUnknownNamedArguments() {
        var command = new Command();

        var exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> command.addAlias("i", "case-insensitive")
        );

        Assertions.assertEquals(
                "Cannot add alias for unknown named argument: case-insensitive.",
                exception.getMessage()
        );
    }

    @Test
    void parsedCommandSupportsCheckedTypedExtraction() {
        var parsed = new Command()
                .addPositional("value", new IntegerArgumentType())
                .parse("12");

        Assertions.assertEquals(12, parsed.get("value", Integer.class));

        var exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> parsed.get("value", String.class)
        );

        Assertions.assertEquals("Argument 'value' is a Integer, not a String.", exception.getMessage());
    }
}
