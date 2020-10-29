package uk.ac.ed.methodius.Exceptions;

import java.io.PrintStream;

import com.sleepycat.je.DatabaseException;

public class DataStoreException extends Exception {
	
	Exception dbExc = null;
	String message = null;
	
	public DataStoreException( Exception dbe ) {
		dbExc = dbe;
	}
	
	public DataStoreException(String m) {
		message = m;
	}
	
	
	public String getMessage() {
		if(dbExc != null) {
			return dbExc.getMessage();
		} else {
			return message;
		}
	}
	
	public void printStackTrace(PrintStream ps){
		if(dbExc != null ) {
			dbExc.printStackTrace(ps);
		} else {
			super.printStackTrace(ps);
		}
	}

}
