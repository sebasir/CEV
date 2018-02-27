package net.hpclab.cev.exceptions;

import java.io.Serializable;

public class UnexpectedOperationException extends Exception implements Serializable {

	private static final long serialVersionUID = -7775083240796208170L;

	public UnexpectedOperationException(String message) {
        super(message);
    }
}
