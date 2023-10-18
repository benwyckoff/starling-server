package com.dragontreesoftware.odyssey.templates.freemarker;

import java.io.IOException;

public class ResolverException extends IOException {

    public ResolverException() {
    }

    public ResolverException(String message) {
        super(message);
    }

    public ResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolverException(Throwable cause) {
        super(cause);
    }
}
