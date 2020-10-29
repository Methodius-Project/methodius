package uk.ac.ed.methodius.Exceptions;

public class UnknownPredicateException extends Exception {

	String name = null;

	public UnknownPredicateException(String n) {
		super(n);
		name = n;
	}
	
	public String getMessage() {
		return "Unknown predicate: " + name;
	}
	
	public String toString() {
		return getMessage();
	}


}
