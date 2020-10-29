package uk.ac.ed.methodius;

import java.util.HashSet;

import org.jdom.Element;

/**
 * Implements a Fact in the content selection process.
 * 
 * Subclasses SingleTPFact and MultipleTPFact do most of the work
 */

@SuppressWarnings("unchecked")
public class TPFact {
    boolean single;

    boolean newComp = false;
    boolean oldComp = false;
    //    boolean isLike;
    String type = "none";
    TPEntity topArg1;
    TPEntity topArg2;
    boolean aggBefore;
    boolean aggAfter;
    HashSet<TPFact> facts;
    HashSet dependents;
    Integer firstAdverb;
    Integer lastAdverb;
    //    Type arg1Type;
    //    Type refType;
    //    boolean names;
    //    String arg1Num;

    boolean another = false;
    public Integer getLastAdverb() {
        return lastAdverb;
    }

    /** returns true if this is a single fact, false if it is a
     * multiple one */

    public boolean isSingle() {
        return single;
    }

    /*	
    public boolean isGeneric() {
	return generic;
    }
     */

    /** returns a HashSet of the facts expressed by this TPFact.  May
     * contain only one fact if a SingleTPFact or arbitrarily many if
     * a MultipleTPFact which may contain other MultipleTPFact */

    public HashSet<TPFact> getFacts() {
        return facts;
    }

    /** returns the number of facts contained in this TPFact */

    public int getSize() {
        return facts.size();
    }

    void addDependent(TPFact factToAdd) {
        dependents.add(factToAdd);
    }

    public HashSet<SingleTPFact> getDependents() {
        return dependents;
    }

    public boolean hasDependents() {
        return (!dependents.isEmpty());
    }


    /** returns the arg1 of the first fact in this TPFact */

    public TPEntity getTopArg1() {
        return topArg1;
    }

    public TPEntity getTopArg2() {
        return topArg2;
    }

    public void setAggregatableBefore(boolean aggBef) {
        aggBefore = aggBef;
    }
    public void setAggregatableAfter(boolean aggAf) {
        aggAfter = aggAf;
    }

    /** can another sentence be added before this one */

    public boolean isAggregatableBefore() {
        return aggBefore;
    }

    /** can another sentence be added after this one */

    public boolean isAggregatableAfter() {
        return aggAfter;
    }

    public boolean isAggregatable() {
        return (aggAfter || aggBefore);
    }

    public void setType(String newType) {
        type = newType;
    }

    public String getType() {
        return type;
    }

    public void setOldComp(boolean b) {
        oldComp = b;	
    }

    public boolean isOldComp() {
        return oldComp;
    }

    public void setNewComp(boolean b) {
        newComp = b;	
    }

    public boolean isNewComp() {
        return newComp;
    }

    public Integer getFirstAdverb() {
        return firstAdverb;
    }

    public Element toXml() {
        return null;
    }
    
    public void setAnother(boolean b) {
        another = true;
    }
    
    public boolean getAnother() {
        return another;
    }

}
