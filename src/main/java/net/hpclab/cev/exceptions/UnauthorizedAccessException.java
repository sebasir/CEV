package net.hpclab.cev.exceptions;

import java.io.Serializable;

public class UnauthorizedAccessException extends Exception implements Serializable {

	private static final long serialVersionUID = -2045014671560211083L;

	public UnauthorizedAccessException(String message) {
		super(message);
	}
}
