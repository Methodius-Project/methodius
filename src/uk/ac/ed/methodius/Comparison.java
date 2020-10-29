package uk.ac.ed.methodius;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/* A comparison to be passed to the content selection and text planning */

@SuppressWarnings("unchecked")

public class Comparison {

    private Fact focalFact;
    private int significance;
    private Fact otherFact;
    private LinkedHashSet<Entity> otherArg2;
    private boolean like;
    private int variety;
    private Type focalType;
//  private Type singleType;
    private Type groupType;
    private Type refType;
    private LinkedHashSet<Entity> otherEntities;

    private int histDistance;
    private Integer hierDistance;

    private Comparison otherComparison;
    private boolean multipleFacts;
    private Log log;


    public Comparison(Entity focalEntity, Fact focal, Fact other, LinkedHashSet otherEnts, int h, int variety, Log log) {

        this.log = log;
        like = false;
        multipleFacts = false;
        focalFact = focal;
        significance = focalFact.getSignificance();
        LinkedHashSet<Entity> focalArg2 = focalFact.getArg2();
        otherFact = other;
        otherArg2 = otherFact.getArg2();
        if (focalArg2.equals(otherArg2)) {
            like = true;
        }
        otherEntities = otherEnts;
        otherArg2 = otherFact.getArg2();
        focalType = focalEntity.getType();
        histDistance = h;
        this.variety = variety;
        switch(variety) {
        case Comparisons.NAME :
            findHierDist();
            break;
        case Comparisons.PREV :
            //	    findHierDist();
            chooseRefType();
            break;
        case Comparisons.TYPE :
            chooseRefType();
            break;
        }
    }

    public void setGroupType(Type otherType) {
        groupType = otherType;
        hierDistance = groupType.getDistance(focalType);
    }

    public void addEntity(Entity e, int hist) {
        otherEntities.add(e);
        log.output("number of entities is " + getNumberOfEntities());
        if (hist < histDistance) {
            histDistance = hist;
        }
        if (variety == Comparisons.TYPE) {
            chooseRefType();
        }
        else { // if (variety != Comparisons.TYPE)
            Type t = e.getType();
            int hier = focalType.getDistance(t);
            if (hier < hierDistance) {
                hierDistance = hier;
                refType = t.getClosestCommonAncestor(focalType);
            }
        }
    }

    public int getVariety() {
        return variety;
    }

    private void findHierDist() {
 //       log.start("findhierdist");
        for (Iterator<Entity> entIter = otherEntities.iterator(); entIter.hasNext();) {
            Entity e = entIter.next();
            Type t = e.getType();
            Type ot = t.getClosestCommonAncestor(focalType);
 //           log.output("for type " + t.getName());
            int hDist = ot.getDistance(focalType);
 //           log.output("dist is " + hDist);            
//            log.output("hierdist is " + hierDistance);
            if (hierDistance == null || hDist < hierDistance) {
//                log.output("resetting hierDistance to " + hDist);
                hierDistance = hDist;
            }
        }
 //       log.output("final hierdist is " + hierDistance);
 //       log.end("findhierdist");
    }

    private void chooseRefType() {
        //	log.output("ref type was " + refType);
        //	log.start("chooseRefType");
        HashSet<Type> types = null;
        for (Iterator<Entity> entIter = otherEntities.iterator(); entIter.hasNext();) {
            Entity e = entIter.next();
            Type eType = e.getType();
            HashSet<Type> allAncs = eType.getAllAncestors();
            allAncs.add(eType);
            if (types == null) {
                types = allAncs;
            }
            else {
                types.retainAll(allAncs);
            }
        }

        Integer largestDist = 0;
        for (Iterator<Type> typeIter = types.iterator(); typeIter.hasNext();) {
            Type t = typeIter.next();
            Integer thisDist = t.getDistance("basic");
            if (thisDist > largestDist) {
                largestDist = thisDist;
                refType = t;
            }
        }
    }	    

    public boolean hasMultipleFacts() {
        return multipleFacts;
    }

    public Fact getFocalFact() {
        return focalFact;
    }

    public Fact getOtherFact() {
        return otherFact;
    }

    public boolean isLike() {
        return like;
    }

    public Type getGroupType() {
        return groupType;
    }


    public void setRefType(Type t) {
        refType = t;
        log.output("reftype for ?group? is " + refType);
    }

    public Type getRefType() {
        return refType;
    }

    public LinkedHashSet<Entity> getOtherEntities() {
        return otherEntities;
    }

    public LinkedHashSet<Entity> getArg2() {
        return otherArg2;
    }

    public int getHierDistance() {
        return hierDistance;
    }

    public int getHistDistance() {
        return histDistance;
    }

    public int getNumberOfEntities() {
        return otherEntities.size();
    }

    public void addComparison(Comparison c) {
        otherComparison = c;
        int otherSig = otherComparison.getFocalFact().getSignificance();
        int sum = significance + otherSig;
        significance = sum / 2;
        multipleFacts = true;
    }

    public Comparison getOtherComparison() {
        return otherComparison;
    }

    public int getSignificance() {
        return significance;
    }

    public String toString() {
        String cType;
        if (like) {
            cType = "like";
        }
        else {
            cType = "unlike";
        }
        String ents = "";
        String n = "";
        for (Iterator<Entity> entIter = otherEntities.iterator(); entIter.hasNext();) {
            Entity e = entIter.next();
            ents += e.getId() + " ";
            n += e.getName() + " ";
        }

        String me = cType + " " + ents;
        if (variety == Comparisons.PREV) {
            me += "previous ";
            me += "type " + groupType + " " ;
        }

        else if (variety == Comparisons.TYPE) {
            me += "type " + groupType + " " ;
        }

        else if (variety == Comparisons.NAME) {
            me += "names " + n + " ";
        }

        if (refType != null) {
            me += "reftype " + refType + " " ;
        }
        me += focalFact.getPredicateName();
        me += " " + otherArg2;
        if (multipleFacts) {
            me += " and ";
            me += " " + otherComparison.getFocalFact().getPredicateName();
            me += " " + otherComparison.getArg2();
        }
        me += " size "  + getNumberOfEntities();
        me += " hist " + histDistance;
        me += " hier " + hierDistance;
        me += " sig " + significance;
        return me;
    }

}