package io.github.betterclient.compiler.method;

import io.github.betterclient.compiler.Compiler;
import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.api.type.VisibilityType;
import io.github.betterclient.compiler.symbol.Symbol;
import io.github.betterclient.compiler.symbol.UsesSymbol;
import io.github.betterclient.compiler.util.BracketUtil;
import io.github.betterclient.compiler.util.SymbolParser;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodCompiler {
    private static List<Argument> parseArguments(String input, UsesSymbol user) {
        List<Argument> arguments = new ArrayList<>();

        String regex = "\\b([a-zA-Z_$][a-zA-Z\\d_$]*)\\s+([a-zA-Z_$][a-zA-Z\\d_$]*)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            // Group 1 contains the type, Group 2 contains the name
            String type = matcher.group(1);
            String name = matcher.group(2);
            arguments.add(new Argument(Symbol.map(user, type), name));
        }

        return arguments;
    }

    private static APIMethod parseMethodTypes(String code, UsesSymbol uses, APIClass clazz) {
        code = code.trim();
        if (code.equals("main")) {
            APIMethod out = new APIMethod("main", clazz);

            out.outputType = new Argument(Type.VOID_TYPE);
            Argument mainArgument = new Argument(Type.getType("[Ljava/lang/String;"));
            mainArgument.name = "args";
            out.arguments.add(mainArgument);

            out.type = new AccessType(VisibilityType.PUBLIC, false);
            out.isStatic = true;

            return out;
        } else {
            //Should only contain single spaces
            code = code.replace("  ", " ");

            List<String> symbols = List.of(code.substring(0, code.indexOf('(')).split(" "));
            String name = symbols.get(symbols.size() - 1);

            APIMethod method = new APIMethod(name, clazz);

            boolean staticis = symbols.contains("static");
            boolean finalis = symbols.contains("final");
            VisibilityType visibilityType = VisibilityType.PUBLIC;
            if (symbols.contains("private")) {
                visibilityType = VisibilityType.PRIVATE;
            } else if (symbols.contains("package-private")) {
                visibilityType = VisibilityType.PACKAGE_PRIVATE;
            } else if (symbols.contains("protected")) {
                visibilityType = VisibilityType.PROTECTED;
            }
            method.type = new AccessType(visibilityType, finalis);
            method.isStatic = staticis;

            String returnType = symbols.get(symbols.size() - 2);
            int arrayCount = SymbolParser.countArrays(returnType);
            returnType = returnType.replaceAll("[\\[\\]]", "");

            String descGen = switch (returnType) {
                case "int" -> "I";
                case "boolean" -> "Z";
                case "long" -> "J";
                case "char" -> "C";
                case "byte" -> "B";
                case "short" -> "S";
                case "double" -> "D";
                case "float" -> "F";
                case "void" -> "V";
                default -> "L" + Symbol.map(uses, returnType) + ";";
            };
            descGen = "[".repeat(arrayCount) + descGen;
            method.outputType = new Argument(Type.getType(descGen));

            method.arguments.addAll(parseArguments(code.substring(code.indexOf('(')), uses));

            return method;
        }
    }

    public static void compile(BracketUtil code, UsesSymbol imports, APIClass clazz, APIMethod method) {
        if (Compiler.DEBUG_OUT) System.out.println("Compiling method \"" + method.name + "\"");

        //compile method code
        new MethodCodeCompiler(clazz, method, code, imports).compile();
    }

    public static APIMethod generateMethod(APIClass clazz, BracketUtil code, UsesSymbol imports) {
        APIMethod method = parseMethodTypes(code.beforeBracket, imports, clazz);
        clazz.methods.add(method);
        return method;
    }
}
