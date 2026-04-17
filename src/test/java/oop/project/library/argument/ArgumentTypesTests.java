package oop.project.library.argument;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArgumentTypesTests {

    private enum Difficulty {
        PEACEFUL, EASY, NORMAL, HARD
    }

    @Test
    void enumArgumentTypeParsesIgnoringCaseByDefault() {
        var type = new EnumArgumentType<>(Difficulty.class);

        Assertions.assertEquals(Difficulty.EASY, type.parse("easy"));
        Assertions.assertEquals(Difficulty.NORMAL, type.parse("NoRmAl"));
    }

    @Test
    void enumArgumentTypeCanRequireExactCasing() {
        var type = new EnumArgumentType<>(Difficulty.class, true);

        Assertions.assertEquals(Difficulty.HARD, type.parse("HARD"));
        Assertions.assertThrows(RuntimeException.class, () -> type.parse("hard"));
    }

    @Test
    void enumArgumentTypeRejectsInvalidValues() {
        var type = new EnumArgumentType<>(Difficulty.class);

        Assertions.assertThrows(RuntimeException.class, () -> type.parse("impossible"));
    }

    @Test
    void regexStringArgumentTypeAcceptsMatchingValues() {
        var type = new RegexStringArgumentType("[A-Z]+-[IV]+");

        Assertions.assertEquals("FIRE-IV", type.parse("FIRE-IV"));
    }

    @Test
    void regexStringArgumentTypeRejectsNonMatchingValues() {
        var type = new RegexStringArgumentType("[A-Z]+-[IV]+");

        Assertions.assertThrows(RuntimeException.class, () -> type.parse("fire-4"));
    }
}
