package uk.ac.ed.methodius;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
@SuppressWarnings("unchecked")
public class Type {

    String name = null;
    List children = null;
    List parents = null;
    List nps = null; // list of NPs
    TypeHandler typeHandler = null;
    //    private HashSet<Type> allAncestors;

    public Type(String n, TypeHandler th){
        name = n;
        typeHandler = th;
        parents = new LinkedList();
        children = new LinkedList();
        nps = new LinkedList();
    }


    public void addParent(Type p) {
        parents.add(p);
    }

    public String getName() {
        return name;
    }

    public int getDistance(String other){
        return typeHandler.getDistance(name, other);
    }

    public int getDistance(Type other){
        return typeHandler.getDistance(name, other.getName());
    }

    public List getChildren() {
        return children;
    }

    public List getParents() {
        return parents;
    }

    public void addChild(Type t){
        children.add(t);
    }

    public void addNp(NP np) {
        nps.add(np);
    }

    public void addNps(List newnps) {
        nps.addAll(newnps);
    }

    public List getNps() {
        return nps;
    }

    public HashSet<Type> getAllAncestors() {
        HashSet<Type> ancestors = new HashSet<Type>();
        if (parents != null) {
            for (Iterator parentIter = parents.iterator(); parentIter.hasNext();) {
                Type parent = (Type)parentIter.next();
                if (!parent.getName().equals("basic")) {
                    ancestors.add(parent);
                    ancestors.addAll(parent.getAllAncestors());
                }
            }
        }
        return ancestors;
    }
    
    public boolean hasParent(String parent) {
        if (parents == null) {
            return false;
        }
        for (Iterator parentIter = parents.iterator(); parentIter.hasNext();) {
            String thisParent = ((Type)parentIter.next()).getName();
            if (thisParent.equalsIgnoreCase(parent)) {
                return true;
            }
        }
        return false;
    }

    public Type getClosestCommonAncestor(Type otherType) {
        HashSet<Type>myAncestors = getAllAncestors();
        myAncestors.retainAll(otherType.getAllAncestors());
        int longestDist = 0;
        Type closest = typeHandler.getType("basic");
        for (Iterator typeIter = myAncestors.iterator(); typeIter.hasNext();) {
            Type thisType = (Type)typeIter.next();
            int thisDist = typeHandler.getDistance(thisType.getName(), "basic");
            if (thisDist > longestDist) {
                longestDist = thisDist;
                closest = thisType;
            }
        }
        return closest;
    }

    public String toString() {
        return this.getName();
    }
}
