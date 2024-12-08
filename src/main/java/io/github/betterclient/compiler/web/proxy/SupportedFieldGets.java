package io.github.betterclient.compiler.web.proxy;

public class SupportedFieldGets {
    public static Object get(Class<?> fieldOwner, String name, Object instance) {
        if (fieldOwner == System.class) {
            return handleSystemGet(name);
        }

        throw new UnsupportedOperationException(fieldOwner.getName() + " . " + name + " is unsupported.");
    }

    private static Object handleSystemGet(String name) {
        return switch (name) {
            case "out" -> System.out;
            case "err" -> System.err;
            case "in" -> System.in;
            default -> throw new UnsupportedOperationException("java.lang.System . " + name + " is unsupported.");
        };
    }
}