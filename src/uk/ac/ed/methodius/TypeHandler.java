package uk.ac.ed.methodius;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.jdom.Element;

import uk.ac.ed.methodius.Exceptions.DataStoreException;

/**
 * Methodius has a type hierarchy. Typehandler is the main class handling this.
 * It provides two primary methods getType(name -> Type) and getDistance(name, name -> int)
 * At present the distance is calculated between each type when the type hierarchy is loaded
 * at startup time. This is efficient in that calculating distance just involves looking
 * up this table but this distanceMap will be size N squared. So an alternative strategy
 * may need considered if it gets too big.
 * 
 * 23/2/07
 * Want to change to allowing multiple parents for a type.
 * This means that when we find it with its parent in the XML,
 * it may already exist with a different parent.
 * This also changes how we calculate the distances to other nodes.
 * Use the same algorithm but check if a distance is already logged,
 * if so then take the shorter.
 * 
 * @author Ray
 *
 */

@SuppressWarnings("unchecked")
public class TypeHandler {

    /**
     *  Map from type name to Type object.
     */
    private static Map typeMap = null;
    private Map typeDataMap;

    /**
     * map from type name to map which maps name to distance.
     * name1 -> {name2 - > dist_name1_name2, name3 -> dist_name1_name3}
     */
    private Map distanceMap = null;
    private Log log;



    /* if no significance is specified in the np defn it gets this */
//  private static final int defaultNpSig = 1;


    public TypeHandler(DataStoreRead ds, Configuration c, Log log) throws DataStoreException {
        this.log = log;
        typeDataMap = ds.getTypes();
//        System.out.println("types data map is " + typeDataMap);
        typeMap = new HashMap();
        Type basic = new Type("basic", this);
        typeMap.put("basic", basic);
        Type stringT = new Type("string", this);
        typeMap.put("string", stringT);
        distanceMap = new HashMap();
        processTypes();
    }

    private void processTypes() {
        log.start("processTypes");
        for (Iterator<String> typeIter = typeDataMap.keySet().iterator(); typeIter.hasNext();) {
           String newName = typeIter.next();
            log.output("type " + newName);
            Type newType = (Type)typeMap.get(newName);
            if (newType == null) {
                log.output("creating new type for " + newName);
                newType = new Type(newName, this);
                typeMap.put(newName, newType);
            }
            Map thisTypeMap = (Map)typeDataMap.get(newName);
            String nps = (String)thisTypeMap.get("nps");
            if (nps != null) {
                List npList = Arrays.asList(nps.split(":"));
                log.output("nps are " + npList);
                newType.addNps(npList);
            }
            String parents = (String)thisTypeMap.get("parents");
            log.output("parents string is " + parents);
            List parentList = new LinkedList();
            if (parents == null) {
                parentList.add("basic");
            }
            else {
                parentList = Arrays.asList(parents.split(":"));
            }
            log.output("parent list is " + parentList);
            for (Iterator<String> parentIter = parentList.iterator(); parentIter.hasNext();) {
                String parentName = parentIter.next();
                Type parentType = (Type)typeMap.get(parentName);
                if (parentType == null) {
                    parentType = new Type(parentName, this);
                    typeMap.put(parentName, parentType);
                    log.output("creating new type for parent " + parentName);
                }
                parentType.addChild(newType);
                newType.addParent(parentType);
                calcDistances(parentName,newName);
            }
        }
    }
    
    private void calcDistances(String pName, String me) {
        Map parentDistances = (Map)distanceMap.get(pName);
        Map myDistances = (Map)distanceMap.get(me);

        if( myDistances == null ) {
            myDistances = new HashMap();
            distanceMap.put(me, myDistances);
        }

        /*
         * foreach entry in my parent's distance map add it to
         * my distance map and me to its distance map incrementing
         * the distance.
         */
        if( parentDistances == null ) {
            parentDistances = new HashMap();
            distanceMap.put(pName, parentDistances);
        }
        Iterator iter = parentDistances.keySet().iterator();

        while(iter.hasNext()) {
            String them = (String)iter.next();
            Integer distI = (Integer)parentDistances.get(them);
            int newDist = distI.intValue() + 1;
            Integer existingDist = (Integer)myDistances.get(them);

            /*
             * we may already have a path between us and them
             * only update if this is shorter.
             */

            if(existingDist == null || newDist < existingDist.intValue()) {
                myDistances.put(them, new Integer(newDist));

                Map theirDistances = (Map)distanceMap.get(them);
                if( theirDistances == null) {
                    theirDistances = new HashMap();
                    distanceMap.put(them, theirDistances);
                }
                theirDistances.put(me, new Integer(newDist) );
            }
        }

        /*
         * add me to my parent's distances and vice versa
         */
        parentDistances.put(me, new Integer(1));
        myDistances.put(pName, new Integer(1));

    }


    public int getDistance(String name1, String name2) {
        if (name1.equals(name2)) {
            return 0;
        }
        Map name1Dists = (Map)distanceMap.get(name1);
        if (name1Dists == null) {
            return 10000;
        }
        Integer distI = (Integer)name1Dists.get(name2);
        if (distI == null) {
            return 10000;
        }
        return distI.intValue();
    }


    public static Type getType(String name) {
        Type t = (Type)typeMap.get(name);
        return t;
    }

    private void printType(Type t, int indent) {
        Log log = Util.getLog();
        for(int i=0; i < indent; i++) {
            log.output("  ");
        }
        log.output(t.getName());
        List children = t.getChildren();
        Iterator iter = children.iterator();
        while(iter.hasNext()) {
            Type nxt = (Type)iter.next();
            printType(nxt, indent+1);
        }
    }

    public void dumpTypeMap() {
        Log log = Util.getLog();
        Type basic = (Type)typeMap.get("basic");
        log.output("TypeMap is:");
        printType(basic, 0);
    }
}
