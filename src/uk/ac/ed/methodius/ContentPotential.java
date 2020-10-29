package uk.ac.ed.methodius;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ed.methodius.Fact;


/**
 * Graph made up of Entities and Facts representing limited Content Potential.
 * Centred around a root (focal) object and radiating out <code>depth</code> entities.
 * ContentPotential also contains within it methods to return a requested number of
 * facts and so logically contains within it the Content Selection algorithm.
 * 
 */

@SuppressWarnings("unchecked")

public class ContentPotential {

    private Entity root;
    private int depth;
    private DataStoreRead ds = null;
    private TypeHandler typeHandler = null;
//    private Type stringType = null;
    private Log log = null;
    private Configuration config;
    private UserModel userModel = null;
    private GenericFactHandler gfh = null;
    private CPFact genericFact = null;
    private int nFacts = 0;
    private int thisWanted;
    private boolean createTypeFact = false;
    @SuppressWarnings("unused")
    private HashSet predicatesAdded;

    /*
     * List of CPfact object. Each CPFact object has a pointer to a Fact,
     * a list of dependent CPFact objects, and a list of dependent CPFact
     * object we have chosen to express.
     */
    private List CPFacts = new LinkedList();



    /**
     * create CP around object <code>name</code> radiating out <code>depth</code> objects.
     *
     * @param d the datastore to use
     * @param name the id of the focus object
     * @param configuration central config object
     *
     * @throws Exception
     */


    public ContentPotential(DataStoreRead d,
            String name,
            Configuration c) throws Exception {
        ds = d;
        config = c;
        depth = config.getMaxCPDepth();
        userModel = config.getUserModel();
        typeHandler = config.getTypeHandler();
        gfh = config.getGenericFactHandler();
        log = config.getLog();
        createTypeFact = config.getCreateTypeFact();
        
//        stringType = typeHandler.getType("string");
        root = ds.getEntity(name);
        if (root == null) {
            log.output("no entity in database for \"" + name + "\"");
            System.exit(0);
        }
        CPFacts = new ArrayList();

        /* 
         * expanding the root object pulls in from the datastore
         * the limited content potential.
         */
        expand(root, depth, null);

        /*
         * gather a list of facts out of the CP.
         */
        CPFacts = collectFacts(root, null);
        log.output("CPFacts is " + CPFacts);
        sortFacts(CPFacts);
        log.output("CPFacts is now" + CPFacts);

        /*
         * check for a generic fact for this entity
         */
        String typeName = root.getTypeName();
//      System.out.println("type for generic search is " + typeName);
        Fact gf = null;
        if (gfh != null) {
            gf = gfh.getGeneric(typeName);
            log.output("found generic for " + typeName);
        }

        if(gf != null && !userModel.alreadySeenGenericFact(typeName)) {
            genericFact = new CPFact(gf, null, null);
            userModel.nowSeenGenericFact(typeName);
            CPFacts.add(genericFact);
        }
        //outputCPFacts(CPFacts);

    }


    public Entity getRoot() {
        return root;
    }


    /**
     * return a limited number of facts.
     * We have a List of CPFact objects in order of significance.
     * Each CPFact can have a list of dependents, these dependents
     * can have dependents and so on.
     * We gather each level of CPFacts together, assigning them a
     * working or effective significance which is made up of their
     * basic significance and the significance of all the facts linking
     * them to the focal object.
     * The significance of a given fact = it's basic significance +
     * twice it's parental significance.
     * We stop when we run out of facts or have found enough.
     * @param the number wanted
     * @return list of Facts
     */

    public List getNFacts(int wanted) {
        return getNFacts(wanted, true, null);
    }

    public List getNFacts(int wanted, boolean flatten) {

        return getNFacts(wanted, flatten, null);
    }

    public List getNFacts(int wanted, boolean flatten, HashSet<Fact> comps) {
        log.output(">getNFacts");
        log.output("wanted is " + wanted);
        log.output("cpfacts is " + CPFacts);
        thisWanted = wanted;
        /* the top level list of facts chosen to express */
        List<CPFact> chosen = new LinkedList();


        if(genericFact != null){
            nFacts++; /* use it so we already have one fact */
        }

        /* make sure comparison fact gets chosen */
        if (comps != null && !comps.isEmpty()) {
//            CPFacts = promoteCompFacts(comps);
        }
        /* the list of candidate facts to consider */
        List<CPFact> thisLevel = new LinkedList();

        /* first add all the top level facts to be considered */
        thisLevel.addAll(CPFacts);
        log.output("this level is " + thisLevel);
        /*
         * while we have more facts to look at and we haven't already
         * got the number wanted, consider each fact. When considering
         * each fact, remove it from the front of the list and add
         * it's dependents to the end of the list for consideration.
         * If this fact has not already been used then add it to the 
         * list of chosen facts. But you may need to add in its
         * parent(s) so that it makes sense when you say it e.g.
         * You want to say "This exhibit was found in Attica. Attica is
         * in Greece". Not just "Attica is in Greece".
         */
        boolean more = true;
        log.output("chosen size is " + chosen.size());
        while(more) {
            /* while we consider this level gather the next */
            List nextLevelFacts = new LinkedList();

            /* look at each fact on this level */
            Iterator iter = thisLevel.iterator();
            while(iter.hasNext() && more){
                CPFact cpf = (CPFact)iter.next();
                Fact f = cpf.getFact();
                String id = f.getFactId();
                log.output("considering " + id);
                /* if we've not already used it then add this fact */
                if(!userModel.alreadySeenFact(id)){
                    chosen = recursiveAdd(chosen,cpf);
                    log.output("chosen size is " + chosen.size());
                    log.output("nFacts " + nFacts);
                    //		    nFacts++;
                    if(nFacts >= thisWanted) {
                        more = false;
                    }
                    userModel.nowSeenFact(id);
                }

                /* whether we've used this fact or not, we need to look at the dependents */
                List deps = cpf.getDependents();
                if( deps != null ) {
                    log.output(id + " has dependents");
                    setEffectiveSignificances(deps,f.getEffectiveSignificance());
                    nextLevelFacts.addAll(deps);
                }
            }
            if(nextLevelFacts.isEmpty()) {
                more = false;   // nothing left to say
            }
            else {
                sortFacts(nextLevelFacts);
                thisLevel = nextLevelFacts;
            }
        }

        /*
         * if we have a generic fact. Add it and then destroy it so
         * that we only say it once.
         */
        if(genericFact != null ) {
            chosen.add(genericFact);

//          genericFact = null;
        }
        log.end("getNFacts");
        if (flatten) {
            List<Fact> res = flattenChosen(chosen);
            return res;
        }
        else {
            return chosen;
        }

    }


    /*
     * add a fact and recursively add all its parents if needed
     * so that we have a link back to the original object.
     */
    private List recursiveAdd(List l, CPFact cpf) {
        List newList = l;
        CPFact parent = cpf.getParent();
        if(parent == null) {
            if(nFacts < thisWanted && !newList.contains(cpf)){
                newList.add(cpf);
                nFacts++;
            }
        } else {
            List alreadyChosen = parent.getChosen();
            if(nFacts < thisWanted && (alreadyChosen == null || !alreadyChosen.contains(cpf))){
                parent.addChosen(cpf);
                nFacts++;
                newList = recursiveAdd(newList, parent);
            }
        }
        return newList;
    }


    private List<Fact> flattenChosen(List<CPFact> cpfl) {
        List res = new LinkedList();
        Iterator iter = cpfl.iterator();
        while(iter.hasNext()) {
            CPFact cpf = (CPFact)iter.next();
            Fact f = cpf.getFact();
            res.add(f);
            List chosen = cpf.getChosen();
            if(chosen != null && !chosen.isEmpty()){
                List subs = flattenChosen(chosen);
                res.addAll(subs);
            }
        }
        return res;
    }

    public void setEffectiveSignificances(List l, int seed) {
        seed = seed * 2;
        Iterator iter = l.iterator();
        while(iter.hasNext()) {
            CPFact cpf = (CPFact)iter.next();
            Fact f = cpf.getFact();
            int sig = f.getSignificance();
            f.setEffectiveSignificance(seed+sig);
        }
    }

    /**
     * s the Content Potential around a focus object. In the DB,
     * facts within entities are stored as Strings which are the ids of
     * the Fact objects.
     * To expand a focal entity, we load in its facts objects, replacing the
     * list of fact ids stored in the entity with references to the facts
     * themselves.
     * Each fact then references either another entity or a String.
     * If it's an Entity then we continue the expansion
     * process through that object until we reach the depth we require.
     * If it's a String we wrap it up as a "stringtype" Entity. That way
     * we know that the arg2 is always an Entity which simpifies things.
     * We differentiate between Entity ids and Strings by looking them
     * up the in DB. If we find it then it's an id, if not then it's a String.
     * Crude but it seems to work ok.
     */
    private void expand(Entity ent, int depth, Fact parentFact ) throws Exception {
        if( depth <= 0 || ent == null ) return;
        log.start("expanding entity: " + ent.getId());
        Map expandedFacts = new HashMap();
        List facts = ent.getFactNames();
        log.output("fact list = " + facts);
        Iterator iter = facts.iterator();
        while(iter.hasNext()){
            String nxtFact = (String)iter.next();
            log.output("processing fact: " + nxtFact);
            Fact f = ds.getFact(nxtFact);
            f.setParent(parentFact);
            String pred = f.getPredicateName();
            String typeName = ent.getTypeName();
            Type t = typeHandler.getType(typeName);
            ent.setType(t);

            int sig = userModel.getPredicateSignificance(pred,t);
            f.setSignificance(sig);
            expandedFacts.put(f.getPredicateName(),f);

            /* set the back pointer to this entity by name */
            f.setArgByName(ent.getId(),ent);

            String nxtName = f.getUnsetArgName();
            log.output("unset arg name = " + nxtName);
            if( nxtName != null ) {
                Entity nxtEntity = ds.getEntity(nxtName);
                if( nxtEntity == null ) {
                    if (nxtName.contains(":")) {
                        log.output("found multiple arg2");
                        String[] nxtNames = nxtName.split(":");
                        for (int i = 0; i < nxtNames.length; i++) {
                            String thisName = nxtNames[i];
                            Entity thisEnt = ds.getEntity(thisName);
                            f.setArgByName(thisName, thisEnt);
                            if (depth > 0) {
                                expand(thisEnt, depth-1, f);
                            }
                        }
                    }
                    else {
 //                       nxtEntity = new Entity(nxtName, nxtName, stringType);
                        nxtEntity = new Entity(nxtName, nxtName, "string");
                        f.setArgByName(nxtName,nxtEntity);
                        log.output("it's null");
                    }
                } else {
                    log.output("set pointer to it");
                    f.setArgByName(nxtName,nxtEntity);
                    if( depth > 0 ) {
                        expand(nxtEntity,depth-1, f);
                    }
                }
            }
        }
        ent.setFacts(expandedFacts);    

        /*
         * add in a special "type" fact with both pointers to this entity.
         */
        
        if (createTypeFact) {
            log.output("creating type fact as specified in config file");
            String id = ent.getId() + "-type";
            Fact typeFact = new Fact(id, "type", ent, ent, log);
            String typeName = ent.getTypeName();
            Type t = typeHandler.getType(typeName);
            ent.setType(t);
            //       Type t = ent.getType();
            int sig = userModel.getPredicateSignificance("type",t);
            typeFact.setSignificance(sig);
            ent.addFact("type",typeFact);
        }
        else {
            log.output("NOT creating type fact");
        }
        
        log.end("finished expanding entity: " + ent.getId());
    }


    private List promoteCompFacts(HashSet<Fact> f) {
        LinkedList factsToReturn = new LinkedList();

        for (Iterator CPFactIter = CPFacts.iterator(); CPFactIter.hasNext();) {
            CPFact cpf = (CPFact) CPFactIter.next();
            if (f.contains(cpf.getFact())) {
                //	factsToReturn.add(whereToAdd, f);
                factsToReturn.add(0, cpf);

            }
            else {
                factsToReturn.add(cpf);
            }
        }
        return factsToReturn;
    }
    /*
     * collects the Facts from the CP and returns them as a List of CPFact objects.
     * Each CPFact object has a fact and a list of dependent CPFact objects.
     * Each list is sorted by significance. If, for any fact, the significance is
     * 0 then it gets skipped.
     */
    private List collectFacts(Entity ent, CPFact parent) {
        List factsToReturn = new LinkedList();
        List facts = ent.getFacts();
        Iterator iter = facts.iterator();
        while(iter.hasNext()) {
            Fact f = (Fact)iter.next();
            if (f == null) {
                log.output("BUG: found null fact in list from ent " + ent + " and cpfact " + parent);
            }
            else {
                CPFact cpf = new CPFact(f,parent);
                List depFacts = null;
                int sig = f.getSignificance();
                if( sig > 0 ) {
                    LinkedHashSet<Entity> ent2 = f.getArg2();
                    if (ent2 == null) {
                        String predName = f.getPredicateName();
                        if (predName.equals("type")) {
                            factsToReturn.add(cpf);
                        }
                        else {
                            log.output("BUG: found null ent from fact " + f);
                        }
                    }
                    else {
                        if (!ent2.contains(ent)) {
                            for (Iterator<Entity> ent2Iter = ent2.iterator(); ent2Iter.hasNext();) {
                                if (depFacts == null) {
                                    depFacts = new ArrayList();
                                }
                                depFacts.addAll(collectFacts(ent2Iter.next(), cpf));
                            }
                            if(depFacts != null) {
                                sortFacts(depFacts);
                            }
                            cpf.setDependents(depFacts);
                        }
                        factsToReturn.add(cpf);
                    }
                }
            }


        }
        return factsToReturn;
    }


    private void sortFacts(List l) {
        Comparator comp = CPFact.getComparator();
        Collections.sort(l, comp);
    }

    public List getList() {
        return CPFacts;
    }

    /*
    private void outputCPFacts(List cpfl) {
        List flatCpfl = flatten(cpfl);
        log.start("outputCPFacts");
        Iterator iter = flatCpfl.iterator();
        while(iter.hasNext()) {
            CPFact cpf = (CPFact)iter.next();
            int level = cpf.getLevel();
            log.output("CPFACT level " + level + ":\n" + cpf.getFact());
        }
        log.end("outputCPFacts");
    }
     */
    public int getFlatSize() {
        return flattenChosen(CPFacts).size();
    }




    /*
    private List flatten(List l){
        List ret = new LinkedList();
        int level = 0;
        int index = 0;
        Iterator iter = l.iterator();
        while(iter.hasNext()) {
            CPFact cpf = (CPFact)iter.next();
            cpf.setLevel(level);
        }
        ret.addAll(l);
        while(index < ret.size()){
            CPFact cpf = (CPFact)ret.get(index);
            index++;
            level = cpf.getLevel();
            List deps = cpf.getDependents();
            if(deps != null && !deps.isEmpty()) {
                Iterator diter = deps.iterator();
                while(diter.hasNext()) {
                    CPFact depcpf = (CPFact)diter.next();
                    depcpf.setLevel(level+1);
                }
                ret.addAll(deps);
            }
        }
        return ret;
    }
     */

    public String toString() {
        if(root == null) {
            return "null root";
        }
        return root.toString();
    }

}
