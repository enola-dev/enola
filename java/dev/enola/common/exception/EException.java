package dev.enola.common.exception;

/** Tired of dealing with checked exceptions? Just use EException everywhere instead! */
public class EException extends RuntimeException {

    public EException(String message) {
        super(message);
    }

    public EException(String message, Throwable cause) {
        super(message, cause);
    }
}
