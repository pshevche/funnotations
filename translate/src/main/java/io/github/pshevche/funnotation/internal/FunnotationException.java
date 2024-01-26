package io.github.pshevche.funnotation.internal;

public class FunnotationException extends RuntimeException {

    public FunnotationException(String message) {
        super(message);
    }

    public FunnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
