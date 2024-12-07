package io.github.betterclient.compiler.exception;

public class CompilerException extends RuntimeException {
    public CompilerException(String message) {
        super(message);
    }

    public CompilerException(Exception e) {
        super(e);
    }
}