package uk.ac.ed.methodius.Exceptions;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 1L;
	private String filepath;
	private int lineNum;
	private String line;
	private String message;
	
	public ConfigurationException(String fp, int ln, String l, String m) {
		filepath = fp;
		lineNum = ln;
		line = l;
		message = m;
	}
	
	
	public String toString(){
		String me = "Bad configuration line in file:\n" +
		            filepath + "\n" +
		            "at linenumber " + lineNum + "\n" +
		            "line is\n" + line + "\n" +
		            message;
		return me;
	}
}
