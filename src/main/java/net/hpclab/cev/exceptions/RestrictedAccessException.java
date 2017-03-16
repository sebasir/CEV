package net.hpclab.cev.exceptions;

import java.io.Serializable;

public class RestrictedAccessException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public RestrictedAccessException(String message) {
        super(message);
    }
}
