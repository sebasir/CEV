package net.hpclab.cev.services;

public class ParseExceptionService {
	private static ParseExceptionService service;

	public String parse(Exception e) {
		String errorMessage = null;
		try {
			Throwable t = e;
			while (!t.getClass().getName().equals("org.postgresql.util.PSQLException") && t != null)
				t = t.getCause();
			errorMessage = t.getMessage();
		} catch (Exception ex) {
			errorMessage = "No se pudo obtener a causa de error:\nError original = " + e.getMessage();
		}
		return errorMessage;
	}

	public static synchronized ParseExceptionService getInstance() {
		if (service == null)
			service = new ParseExceptionService();
		return service;
	}
}
