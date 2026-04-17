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

    @Test
    void validatedArgumentTypeCanWrapExistingParser() {
        var type = new ValidatedArgumentType<>(
                new IntegerArgumentType(),
                value -> value % 2 == 0,
                "Value must be even."
        );

        Assertions.assertEquals(8, type.parse("8"));
    }

    @Test
    void validatedArgumentTypeRejectsValuesThatFailValidation() {
        var type = new ValidatedArgumentType<>(
                new IntegerArgumentType(),
                value -> value % 2 == 0,
                "Value must be even."
        );

        var exception = Assertions.assertThrows(RuntimeException.class, () -> type.parse("7"));
        Assertions.assertEquals("Value must be even.", exception.getMessage());
    }

    @Test
    void choiceStringArgumentTypeStillRejectsUnknownChoices() {
        var type = new ChoiceStringArgumentType(java.util.List.of("peaceful", "easy", "normal", "hard"));

        Assertions.assertEquals("easy", type.parse("easy"));
        Assertions.assertThrows(RuntimeException.class, () -> type.parse("impossible"));
    }

    @Test
    void rangedArgumentTypesStillEnforceBounds() {
        var integerType = new RangedIntegerArgumentType(1, 10);
        var doubleType = new RangedDoubleArgumentType(1.5, 2.5);

        Assertions.assertEquals(5, integerType.parse("5"));
        Assertions.assertEquals(2.0, doubleType.parse("2.0"));
        Assertions.assertThrows(RuntimeException.class, () -> integerType.parse("11"));
        Assertions.assertThrows(RuntimeException.class, () -> doubleType.parse("3.0"));
    }
}
