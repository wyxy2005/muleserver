package com.ucweb.esb.exception;

/**
 * Created by ggd543 on 14-1-15.
 */
public class DataTransformerException extends Exception {
    public DataTransformerException() {
    }

    public DataTransformerException(String message) {
        super(message);
    }

    public DataTransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataTransformerException(Throwable cause) {
        super(cause);
    }
}
