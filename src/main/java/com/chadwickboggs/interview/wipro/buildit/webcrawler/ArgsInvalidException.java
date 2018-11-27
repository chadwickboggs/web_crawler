package com.chadwickboggs.interview.wipro.buildit.webcrawler;

import javax.annotation.Nonnull;


public class ArgsInvalidException extends Exception {

    public ArgsInvalidException() {
        super();
    }


    public ArgsInvalidException(@Nonnull final String message) {
        super(message);
    }


    public ArgsInvalidException(@Nonnull final String message, @Nonnull final Throwable cause) {
        super(message, cause);
    }


    public ArgsInvalidException(@Nonnull final Throwable cause) {
        super(cause);
    }


    protected ArgsInvalidException(
        @Nonnull final String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
