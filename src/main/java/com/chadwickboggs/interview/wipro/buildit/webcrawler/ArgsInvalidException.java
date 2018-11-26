package com.chadwickboggs.interview.wipro.buildit.webcrawler;

public class ArgsInvalidException extends Exception {

    public ArgsInvalidException() {
        super();
    }


    public ArgsInvalidException(String message) {
    }


    public ArgsInvalidException(String message, Throwable cause) {
        super(message, cause);
    }


    public ArgsInvalidException(Throwable cause) {
        super(cause);
    }


    protected ArgsInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
