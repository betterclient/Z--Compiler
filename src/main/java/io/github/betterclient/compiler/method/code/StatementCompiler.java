package io.github.betterclient.compiler.method.code;

import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.api.code.impl.get.load.LoadConstantCode;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.exception.CompilerException;
import io.github.betterclient.compiler.util.CodeCompilerUtils;
import io.github.betterclient.compiler.method.MethodCodeCompiler;
import io.github.betterclient.compiler.util.StringParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record StatementCompiler(MethodCodeCompiler compiler) {
    public ValueReturnCode compileStatement(String input, boolean first) {
        if(!input.contains("(") && !input.contains("=") && first) throw new CompilerException("Not valid statement: " + input);

        if (input.startsWith("\"") && input.endsWith("\"") && !first) {
            //Strings are LoadConstantCodes (if not first which means it's an argument)
            return new LoadConstantCode(StringParser.parse(input.substring(1, input.length() - 1)));
        } else if (input.startsWith("\"")) {
            //Some method calls going on with string
            return new StringConstantMethodCompiler(compiler).compile(input, first);
        }

        if (!input.contains("=")) {
            String[] a = input.split("\\(", 2);
            a[0] = a[0].replace(" ", "");
            input = String.join("(", a);
        }

        // System.out - this.abc
        Pattern fieldAccessPattern = Pattern.compile("^([a-zA-Z0-9_.]+)\\.([a-zA-Z0-9_]+)$");
        Matcher fieldAccessMatcher = fieldAccessPattern.matcher(input);
        if (fieldAccessMatcher.matches()) {
            String object = fieldAccessMatcher.group(1);
            String field = fieldAccessMatcher.group(2);
            return new FieldGetCompiler(new CodeCompilerUtils(compiler), compiler.clazz(), compiler.symbol(), compiler.method())
                    .compile(object, field);
        }

        // this.hello() - object.hello() - Class.hello()
        Pattern methodCallPattern = Pattern.compile("^([a-zA-Z0-9_.]+)\\.([a-zA-Z0-9_]+)\\((.*)\\)$");
        Matcher methodCallMatcher = methodCallPattern.matcher(input);
        if (methodCallMatcher.matches()) {
            String object = methodCallMatcher.group(1);
            String method = methodCallMatcher.group(2);
            String arguments = methodCallMatcher.group(3);
            return new MethodCallCompiler(new CodeCompilerUtils(compiler), compiler.clazz(), compiler.symbol(), compiler.method())
                    .compile(object, method, arguments);
        }

        // hello()
        Pattern implicitMethodCallPattern = Pattern.compile("^([a-zA-Z0-9_]+)\\((.*)\\)$");
        Matcher implicitMethodCallMatcher = implicitMethodCallPattern.matcher(input);
        if (implicitMethodCallMatcher.matches()) {
            String method = implicitMethodCallMatcher.group(1);
            String arguments = implicitMethodCallMatcher.group(2);

            String object = "this";
            if (compiler.method().isStatic) object = compiler.clazz().name;

            return new MethodCallCompiler(new CodeCompilerUtils(compiler), compiler.clazz(), compiler.symbol(), compiler.method())
                    .compile(object, method, arguments);
        }

        // abc = abc.trim()
        Pattern assignmentPattern = Pattern.compile("^([a-zA-Z0-9_.]+)\\s*=\\s*(.+)$");
        Matcher assignmentMatcher = assignmentPattern.matcher(input);
        if (assignmentMatcher.matches()) {
            String field = assignmentMatcher.group(1);
            String value = assignmentMatcher.group(2);
            return new FieldSetCompiler(new CodeCompilerUtils(compiler), compiler.clazz(), compiler.symbol(), compiler.method())
                    .compile(field, value, first);
        }

        throw new CompilerException("Not valid statement: " + input);
    }
}
