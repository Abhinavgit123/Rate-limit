package com.project.ratelimit.exception;

public class RatelimitException extends Exception{

    public String errorCode;

    public RatelimitException(String message) {
        super(message);
    }


    public RatelimitException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
