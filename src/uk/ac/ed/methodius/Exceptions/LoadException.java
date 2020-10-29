package uk.ac.ed.methodius.Exceptions;

import java.io.PrintStream;

public class LoadException extends Exception{
	private static final long serialVersionUID = 1L;
	private String message = null;
	
	public LoadException(String s) {
		super(s);
	}
	
	
	public LoadException(Exception e) {
		super(e);
	}
	
	
	public LoadException(String m, Exception e){
		super(e);
		message = m + ":\n" + e.getMessage();
	}
	
	
	public String getMessage(){
		if(message == null) {
			return super.getMessage();
		} else {
			return message;
		}
	}
}
