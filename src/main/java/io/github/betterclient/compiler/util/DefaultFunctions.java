package io.github.betterclient.compiler.util;

import io.github.betterclient.compiler.api.APILoader;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;

public class DefaultFunctions {
    public static MethodCallCode println(ValueReturnCode s) {
        return new MethodCallCode(
                APILoader.getOrMake("java/io/PrintStream")
                        .getMethod("println", "(Ljava/lang/String;)V"),
                false,

                //instance System.out
                new FieldGetCode(
                        APILoader.getOrMake("java/lang/System")
                                .getField("out", "Ljava/io/PrintStream;"),
                        true,
                        null
                ),
                //to print
                s
        );
    }
}
