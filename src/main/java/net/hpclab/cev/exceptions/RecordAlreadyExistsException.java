package net.hpclab.cev.exceptions;

import java.io.Serializable;

public class RecordAlreadyExistsException extends Exception implements Serializable {

	private static final long serialVersionUID = 4021456654556968691L;

	public RecordAlreadyExistsException(String message) {
		super(message);
	}
}
