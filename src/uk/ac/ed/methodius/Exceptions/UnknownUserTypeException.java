package uk.ac.ed.methodius.Exceptions;

public class UnknownUserTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownUserTypeException(String tname) {
		super("unknown type: " + tname);
	}

}
