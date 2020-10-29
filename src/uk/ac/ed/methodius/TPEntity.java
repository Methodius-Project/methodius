package uk.ac.ed.methodius;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Implements an Entity in the content selection process.
 * 
 */
@SuppressWarnings("unchecked")
public class TPEntity {

    private String number;
    private List<Entity> entities;
    private Type type;
    private String name;
    private String shortName;
    private String gender;
    private String form;
    private String id;
    private boolean generic = false;
    private boolean mostRecent = false;
    private boolean canned = false;
    private Log log;

    public TPEntity(Entity ent, Log log) {
        this.log = log;
        log.start("TPEntity");
        log.output("creating a TPEntity for " + ent);
        number = "sg";
        if (ent.isGeneric()) {
            generic = true;
            number = "pl";
            form = "generic";
        }
        entities = new LinkedList<Entity>();
        entities.add(ent);
        type = ent.getType();
        String typeName = ent.getTypeName();
        log.output("TPEntity: typeName is " + typeName);
        if (typeName.equalsIgnoreCase("canned-text") || typeName.equalsIgnoreCase("string")) {
            canned = true;
        }
        log.output("TPEntity: canned is " + canned);
        
        id = ent.getId();
        name = ent.getName();
        shortName = ent.getShortName();
        gender = ent.getGender();
        log.end("TPEntity");
    }

    public TPEntity(LinkedHashSet<Entity> ents, Log log) {

        this.log = log;
        log.start("TPEntity");
        log.output("creating a TPEntity for " + ents);
        number = "pl";
        entities = new LinkedList<Entity>();
        entities.addAll(ents);
        id = "";
        for (Iterator<Entity> entIter = ents.iterator(); entIter.hasNext();) {
            Entity e = entIter.next();
            type = e.getType();
            String typeName = e.getTypeName();
            log.output("TPEntity: typeName is " + typeName);
            if (typeName.equalsIgnoreCase("canned-text") || typeName.equalsIgnoreCase("string")) {
                canned = true;
            }
            log.output("TPEntity: canned is " + canned);
            id += e.getId();
            if (name == null) {
                name = e.getName();
            }
        }
        log.end("TPEntity");
    }


    public String getNumber() {
        return number;
    }


    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }


    public void setType(Type t) {
        log.output("TPEntity set type to " + t.getName());
        type = t;
    }

    public Type getType() {
        return type;
    }

    public void setForm(String f) {
        form = f;
    }

    public String getForm() {
        return form;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public int size() {
        return entities.size();
    }

    public String getId() {
        return id;
    }

    public boolean isGeneric() {
        return generic;
    }

    public boolean isCanned() {
        return canned;
    }

    public void setMostRecent(boolean b) {
        mostRecent = b;
    }

    public boolean isMostRecent() {
        return mostRecent;
    }

    /** This TPEntity is equal to another if it contains all and only the same entities */

    public boolean equals(Object other) {
        if (other instanceof TPEntity && this.size() == ((TPEntity)other).size()) {
            List<Entity> otherEnts = ((TPEntity)other).getEntities();
            for (Iterator entIter = entities.iterator(); entIter.hasNext();) {
                if (!otherEnts.contains(entIter.next())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String toString() {
        String me = id;
        if (type != null) {
            me += " type:" + type.getName();
        }
        else {
            me += " type:null";
        }
        me += " name " + name;
        me += " generic " + generic;
        me += " most recent " + mostRecent;
        return me;
    }
    
    public String getCorpusXMLId() {
        String id = "";
        for (Iterator<Entity> entIter = entities.iterator(); entIter.hasNext();) {
            Entity e = entIter.next();
            type = e.getType();
            if (id != "") {
                id += ",";
            }
            id += e.getId();
        }
        return id;
    }
}
