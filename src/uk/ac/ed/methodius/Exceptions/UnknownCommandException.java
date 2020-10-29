package uk.ac.ed.methodius.Exceptions;

public class UnknownCommandException extends Exception {
	
	
	private String command = "";

	public UnknownCommandException(String c) {
		command = c;
	}
	
	public String getMessage() {
		return "Unknown command: " + command;
	}
	
	
	public String toString() {
		return "Unknown command: " + command;
	}
}
