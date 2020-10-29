package uk.ac.ed.methodius;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;

import uk.ac.ed.methodius.Exceptions.FactArgException;
import uk.ac.ed.methodius.Exceptions.UnknownArgNameException;



/**
 * represents a Fact object in the Content Potential.
 * It has two args which either contain Strings which are
 * the names of the two arg objects which have not yet been
 * loaded or the Entity objects themselves if they have been
 * loaded. Also potentially has a list of facts associated with
 * it too. These are handled in the same way as the Entity facts.
 *
 */

public class Fact {
    private String predicate;
    private String arg1Name;
    private String arg2Name;   // if a string then it's the arg itself.
    private Entity arg1;
    //private Entity arg2;
    private LinkedHashSet<Entity> arg2;
//    private HashSet<Entity> multipleArg2;
    private List facts;
    private int distanceWt = 0;
    private int significance = 0;
    private int passThruSig = 10;
    private Fact parent = null;
    private String factId;
    private Log log;
    /* this is the recalculated significance relative to the focal entity */
    private int effectiveSignificance = -1;


    public Fact(String id, String n, String a1, String a2, Log log) {
        this.log = log;
        log.output("FACT: creating fact " + id + " with 2 strings");
        factId = id;
        predicate = n;
        arg1Name = a1;
        arg2Name = a2;
        arg1 = null;
        arg2 = null;
//        multipleArg2 = null;
        facts = new LinkedList();
        distanceWt = 0;
        log.output("arg2 is " + arg2);
    }

    public Fact(String id, String n, Entity a1, Entity a2, Log log) {
        this.log = log;
        log.output("FACT creating fact " + id + "  with 2 entities: " + a1 + " and " + a2);
        log.output("two ents are the same " + a1.equals(a2));
        factId = id;
        predicate = n;
        arg1Name = a1.getId();
        arg2Name = a2.getId();

        arg1 = a1;
        arg2 = new LinkedHashSet<Entity>();
        arg2.add(a2);
//        arg2 = a2;
        facts = new LinkedList();
        distanceWt = 0;
        log.output("new fact is " + this);
    }

    public Fact(String id, String n, Entity a1, LinkedHashSet<Entity> a2, Log log) {
        log.output("FACT creating fact " + id + "  with a hash set of arg2s");
        factId = id;
        predicate = n;
        arg1Name = a1.getId();
        for (Iterator<Entity> arg2Iter = a2.iterator(); arg2Iter.hasNext();) {
            String thisArg2 = arg2Iter.next().getId();
            if (arg2Name == null) {
                arg2Name = thisArg2;
            }
            else {
                arg2Name += ":" + thisArg2; 
            }
        }
        arg1 = a1;
        arg2 = a2;
//        arg2 = a2;
        facts = new LinkedList();
        distanceWt = 0;
        log.output("arg2 is " + arg2);
    }

    public String getPredicateName() {
        return predicate;
    }

    public String getFactId() {
        return factId;
    }


    public String getArg1Name() {
        return arg1Name;
    }

    public String getArg2Name() {
        return arg2Name;
    }


    public Entity getArg1() {
        return arg1;
    }

    public LinkedHashSet<Entity> getArg2() {
        return arg2;
    }

    public List getFacts() {
        return facts;
    }


    public int getDistanceWeighting() {
        return distanceWt;
    }


    public void setDistanceWeighting(int d) {
        distanceWt = d;
    }


    public int getSignificance() {
        return significance;
    }


    public void setSignificance(int s) {
        significance = s;
    }

    public int getPassThruSig() {
        return passThruSig;
    }

    public void setPassThruSig(int s) {
        passThruSig = s;
    }


    public int getCPSignificance() {
        return significance * distanceWt;
    }


    public Fact getParent() {
        return parent;
    }

    public void setParent(Fact p) {
        parent = p;
    }

    public int getEffectiveSignificance() {
        if(effectiveSignificance == -1) {
            effectiveSignificance = significance;
        }
        return effectiveSignificance;
    }

    public void setEffectiveSignificance(int i) {
        effectiveSignificance = i;
    }

    /**
     * replaces the arg which has value <code>eName</code> with the
     * Entity <code>ent</code>
     * <p>
     * @throws FactArgException, UnknownArgNameException 
     */


    public void setArgByName(String eName, Entity ent) throws FactArgException, UnknownArgNameException {
        log.output("FACT " + predicate + ": setting arg1 " + eName + " to entity " + ent);
        if( arg1Name.equals(eName) ) {
            log.output("FACT " + predicate + ": setting arg1 " + eName + " to entity " + ent);
            arg1 = ent;
        }
        else if( arg2Name.equals(eName) ) {
            if (arg2 == null) {
                arg2 = new LinkedHashSet<Entity>();
            }
            arg2.add(ent);
            log.output("FACT " + predicate + ": adding entity " + ent + "to arg2 " + eName);
            //arg2 = ent;
        }
        else if (arg2Name.contains(":")) {
            String[] realNames = arg2Name.split(":");
            for (int i = 0; i < realNames.length; i++) {
                String thisName = realNames[i];
                if (thisName.equals(eName)) {
                    if (arg2 == null) {
                        arg2 = new LinkedHashSet<Entity>();
                    }
                    log.output("FACT " + predicate + ": adding entity " + ent + "to arg2 " + eName);
                    arg2.add(ent);
                }
            }
        }
        else {
            throw new UnknownArgNameException(this, eName);
        }
    }

    // overriding equals and hashcode so that facts with the same id will be
    // treated as equal (they won't be the same object if they were
    // created for different texts)

    
    
    
    /*
    public boolean equals(Object other) {
        if (other instanceof Fact && factId.equals(((Fact)other).getFactId())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return factId.hashCode();
    }
*/

    public String toString() {
        String me = "Fact:\n" + "id: " + factId + "\n" + "predicate: " + predicate + "\n";

        if( arg1 == null ) {
            me = me + "arg1: " + arg1Name + "(String)\n";
        }
        else {
            Entity ent = (Entity)arg1;
            me = me + "arg1: " + ent.getId() + "(Entity)\n";
        }

        if( arg2 == null ) {
            me = me + "arg2: " + arg2Name + "(String)\n";
        }
        else {
            me = me + "arg2: ";
            for (Iterator<Entity> arg2Iter = arg2.iterator(); arg2Iter.hasNext();) {
                Entity ent = (Entity)arg2Iter.next();
                me = me + ent.getId() + "(Entity)\n";
            }
            //Entity ent = (Entity)arg2;
//            me = me + "arg2: " + ent.getId() + "(Entity)\n";
        }

        int totalSig = getSignificance();
        me = me + "sig = " + totalSig;

        return me;
    }


    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((factId == null) ? 0 : factId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Fact other = (Fact) obj;
        if (factId == null) {
            if (other.factId != null)
                return false;
        } else if (!factId.equals(other.factId))
            return false;
        return true;
    }

    public String getUnsetArgName() {
        String ret = null;

        if( arg1 == null ) {
            ret = arg1Name;
        } else if( arg2 == null ) {
            ret = arg2Name;
        }

        return ret;
    }



    private static class FactComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            Fact f1 = (Fact)arg0;
            Fact f2 = (Fact)arg1;
            int sig1 = f1.getCPSignificance();
            int sig2 = f2.getCPSignificance();
            return sig2 - sig1;
        }

    }


    public static Comparator getComparator() {
        return new FactComparator();
    }
}
