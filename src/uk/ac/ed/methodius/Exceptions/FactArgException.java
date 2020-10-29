package uk.ac.ed.methodius.Exceptions;

import uk.ac.ed.methodius.Entity;

public class FactArgException extends Exception {
	
	private Entity existingEnt = null;
	private Entity newEnt = null;

	public FactArgException(Object arg1, Entity ent) {
		existingEnt = (Entity)arg1;
		newEnt = ent;
	}
	
	public String getMessage() {
		return "Trying to set arg to " + newEnt.getId() + " but it's already set to " + existingEnt.getName();
	}

}
