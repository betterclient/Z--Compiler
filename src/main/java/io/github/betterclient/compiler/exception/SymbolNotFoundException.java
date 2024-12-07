package io.github.betterclient.compiler.exception;

public class SymbolNotFoundException extends CompilerException {
    public SymbolNotFoundException(String message) {
        super(message);
    }

    public SymbolNotFoundException(Exception e) {
        super(e);
    }
}
