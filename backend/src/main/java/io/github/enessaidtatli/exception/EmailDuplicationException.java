package io.github.enessaidtatli.exception;

import jakarta.security.auth.message.AuthException;

public class EmailDuplicationException extends RuntimeException {
    public EmailDuplicationException(String msg) {
        super(msg);
    }

    public EmailDuplicationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
