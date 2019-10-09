package org.postgresql.core;

public class SqlInjectionException extends RuntimeException {

    public SqlInjectionException(String message) {
        super(message);
    }
}
