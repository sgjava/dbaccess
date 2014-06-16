/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * {@link java.lang.RuntimeException} used by
 * {@link com.codeferm.dbaccess.DbAccess} implementations.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbAccessException extends RuntimeException {

    /**
     * Save cause passed.
     */
    private transient Throwable cause = null;

    /**
     * Throwable constructor.
     *
     * @param cause Cause of exception.
     */
    public DbAccessException(final Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    /**
     * Message and throwable constructor.
     *
     * @param message Exception message.
     * @param cause Exception cause.
     */
    public DbAccessException(final String message, final Throwable cause) {
        super(message, cause);
        this.cause = cause;
    }

    /**
     * Message constructor.
     *
     * @param message Message String.
     */
    public DbAccessException(final String message) {
        super(message);
    }

    /**
     * Default constructor.
     */
    public DbAccessException() {
        super();
    }

    /**
     * Get cause.
     *
     * @return Cause.
     */
    @Override
    public final Throwable getCause() {
        return cause;
    }

    /**
     * Print cause if passed.
     */
    @Override
    public final void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.err.println("Caused by:"); //NOPMD
            cause.printStackTrace(); //NOPMD
        }
    }

    /**
     * Print cause if passed via PrintStream.
     *
     * @param printStream Print stream.
     */
    @Override
    public final void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);
        if (cause != null) {
            printStream.println("Caused by:");
            cause.printStackTrace(printStream);
        }
    }

    /**
     * Print cause if passed via PrintStream.
     *
     * @param printWriter Print writer.
     */
    @Override
    public final void printStackTrace(final PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (cause != null) {
            printWriter.println("Caused by:");
            cause.printStackTrace(printWriter);
        }
    }
}
