package uk.ac.ed.methodius;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

@SuppressWarnings("unchecked")

public class Comparisons {

    public static final int TYPE = 1;
    public static final int NAME = 2;
    public static final int PREV = 3;

    private Log log;
    private UserModel um;

    private List<List<Fact>> uniqueFactHistory;
    private List<Entity> uniqueFocalHistory;
    private Entity focalEntity;
    private HashSet<Fact> usefulFocalFacts;
    private HashSet<String> usefulPredNames;
    private HashMap<Type, HashSet<String>> badCombinations;

    private int maxByName = 2;
    private int maxHist = 10;
    private double hierarchyFactor = .5;
    private double historyFactor = .5;
    private double membersFactor = 2;
    private double compsFactor = 2;
    private double threshhold = -5;

    private Comparison bestComp;


    /** Takes the history of previous focal objects and facts from the
     * user model, and selects one or more facts from the content
     * potential for the new focal object which can be compared to
     * previous facts. 
     @param um the UserModel
     @param cp the new ContentPotential
     */


    public Comparisons(UserModel um, ContentPotential cp, Log log) {
        this.log = log;
        log.start("Comparisons");
        this.um = um;

        // get the new focal entity, and the list of previouss focal entities and facts expressed

        focalEntity = cp.getRoot();
        List factHistory = um.getAllContentListHistory();
        List focalHistory = um.getAllFocalHistory();

        // make sure each entity is only on the history list once, and make sure this focal entity doesn't appear on the list (can't compare with itself)

        rationalize(focalHistory, factHistory);		
        if (uniqueFocalHistory == null || uniqueFocalHistory.isEmpty()) {
            log.output("no history, so no comparisons possible");
        }
        else {
            // get the new content potential
            LinkedList focalFacts = (LinkedList)((LinkedList)cp.getList()).clone();

            // remove facts about this object which have already been expressed in a previous text
            LinkedList newFocalFacts = removeUsedFacts(focalFacts);

            // find the list of predicate names which is common to the pre-selected list of predicates and the focal facts for this entity
            usefulPredNames = getUsefulPredNames(newFocalFacts);
            log.output("useful pred names are " + usefulPredNames);

            // get the list of facts which is common to the pre-selected list of predicates and the focal facts for this entity
            usefulFocalFacts = getUsefulFacts(newFocalFacts);

            // initialize the list of types and predicates which can't be used (add to as we go along)
            badCombinations = new HashMap<Type, HashSet<String>>();

            // create all possible comparisons in a series of steps
            HashMap<String, HashSet<Comparison>> allComps = findComparisons();
            LinkedList<Comparison> prunedComps = pruneComparisons(allComps);
            HashSet<Comparison> multComps = addMultipleComparisons(prunedComps);

            // rank the comparisons
            TreeMap<Double, HashSet<Comparison>> sortedComps = doComparisonScores(multComps);

            // get the best comparison from the sorted list
            bestComp = findBestComparison(sortedComps);
            log.output("best comparison is " + bestComp);
        }
        log.end("Comparisons");
    }

    /** First step in find all possible comparisons */

    private HashMap<String, HashSet<Comparison>> findComparisons() {
        HashMap<String, HashSet<Comparison>>allComparisons = new HashMap<String, HashSet<Comparison>>();
        log.start("findComparisons");

        // uniqueFocalHistory is an ordered list of Entity objects each of which has a list of Fact objects in uniqueFactHistory
        // go through fact history one at a time 

        for (int i = 0; i < uniqueFactHistory.size(); i++) {
            int realHist = i + 1;
            List facts = uniqueFactHistory.get(i);
            Entity compEntity = (Entity) uniqueFocalHistory.get(i);
            Type compType = compEntity.getType();
            HashSet<Type> ancestors = (HashSet<Type>)compType.getAllAncestors().clone();
            ancestors.add(compType);
            log.start("looking at " + compEntity.getId());
            log.output("facts are " + facts);
            // get the subset of facts which correspond to the preselected comparison fact list
            HashSet<Fact> factsToAdd = getUsefulFacts(facts);
            log.output("facts to add are " + factsToAdd);
            // make a list of facts from the preselected list which this Entity doesn't have entries for
            HashSet<String> nullFacts = getLeftoverFacts(facts);
            log.output("null facts are " + nullFacts);

            // go through the remaining facts
            for (Iterator<Fact> factIter = factsToAdd.iterator(); factIter.hasNext();) {
                Fact f = factIter.next();
                LinkedHashSet<Entity> arg2 = f.getArg2();
                String pred = f.getPredicateName();
                log.start("predicate " + pred + " arg2 " + arg2);
                // find the corresponding fact for the focal object
                Fact focalFact = getFocalFact(pred);

                // see if there are already comparisons with this predicate
                HashSet<Comparison> newHash = new HashSet<Comparison>();
                HashSet<Comparison> existing = getExistingComparisons(f.getPredicateName(), allComparisons);
                if (existing == null) {
                    log.output("no existing comparisons with " + pred);
                    existing = new HashSet<Comparison>();
                }
                // check any existing comparisons to see if current entity can be added to them
                for (Iterator<Comparison> compIter = existing.iterator(); compIter.hasNext();) {
                    Comparison ex = compIter.next();

                    log.output("looking at comparison " + ex);
                    Type exType = ex.getGroupType();

                    // can only add to comparison if arg2s are the same

                    boolean sameArg2 = arg2.equals(ex.getArg2());
                    // if the existing comparison is a prev, can't add to it
                    if (ex.getVariety() == PREV) {
                        log.output("comp is prev, no good");
                        continue;
                    }
                    // if existing comp is a type comp, and new entity is this type or ancestor
                    else if (ex.getVariety() == TYPE && ancestors.contains(exType)) {
                        log.output("found same type " + exType.getName());
                        // if new entity has the same arg2, add it to the group
                        if (sameArg2) {
                            log.output("and same arg2 " + arg2);
                            log.output("adding " + compEntity.getId() + " to " + ex);
                            //ex.addEntity(compEntity, i);
                            ex.addEntity(compEntity, realHist);
                            log.output("now comp is " + ex);
                        }
                        // otherwise, we can't make a comparison for this type and pred, so remove it from the list and add it to the bad list for future reference
                        else {
                            log.output("found a group and pred which I don't match so removing it and adding to bad list");
                            compIter.remove();
                            HashSet<String> preds =  badCombinations.get(exType);
                            if (preds == null) {
                                preds = new HashSet<String>();
                            }
                            preds.add(pred);
                            badCombinations.put(exType, preds);
                            log.output("bad combinations are " + badCombinations);
                        }
                    }
                    // if existing comparison has entities which have names and this ent has a name
                    else if (ex.getVariety() == NAME) {
                        log.output("found a names comparison");
                        if (compEntity.getName() != null && sameArg2) {
                            // if the comp has room for another name
                            if (ex.getNumberOfEntities() < maxByName) {
                                ex.addEntity(compEntity, i);
                            }		
                            // make new comparisons with each of the ents in the existing comp
                            else {
                                int newHist = ex.getHistDistance();
                                if (realHist < newHist) {
                                    newHist = realHist;
                                }
                                HashSet<Entity> others = ex.getOtherEntities();
                                for (Iterator<Entity> otherIter = others.iterator(); otherIter.hasNext();) {
                                    Entity otherEnt = otherIter.next();
                                    LinkedHashSet<Entity> newGroup = new LinkedHashSet<Entity>();
                                    newGroup.add(compEntity);
                                    newGroup.add(otherEnt);
                                    Comparison newComp = new Comparison(focalEntity, focalFact, f, newGroup, newHist, NAME, log);
                                    newHash.add(newComp);
                                }
                                LinkedHashSet<Entity> newGroup = new LinkedHashSet<Entity>();
                                newGroup.add(compEntity);
                                Comparison newComp = new Comparison(focalEntity, focalFact, f, newGroup, realHist, NAME, log);
                                newHash.add(newComp);

                            }
                        }
                    }

                }

                log.output("adding new comparisons");
                // add new Comparisons for this fact

                // make a prev comp if it's the most recent fact
                if (i == 0) {
                    LinkedHashSet<Entity> newGroup = new LinkedHashSet<Entity>();
                    newGroup.add(compEntity);
                    for (Iterator<Type> ancestorIter = ancestors.iterator(); ancestorIter.hasNext();) {
                        Type thisType = ancestorIter.next();
                        Comparison newComp = new Comparison(focalEntity, focalFact, f, newGroup, realHist, PREV, log);
                        newComp.setGroupType(thisType);
                        newHash.add(newComp);
                        log.output("made prev comp with type " + thisType + " for " + compEntity.getId());
                    }
                }
                // make a name comp if it has a name
                if (compEntity.getName() != null) {
                    LinkedHashSet<Entity> newGroup = new LinkedHashSet<Entity>();
                    newGroup.add(compEntity);
                    
                    log.output("name is " + compEntity.getName());
                    
                    Comparison newComp = new Comparison(focalEntity, focalFact, f, newGroup, realHist, NAME, log);

                    newHash.add(newComp);
                    log.output("made name comp for " + compEntity.getId());
                }
                // make type comps
                for (Iterator<Type> ancestorIter = ancestors.iterator(); ancestorIter.hasNext();) {
                    // if there's no comparison for this type and pred, make one
                    Type thisType = ancestorIter.next();
                    log.start("type " + thisType.getName());
                    HashSet<String> badPreds = badCombinations.get(thisType);
                    if (badPreds == null || !badPreds.contains(pred)) {
                        HashSet<Comparison> existingTypes = findTypePredComparisons(allComparisons, thisType, pred);
                        log.output("found existing comps " + existingTypes);
                        if (existingTypes.isEmpty()) {
                            LinkedHashSet<Entity> newGroup = new LinkedHashSet<Entity>();
                            newGroup.add(compEntity);
                            Comparison newComp = new Comparison(focalEntity, focalFact, f, newGroup, realHist, TYPE, log);
                            newComp.setGroupType(thisType);
                            newHash.add(newComp);
                            log.output("made new comp for type " + thisType.getName() + " and entity " + compEntity.getId());
                        }
                    }
                    else {
                        log.output("pred and type on bad list");
                    }
                    log.end("type " + thisType.getName());
                }
                existing.addAll(newHash);
                allComparisons.put(pred, existing);
                log.end("predicate " + pred + " arg2 " + arg2);
            }
            log.output("adding bad preds for facts which this ent hasn't got");
            for (Iterator<Type> ancestorIter = ancestors.iterator(); ancestorIter.hasNext();) {
                Type thisType = ancestorIter.next();
                HashSet<String> badPreds = badCombinations.get(thisType);
                if (badPreds == null) {
                    badPreds = new HashSet<String>();
                }
                for (Iterator<String> badIter = nullFacts.iterator(); badIter.hasNext();) {
                    String badPred = badIter.next();
                    badPreds.add(badPred);
                }
                badCombinations.put(thisType, badPreds);
            }
            removeBadCombinations(allComparisons);
            log.output("bad combinations now " + badCombinations);
            //	    log.output("comparisons now " + allComparisons);
            log.end("looking at " + compEntity.getId());

        }
        return allComparisons;
    }

    private LinkedList<Comparison> pruneComparisons(HashMap<String, HashSet<Comparison>> theseComps) {
        log.start("pruneComparisons");
        LinkedList<Comparison> compsToReturn = new LinkedList<Comparison>();

        // go through all the comparisons and make a hash map by entities instead
        for (Iterator<String> allIter = theseComps.keySet().iterator(); allIter.hasNext();) {
            String s = allIter.next();
            log.output("going through comps with pred " + s);
            HashSet<Comparison> comps = theseComps.get(s);
            HashMap<HashSet<Entity>, HashSet<Comparison>> firstPass = new HashMap<HashSet<Entity>, HashSet<Comparison>>();
//          HashMap<Type, Comparison> singleEntComps = new HashMap<Type, Comparison>();
            for (Iterator<Comparison> compIter = comps.iterator(); compIter.hasNext();) {
                Comparison c = compIter.next();
                HashSet<Entity> theseEnts = c.getOtherEntities();
                HashSet<Comparison> theComps = firstPass.get(theseEnts);
                if (theComps == null) {
                    theComps = new HashSet<Comparison>();
                }
                theComps.add(c);
                firstPass.put(theseEnts, theComps);
            }

            // go through the new hash set by entities (still within particular pred)
            for (Iterator<HashSet<Entity>> firstPassIter = firstPass.keySet().iterator(); firstPassIter.hasNext();) {
                HashSet<Entity> ents = firstPassIter.next();
                log.output("  going through comps with ents " + ents);
                HashSet<Comparison> newComps = firstPass.get(ents);
                Integer smallestDist = null;
                Integer smallestNameDist = null;
                TreeMap<Integer, HashSet<Comparison>> bestOfBunch =  new TreeMap<Integer, HashSet<Comparison>>();
                TreeMap<Integer, HashSet<Comparison>> bestOfNameBunch =  new TreeMap<Integer, HashSet<Comparison>>();
                for (Iterator<Comparison> cIter = newComps.iterator(); cIter.hasNext();) {
                    Comparison c = cIter.next();
                    int variety = c.getVariety();
                    log.output("   seeing if we will prune " + c);

                    // throw away ones which are unlike comparisons with only one member
                    if (c.getNumberOfEntities() == 1 && !c.isLike()) {
                        log.output("    getting rid of single unlike" + c);
                    }
                    // keep all which use names
                    else if (variety == NAME) {
                        int d = c.getHierDistance();
                        log.output("     dist " + d);
                        if (smallestNameDist == null) {
                            smallestNameDist = d;
                            HashSet<Comparison> tmpHash = new HashSet<Comparison>();
                            tmpHash.add(c);
                            bestOfNameBunch.put(smallestNameDist, tmpHash);
                            log.output("    updated current name best because smallest dist");
                        }
                        else if (d < smallestNameDist) {
                            smallestNameDist = d;
                            HashSet<Comparison> tmpHash = bestOfNameBunch.get(d);
                            if (tmpHash == null) {
                                tmpHash = new HashSet<Comparison>();
                            }
                            tmpHash.add(c);
                            bestOfNameBunch.put(smallestNameDist, tmpHash);
                            log.output("    updated current name best because prev");
                        }
//                      log.output("    keeping names comp" + c);
                        //compsToReturn.add(c);
                    }
                    // if we have a single type comp and a prev comp for the same ents, one with the smallest hier dist, prev if possible
                    else {
                        int d = c.getHierDistance();
                        log.output("     dist " + d);
                        if (smallestDist == null) {
                            smallestDist = d;
                            HashSet<Comparison> tmpHash = new HashSet<Comparison>();
                            tmpHash.add(c);
                            bestOfBunch.put(smallestDist, tmpHash);
                            log.output("    updated current best because smallest dist");
                        }
                        else if (d <= smallestDist) {
                            smallestDist = d;
                            HashSet<Comparison> tmpHash = bestOfBunch.get(d);
                            if (tmpHash == null) {
                                tmpHash = new HashSet<Comparison>();
                            }
                            tmpHash.add(c);
                            bestOfBunch.put(smallestDist, tmpHash);
                            log.output("    updated current best because prev");
                        }
                    }
                }
//              if (!bestOfBunch.isEmpty()) {
                for (Iterator<Integer> bunchIter = bestOfBunch.keySet().iterator(); bunchIter.hasNext();) {
                    compsToReturn.addAll(bestOfBunch.get(bunchIter.next()));
                    log.output("   kept best of bunch" + bestOfBunch);
                }
                for (Iterator<Integer> bunchIter = bestOfNameBunch.keySet().iterator(); bunchIter.hasNext();) {
                    compsToReturn.addAll(bestOfNameBunch.get(bunchIter.next()));
                    log.output("   kept best of name bunch" + bestOfNameBunch);
                }
                //              }
            }
        }

        log.output("pruned comps are " + compsToReturn);
        log.end("pruneComparisons");
        return compsToReturn;
    }



    private HashSet<Comparison> addMultipleComparisons(LinkedList<Comparison> allCompsFlat) {
        log.start("addMultipleComparisons");
        HashSet<Comparison> allComps = new HashSet<Comparison>();
        for (int i = 0; i < allCompsFlat.size(); i++) {
            Comparison c = allCompsFlat.get(i);
            log.output("adding to comparison " + c);
            allComps.add(c);
            boolean like = c.isLike();
            int var = c.getVariety();
            
            String predName = c.getFocalFact().getPredicateName();
            List<Comparison> otherComps = allCompsFlat.subList(i+1, allCompsFlat.size());
            for (Iterator<Comparison> restIter = otherComps.iterator(); restIter.hasNext();) {
                Comparison c2 = restIter.next();
                String predName2 = c2.getFocalFact().getPredicateName();
                if (like == c2.isLike() && c.getVariety() == c2.getVariety() && c.getOtherEntities().equals(c2.getOtherEntities()) && !predName.equals(predName2)) {
                    Type gType = c.getGroupType();
                    if (var == NAME || gType.equals(c2.getGroupType())) {
                        log.output("adding comparison" + c2);
                        Comparison multComp = new Comparison(focalEntity, c.getFocalFact(), c.getOtherFact(), c.getOtherEntities(), c.getHistDistance(), c.getVariety(), log);
                        if (var == TYPE || var == PREV) {
                            multComp.setGroupType(gType);
                        }
                        multComp.addComparison(c2);
                        log.output("multComp is " + multComp);
                        allComps.add(multComp);
                    }
                }
            }
        }
        log.end("addMultipleComparisons");
        return allComps;
    }


    private HashSet<Comparison> findTypePredComparisons(HashMap<String, HashSet<Comparison>> list, Type type, String pred) {
        HashSet<Comparison> compsToReturn = new HashSet<Comparison>();
        HashSet<Comparison> allPredComps = list.get(pred);
        //	log.output("all comps with pred are " + allPredComps);
        if (allPredComps != null) {
            for (Iterator<Comparison> predCompsIter = allPredComps.iterator(); predCompsIter.hasNext();) {
                Comparison thisComp = predCompsIter.next();
                //		log.output("doing " + thisComp);
                //		log.output("type is " + type + " and this group type is " + thisComp.getGroupType());
                if (type.equals(thisComp.getGroupType())) {
                    compsToReturn.add(thisComp);
                }
            }
        }
        return compsToReturn;
    }


    public TreeMap doComparisonScores(HashSet<Comparison> allComps) {
        // Iterating through groups
        TreeMap<Double, HashSet<Comparison>> sorted = new TreeMap<Double, HashSet<Comparison>>(getComparator());
        for (Iterator<Comparison> compIter = allComps.iterator(); compIter.hasNext();) {
            Comparison c = compIter.next();
            int members = c.getNumberOfEntities();
            int hist = c.getHistDistance();
            int hier = c.getHierDistance();
            int factsNum = 1;
            if (c.hasMultipleFacts()) {
                factsNum = 2;
            }
            Double score = (membersFactor * members)
            + (compsFactor * (factsNum - 1))
            - (hierarchyFactor * hier)
            - (historyFactor * hist);
            //	    log.output("score is " + score);
            if (score > threshhold) {
                HashSet<Comparison> existingGroup = sorted.get(score);
                if (existingGroup == null) {
                    existingGroup = new HashSet<Comparison>();
                }
                existingGroup.add(c);
                sorted.put(score, existingGroup);
            }

        }
        log.output("scored comps are " + sorted);
        return sorted;
    }

    /** look through scored comps in order and pick the best*/

    public Comparison findBestComparison(TreeMap<Double, HashSet<Comparison>> sorted) {

        Comparison bestLike = null;
        Comparison bestUnlike = null;
        Integer highestLikeSig = null;
        Integer highestUnlikeSig = null;

        for (Iterator<Double> sortedCompIter = sorted.keySet().iterator(); sortedCompIter.hasNext();) {
            Double topScore = sortedCompIter.next();

            HashSet<Comparison> comps = sorted.get(topScore);

            // ordering comparisons with same score
            // - unlike is better than like (harder to find one)
            // - names is better than prev is better than type because
            // of the referring expressions we'll get for them -
            // they're in order of usefulness to distinguish between
            // other things in the history

            boolean name = false;
            for (Iterator<Comparison> compIter = comps.iterator(); compIter.hasNext();) {
                Comparison c = compIter.next();
                int var = c.getVariety();
                int thisSig = c.getSignificance();
                if (c.isLike()) { // a "like" comparison
                    if (highestLikeSig == null || thisSig > highestLikeSig) {
                        highestLikeSig = thisSig;
                        bestLike = c;
                        if (var == NAME) {
                            name = true;
                        }
                    }
                    else if (!name && thisSig == highestLikeSig && var == PREV) {
                        bestLike = c;
                    }
                    else if (thisSig == highestLikeSig && var == NAME) {
                        bestLike = c;
                        name = true;
                    }
                }
                else { // an "unlike comparison"
                    if (highestUnlikeSig == null || thisSig > highestUnlikeSig) {
                        highestUnlikeSig = thisSig;
                        bestUnlike = c;
                    }
                    else if (!name && thisSig == highestUnlikeSig && var == PREV) {
                        bestUnlike = c;
                    }
                    else if (thisSig == highestUnlikeSig && var == NAME) {
                        bestUnlike = c;
                        name = true;
                    }
                }
            }
            if (bestUnlike != null) {
                return bestUnlike;
            }
            else if (bestLike != null) {
                return bestLike;
            }
        }
        return null;
    }

    private HashSet<Comparison> getExistingComparisons(String pred, HashMap<String, HashSet<Comparison>> comparisons) {
        return comparisons.get(pred);
    }

    private Fact getFocalFact(String pred) {
        for (Iterator<Fact> focIter = usefulFocalFacts.iterator(); focIter.hasNext();) {
            Fact focFact = focIter.next();
            if (focFact.getPredicateName().equals(pred)) {
                return focFact;
            }
        }
        return null;
    }

    private HashSet<String> getLeftoverFacts(List allFacts) {
        HashSet<String> leftoverFacts = new HashSet<String>();
        HashSet<String> thesePreds = new HashSet<String>();
        for (Iterator factIter = allFacts.iterator(); factIter.hasNext();) {
            Fact f = ((CPFact) factIter.next()).getFact();
            String p = f.getPredicateName();
            thesePreds.add(p);
        }
        for (Iterator<String> predIter = usefulPredNames.iterator(); predIter.hasNext();) {
            String p = predIter.next();
            if (!thesePreds.contains(p)) {
                leftoverFacts.add(p);
            }
        }
        return leftoverFacts;
    }

    private HashSet<Fact> getUsefulFacts(List allFacts) {
        HashSet<Fact> usefulFacts = new HashSet<Fact>();
        for (Iterator factIter = allFacts.iterator(); factIter.hasNext();) {
            Fact f = ((CPFact) factIter.next()).getFact();
            if (usefulPredNames.contains(f.getPredicateName())) {
                usefulFacts.add(f);
            }
        }
        return usefulFacts;
    }

    private HashMap<String, HashSet<Comparison>> removeBadCombinations(HashMap<String, HashSet<Comparison>> allComparisons) {
        log.start("removeBadCombinationsFromAllComparisons");
        //	log.output("allComparisons " + allComparisons);
        for (Iterator<Type> badCombosIter = badCombinations.keySet().iterator(); badCombosIter.hasNext();) {
            Type t = badCombosIter.next();
            HashSet<String> badPreds = badCombinations.get(t);
            for (Iterator<String> badPredIter = badPreds.iterator(); badPredIter.hasNext();) {
                String badPred = badPredIter.next();
                //		log.output("looking for bad comp with " + t + " and " + badPred);
                HashSet<Comparison> badComps = findTypePredComparisons(allComparisons, t, badPred);
                if (!badComps.isEmpty()) {
                    HashSet<Comparison> badSet = allComparisons.get(badPred);
                    badSet.removeAll(badComps);
                    //		    log.output("removing bad comps " + badComps + " from " + badSet);
                }
            }
        }
        log.end("removeBadCombinationsFromAllComparisons");
        return allComparisons;
    }

    private void  rationalize(List focalHist, List factHist) {
        log.start("rationalize");
        // list of previous comparisons used

        HashSet<Entity> alreadyDone = new HashSet<Entity>();
        List<Entity> fullUniqueFocalHistory = new LinkedList<Entity>();
        List<List<Fact>> fullUniqueFactHistory = new LinkedList<List<Fact>>();

        // go through focal history
        for (int i = 0; i < focalHist.size(); i++) {
            Entity e = (Entity)focalHist.get(i);
            log.output("hist entity " + e + " focal entity " + focalEntity);
            List factList = (List)factHist.get(i);
            // if entry in history is not current focus
            if (!e.equals(focalEntity)) {
                if (alreadyDone.contains(e)) {
                    log.output("entity " + e + " already in history, adding facts");
                    for (int j = 0; j < fullUniqueFocalHistory.size(); j++) {
                        Entity oldE = fullUniqueFocalHistory.get(j);
                        if (oldE.equals(e)) {
                            List oldFactList = fullUniqueFactHistory.get(j);
                            oldFactList.addAll(factList);
                        }
                    }
                }
                else {
                    alreadyDone.add(e);
                    fullUniqueFocalHistory.add(e);
                    fullUniqueFactHistory.add(factList);
                }
            }
        }
        for (int i = 0; i < fullUniqueFactHistory.size(); i++) {
            List<Fact> facts = fullUniqueFactHistory.get(i);
            if (facts.isEmpty()) {
                log.output("removing " + fullUniqueFocalHistory.get(i) + " because no facts left");
                fullUniqueFactHistory.remove(i);
                fullUniqueFocalHistory.remove(i);
            }
        }

        if (fullUniqueFocalHistory.size() > maxHist) {
            uniqueFactHistory = fullUniqueFactHistory.subList(0, maxHist);
            uniqueFocalHistory = fullUniqueFocalHistory.subList(0, maxHist);
        }

        else {
            uniqueFactHistory = fullUniqueFactHistory;
            uniqueFocalHistory = fullUniqueFocalHistory;
        }
        log.end("rationalize");
    }

    private LinkedList removeUsedFacts(LinkedList focalFacts) {
        List entHistory = um.getContentListHistory(focalEntity);
        log.output("ent history for " + focalEntity + " is " + entHistory);
        if (entHistory != null) {
            for (Iterator focalFactIter = focalFacts.iterator(); focalFactIter.hasNext();) {
                Fact f = ((CPFact)focalFactIter.next()).getFact();
                for (Iterator usedFactIter = entHistory.iterator(); usedFactIter.hasNext();) {
                    Fact f2 = ((CPFact)usedFactIter.next()).getFact();
                    if (f.equals(f2)) {
                        focalFactIter.remove();
                        break;
                    }
                }
            }
        }
        return focalFacts;
    }

    private HashSet<String> getUsefulPredNames(List focalFacts) {
        log.start("getUsefulPredNames");
        HashSet<String> useful = new HashSet<String>();
        for (Iterator factIter = focalFacts.iterator(); factIter.hasNext();) {
            String pName = ((CPFact)factIter.next()).getFact().getPredicateName();
            log.output("pred is " + pName);
            if (um.isComparable(pName)) {
                useful.add(pName);
            }
        }
        log.output("useful facts are " + useful);
        log.end("getUsefulPredNames");
        return useful;
    }


    public Comparison getBestComp() {
        return bestComp;
    }


    private static class DescendingComparator implements Comparator{
        public int compare(Object o1, Object o2) /*descending order*/  {
            if (((Double) o1).doubleValue() > ((Double)o2).doubleValue()) return -1;
            else if (((Double) o1).doubleValue() < ((Double)o2).doubleValue()) return 1;
            return 0; /*equal*/
        }
    }

    private static DescendingComparator getComparator() {
        return new DescendingComparator();
    }
}
