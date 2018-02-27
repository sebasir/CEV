package net.hpclab.cev.exceptions;

import java.io.Serializable;

public class RecordNotExistsException extends Exception implements Serializable {

	private static final long serialVersionUID = 2417441961334056510L;

	public RecordNotExistsException(String message) {
        super(message);
    }
}
