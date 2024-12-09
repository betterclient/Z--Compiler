package io.github.betterclient.compiler.web.proxy;

import java.io.PrintStream;

public class SupportedMethodCalls {
    public static Object call(Class<?> targetClass, String name, Object instance, Object[] otherArguments) {
        if(targetClass == System.class) {
            return handleSystem(name, otherArguments);
        } else if (targetClass == PrintStream.class) {
            handlePrintStream(name, (PrintStream) instance, otherArguments);
            return null;
        } else if (targetClass == String.class) {
            return handleString((String) instance, name, otherArguments);
        }

        throw new UnsupportedOperationException(targetClass.getName() + " . " + name + " is unsupported.");
    }

    private static Object handleString(String instance, String name, Object[] otherArguments) {
        return switch (name) {
            case "toString" -> instance;
            case "trim" -> instance.trim();
            case "substring" -> instance.substring((Integer) otherArguments[0], (Integer) otherArguments[1]);
            case "equals" -> instance.equals(otherArguments[0]);
            case "concat" -> instance.concat((String) otherArguments[0]);
            case "replace" -> instance.replace((String) otherArguments[0], (String) otherArguments[1]);
            default -> throw new UnsupportedOperationException("java.lang.String . " + name + "() is unsupported.");
        };
    }

    private static void handlePrintStream(String name, PrintStream instance, Object[] otherArguments) {
        switch (name) {
            case "println" -> instance.println(otherArguments[0]);
            case "print" -> instance.print(otherArguments[0]);
            default -> throw new UnsupportedOperationException("java.io.PrintStream . " + name + "() is unsupported.");
        };
    }

    private static Object handleSystem(String name, Object[] otherArguments) {
        Object a = null;
        switch (name) {
            case "currentTimeMillis" -> a = System.currentTimeMillis();
            case "nanoTime" -> a = System.nanoTime();
            case "getProperty" -> a = System.getProperty((String) otherArguments[0]);
            case "clearProperty" -> a = System.clearProperty((String) otherArguments[0]);
            case "getenv" -> a = System.getenv((String) otherArguments[0]);
            case "identityHashCode" -> a = System.identityHashCode(otherArguments[0]);

            default -> throw new UnsupportedOperationException("java.lang.System . " + name + "() is unsupported.");
        }

        return a;
    }
}
