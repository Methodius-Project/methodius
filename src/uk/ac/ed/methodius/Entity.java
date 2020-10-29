package uk.ac.ed.methodius;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;

/**
 * represents an Entity in the Content Potential.
 * Each Entity has a <code>type</code>
 * and possibly a list of facts. In the DB the facts are stored
 * as Strings which are the names of their fact objects. In the Entity object they
 * are stored as a list of Objects which are either Strings or Facts, depending on whether
 * or not they have been expanded.
 *
 */

@SuppressWarnings("unchecked")

public class Entity {
	/*
	 * may be Strings or Facts depending on whether they have been expanded
	 * If unexpanded then the name of the Fact is the map key and it points to
	 * a null. If expanded, the key is the predicate name and the value the Fact.
	 */
	private Map facts;
    private ArrayList topLevelFacts;
	private String id = null;
	private String name = null;
	private String shortname = null;
	private String gender = null;
	private String number = null;
	private String generic = null;

	private Type type = null;
	private String typeName;
	
	private int distance;
	
	public Entity(String id,
			      String t,
			      String name,
			      String shortname,
			      String gender,
			      String number,
			      String generic,
			      List f) {
		this.id = id;
		typeName = t;
		type = TypeHandler.getType(t);
		this.name = name;
		this.shortname = shortname;
		this.gender = gender;
		this.number = number;
		this.generic = generic;
		distance = 0;
		
		facts = new HashMap();
		topLevelFacts = new ArrayList();
		if( f != null ) {
			Iterator iter = f.iterator();
			while(iter.hasNext()) {
				String factName = (String)iter.next();
				facts.put(factName,null);
			}
		}
	}
	
	
	public Entity(String id, String n, String t) {
	    this(id, t, n, "", "", "", null, null);
	}
	

	
	public String getId() {
		return id;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type t) {
	    type = t;
	}
	
	public String getTypeName() {
	    return typeName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getShortName() {
		return shortname;
	}
	
	public String getGender() {
		return gender;
	}

	public String getNumber() {
		return number;
	}
	
	public boolean isGeneric() {
	    if (generic == null) {
		return false;
	    }
	    return generic.equals("T");
	}
	
	public List getFacts() {
		return new LinkedList(facts.values());
	}
	
	public List getFactNames() {
		return new LinkedList(facts.keySet());
	}
	
	
	public Fact getFact(String fname) {
		return (Fact)facts.get(fname);
	}
	
	
	public void addFact(String pred, Fact f) {
		facts.put(pred,f);
	}
	
	
	public void setFacts(Map newFacts) {
		facts = newFacts;
		if(facts.containsKey("name")) {
			Fact f = (Fact)facts.get("name");
			name = f.getArg2Name();
		}
	}
	
	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int d) {
		distance = d;
	}
    
    public void addTopLevelFact(Fact f) {
	topLevelFacts.add(f);
    }
    
    public ArrayList getTopLevelFacts() {
	return topLevelFacts;
    }
    
    
    // overriding equals and hashcode so that entities with the same id will be
    // treated as equal (they won't be the same object if they were
    // created for different texts)

    
    
    /*
    public boolean equals(Object other) {
	if (other instanceof Entity && this.getId().equals(((Entity)other).getId())) {
	    return true;
	}
	return false;
    }

    public int hashCode() {
	return id.hashCode();
    }
*/
    /*
    public String toLongString() {
	String me = "ID: " + id + "\n" +
	    "Type: " + type.getName() + "\n" +
	    "Name: " + name + "\n" +
	    "Shortname: " + shortname + "\n" +
	    "Gender: " + gender + "\n" +
	    "Number: " + number + "\n" +
	    "Generic: " + generic + "\n" +
	    "Facts:" + facts;
	return me;
    }
    */


    public String toString() {
        return id;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


}
