package uk.ac.ed.methodius.Exceptions;

import uk.ac.ed.methodius.Fact;

public class UnknownArgNameException extends Exception {
	
	private Fact fact = null;
	private String argName = null;
	
	public UnknownArgNameException(Fact f, String n) {
		fact = f;
		argName = n;
	}
	
	public String getMessage() {
		return "Unknown arg " + argName + " in fact " + fact;
	}

}
