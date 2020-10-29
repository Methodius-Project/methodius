package uk.ac.ed.methodius.Exceptions;

import uk.ac.ed.methodius.Entity;
import uk.ac.ed.methodius.Fact;

public class ContentPotentialException extends Exception {
	
	private Entity ent = null;
	private Fact fact = null;
	private String argName = null;

	public ContentPotentialException(Entity e, Fact f, String n) {
		ent = e;
		fact = f;
		argName = n;
	}
	
	
	public String getMessage() {
		return "Expanding CP for entity " + ent.getId() + "\n" +
		       "looking at predicate " + fact.getPredicateName() + "\n" +
		       "cannot find entity named " + argName;
	}

}
