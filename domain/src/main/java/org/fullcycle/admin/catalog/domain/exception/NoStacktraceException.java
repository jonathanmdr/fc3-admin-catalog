package org.fullcycle.admin.catalog.domain.exception;

public class NoStacktraceException extends RuntimeException {

    private final static boolean ENABLE_SUPPRESSION = true;
    private final static boolean WRITABLE_STACK_TRACE = false;

    public NoStacktraceException(final String message) {
        this(message, null);
    }

    public NoStacktraceException(final String message, final Throwable cause) {
        super(message, cause, ENABLE_SUPPRESSION, WRITABLE_STACK_TRACE);
    }

}
