package io.github.betterclient.compiler.api.type;

import org.objectweb.asm.Type;

/**
 * Parsing arguments:
 * eg: "String str"
 */
public class Argument {
    /**
     * Type of the argument.
     * eg: "java/lang/String"
     */
    public String type;

    /**
     * Name of the argument.
     * eg: "str"
     */
    public String name;

    /**
     * Amount of arrays
     * eg: 2 for int[][]
     */
    public int arrayCount;

    public Argument(Type type) {
        this.type = type.getClassName().replaceAll("[\\[\\]]", "");
        this.name = "arg";
        this.arrayCount = type.getSort() == Type.ARRAY ? type.getDimensions() : 0;
    }

    public Argument(String type, String name) {
        this(Type.getType("L" + type + ";"));
        this.name = name;
    }

    public Type compile() {
        return Type.getType("[".repeat(Math.max(0, arrayCount)) +
                switch (type) {
                    case "int" -> "I";
                    case "long" -> "J";
                    case "double" -> "D";
                    case "float" -> "F";
                    case "short" -> "S";
                    case "byte" -> "B";
                    case "boolean" -> "Z";
                    case "char" -> "C";
                    case "void" -> "V";
                    default -> "L" + type.replace(".", "/") + ";";
                });
    }
}
