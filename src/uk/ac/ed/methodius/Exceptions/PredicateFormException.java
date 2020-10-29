package uk.ac.ed.methodius.Exceptions;

import java.io.IOException;

import org.jdom.Element;

import uk.ac.ed.methodius.Util;

public class PredicateFormException extends Exception {

	Element predicateXML = null;
	String badName = null;
	
	
	public PredicateFormException(Element root, String bad) {
		predicateXML = root;
		badName = bad;
	}
	
	
	public String getMessage() {
		String xml = null;
		try {
			xml = Util.XMLtoString(predicateXML);
		} catch( Exception e ) {
			xml = "error while trying to convert XML to a string.";
		}
		return "Badly formed Predicate XML at: " + badName + " in\n" + xml;
	}
}
