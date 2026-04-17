package oop.project.library.argument;

import java.util.Arrays;

public class EnumArgumentType<E extends Enum<E>> implements ArgumentType<E> {

    private final Class<E> enumType;
    private final boolean caseSensitive;

    public EnumArgumentType(Class<E> enumType) {
        this(enumType, false);
    }

    public EnumArgumentType(Class<E> enumType, boolean caseSensitive) {
        this.enumType = enumType;
        this.caseSensitive = caseSensitive;
    }

    public E parse(String value) throws RuntimeException {
        for (E constant : enumType.getEnumConstants()) {
            if (matches(constant.name(), value)) {
                return constant;
            }
        }

        throw new RuntimeException("Value must be one of: " + Arrays.toString(enumType.getEnumConstants()) + ".");
    }

    private boolean matches(String enumName, String value) {
        if (caseSensitive) {
            return enumName.equals(value);
        }

        return enumName.equalsIgnoreCase(value);
    }
}
