package io.github.betterclient.compiler.method.code;

import io.github.betterclient.compiler.api.code.impl.DisbandReturn;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.api.code.impl.get.load.LoadConstantCode;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.method.MethodCodeCompiler;
import io.github.betterclient.compiler.util.CodeCompilerUtils;
import io.github.betterclient.compiler.util.StringParser;

public record StringConstantMethodCompiler(MethodCodeCompiler compiler) {
    public ValueReturnCode compile(String input, boolean first) {
        String string = StringParser.parse(input.substring(1, StringParser.find(input)));
        String remaining = input.substring(StringParser.find(input) + 2);

        String name = remaining.substring(0, remaining.indexOf('('));
        String arguments = remaining.substring(remaining.indexOf('(') + 1, remaining.length() - 1);

        String argName = StringParser.randomString();
        Argument argument = new Argument("java/lang/String", argName);
        compiler.method().arguments.add(argument);

        ValueReturnCode call = new MethodCallCompiler(new CodeCompilerUtils(compiler), compiler.clazz(), compiler.symbol(), compiler.method())
                .compile("this." + argName, name, arguments);

        //Replace argument with loadconstant
        if (call instanceof MethodCallCode mcc) {
            mcc.arguments.remove(0);
            mcc.arguments.add(0, new LoadConstantCode(string));
        }

        compiler.method().arguments.remove(argument);

        if (first) {
            MethodCodeCompiler.lastCompiledStatementDescriptor = "V";
            return new DisbandReturn(call);
        }
        return call;
    }
}
