package io.github.betterclient.compiler.exception;

import io.github.betterclient.compiler.api.APIMethod;

public class StandardCodeExceptions {
    public static void noreturn(APIMethod method) {
        throw new NoReturnException("Method " + method.name + " doesn't have a return statement.");
    }

    public static void invalidStatement(String beforeBracket) {
        throw new SymbolNotFoundException("Unable to figure out: " + beforeBracket);
    }
}
