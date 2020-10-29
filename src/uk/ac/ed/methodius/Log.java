package uk.ac.ed.methodius;

import java.io.PrintStream;


/**
 * Log program output to s.o. and to file output.
 * TODO: add levels of output
 * TODO: log to a file and to screen
 * TODO: allow different levels of output to screen and file
 * TODO: check stacking and unstacking of routines.
 * @author Ray
 *
 */

public class Log {
	
	private PrintStream out = null;
	private int indent = 0;
	private String indentString = "  ";
	private boolean silent = false;
	private boolean indentWanted = false; // used by print, println

	public Log(PrintStream pw) {
		out = pw;
	}
	
	public void start(String name) {
		output(">" + name);
		indent++;
	}
	
	public void end(String name) {
		indent--;
		if(indent < 0) {
			indent = 0;
		}
		output("<" + name);
	}
	
	// TODO: make this more efficient.
	public void output(String s) {
		if(silent) return;
		
		String thisIndent = "";
		for(int i = 0; i < indent; i++) {
			thisIndent = thisIndent + indentString;
		}
		out.print(thisIndent);
		if (s != null) {
		    s = s.replaceAll("\n", "\n" + thisIndent);
		    out.println(s);
		}
	}	
	
	
	public void output(String[] arr) {
		if(silent) return;
		
		String thisIndent = "";
		for(int i = 0; i < indent; i++) {
			thisIndent = thisIndent + indentString;
		}
		
		for(int arr_i = 0; arr_i < arr.length; arr_i++ ) {
			
			out.print(thisIndent);
			out.println(arr[arr_i]);
		}
	}
	
	public void print(String s) {
		if(silent) return;
		
		String thisIndent = "";
		
		if(indentWanted) {
			for(int i = 0; i < indent; i++) {
				thisIndent = thisIndent + indentString;
			}
			out.print(thisIndent);
		}
		if (s != null) {
			s = s.replaceAll("\n", "\n" + thisIndent);
			out.print(s);
		}
	}
	
	public void print(boolean start, String s) {
		indentWanted = true;
		print(s);
		indentWanted = false;
	}
	
	public void println(String s) {
		if(silent) return;
		
		String thisIndent = "";
		
		if(indentWanted) {
			for(int i = 0; i < indent; i++) {
				thisIndent = thisIndent + indentString;
			}
			out.print(thisIndent);
		}
		if (s != null) {
			s = s.replaceAll("\n", "\n" + thisIndent);
			out.println(s);
		}
		indentWanted = true;
	}
	
	public void println(boolean start, String s){
		indentWanted = true;
		println(s);
	}

	
	public String getIndentString() {
		String thisIndent = "";
		for(int i = 0; i < indent; i++) {
			thisIndent = thisIndent + indentString;
		}
		return thisIndent;
	}
	
	public PrintStream getPrintStream() {
		return out;
	}
	
	
	public void setNoOutput() {
		silent = true;
	}
	
	public boolean isSilent() {
		return silent;
	}
}
