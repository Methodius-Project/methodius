package uk.ac.ed.methodius;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.HashSet;
import java.util.HashMap;
@SuppressWarnings("unchecked")
public class TextPlan {


    private LinkedList<TPFact> textPlan;
    private Log log;
    private UserModel um;
    private int max;
    private int where;
    private Fact firstCompFact;
    private Fact secondCompFact;
    private TPFact firstCompTPFact;
    private TPFact secondCompTPFact;
    private Comparison comparison;
    private SingleTPFact typeTPF;
    private HashMap<String,Integer> adverbMap;

    /* 
     * The text plan which will be passed to LogicalForms to create
     * the input for openccg
     */

    /** text plan takes the list of facts chosen from the content
     * potential and groups them using various aggregation rules
     * If a comparison has been found, it uses that too
     */


    public TextPlan(List<?> contentPlan, Comparison bestComp, Configuration config, Log l) {
        log = l;
        log.start("TextPlan");
        log.output("raw plan " + contentPlan);
        adverbMap = config.getAdverbMap();
        log.output("adverb map is" + adverbMap);
        this.um = config.getUserModel();
        max = um.getFactsPerSentence();
        
        where = 0;

        if (bestComp != null) {
            comparison = bestComp;
            firstCompFact = bestComp.getFocalFact();
            if (comparison.hasMultipleFacts()) {
                secondCompFact = bestComp.getOtherComparison().getFocalFact();
            }
        }

        List<CPFact> orderedPlan = reorder(contentPlan);
        log.output("reordered plan " + orderedPlan);
        textPlan = buildInitialPlan(orderedPlan);
        log.output("initial plan " + textPlan);
        log.output("text plan size is " + getRealSize(textPlan));
        if (comparison != null) {
            textPlan = addComparisons(textPlan);
        }
        // no point in trying to aggregate if we're only allowed one fact per sentence
        if (max > 1) {
            // two passes through same verb to get all possibilities
            textPlan = addSameVerbAggregations();
            textPlan = addSameVerbAggregations();
            // two passes through and aggregations to get all possibilities
            // don't aggregate too muich too soon, first pass only allows lengths up to max-1
            //           textPlan = addAndAggregations(max-1);
            textPlan = addAndAggregations(max);
            textPlan = addAndAggregations(max);
        }
        textPlan = makeSureNotCannedFirst();
        log.end("TextPlan");
    }

    /** Builds an initial text plan by iterating through the content
     * list which contains level 0 facts, each of which optionally has
     * a list of its dependent facts  */

    private LinkedList<TPFact> buildInitialPlan(List<CPFact> orderedPlan) {
        int smallestAdverb = -1;
        LinkedList<TPFact> planToReturn = new LinkedList<TPFact>();
        // look at each fact from the text plan
        for (Iterator<CPFact> planIter = orderedPlan.iterator(); planIter.hasNext();) {
            CPFact cpf = planIter.next();
            Fact topFact = cpf.getFact();
            log.output("making plan based on " + topFact);
            // create a TPFact from the Fact
            SingleTPFact stpf = new SingleTPFact(topFact, um, adverbMap, log);
            
            TPEntity currentArg1 = stpf.getArg1();

            // if this is a type fact, check whether it's the same type as the previous focal object, so we can say e.g.
            // this is another amphora
            // don't do this if the previous focus is the same as this one (just in case)
            if (stpf.getPredicateName().equals("type")) {
                TPEntity currentArg2 = stpf.getArg2();
                TPEntity thisPageFocus = new TPEntity(um.getPageFocus(), log);
                Entity last = um.getFocalHistory(0);
                if (last != null) {
                    TPEntity prevPageFocus = new TPEntity(last, log);
                    if (!prevPageFocus.equals(thisPageFocus) && currentArg1.equals(thisPageFocus) && thisPageFocus.getType().equals(prevPageFocus.getType())) {
                        currentArg2.setForm("another");
                    }
                }
                // store the type fact so we can tie any generic fact to it, texts are more coherent if generic immediately follows type
                typeTPF = stpf;
            }
            else if (currentArg1.isGeneric() && typeTPF != null && currentArg1.equals(typeTPF.getArg1())) {
                typeTPF.addDependent(stpf);
            }
            // if this fact is one that we're making a comparison with
            if (topFact.equals(firstCompFact)) {
                stpf.setNewComp(true);
                firstCompTPFact = stpf;
                if (comparison.hasMultipleFacts()) {
                    stpf.setAggregatableAfter(false);
                    stpf.setAggregatableBefore(false);
                }
            }
            else if (topFact.equals(secondCompFact)) {
                stpf.setNewComp(true);
                secondCompTPFact = stpf;
                stpf.setAggregatableAfter(false);
                stpf.setAggregatableBefore(false);
            }

            // the list of dependents of this fact which have been selected to be expressed in this text            
            List<?> dependents = cpf.getChosen();

            log.output("chosen is " + dependents);
            stpf.addCPDependents(dependents);
            // make a plan for this top level fact
            List<TPFact> partialPlan = makePlan(stpf, true);
            // this is an integer because adverbs are ordered depending on the time they refer to
            // e.g. "initially" is before "now"  This was done as a hack
            int thisAdverb = stpf.getFirstAdverb();
            if (thisAdverb >= smallestAdverb) {
                planToReturn.addAll(partialPlan);
            }
            else {
                planToReturn.addAll(0, partialPlan);
            }
        }
        return planToReturn;
    }

    /**
     * add a fact to the text plan
     * <ul>
     * <li>if a fact has no dependents or cannot be aggregated, append
     * it to the plan as a single fact</li>
     * <li> go through the dependents and collect any which could be aggregated</li>
     * <li> choose which dependent to aggregate</li>
     * <ul>
     * <li> type fact</li>
     * <li> fact with verb </li>
     * <li> fact with no verb but with arg1
     * </ul>
     * <li>once the fact has been grouped with a dependent if possible, add other dependents
     * </ul>
     * @param topTPFact the single fact to add
     * @param level0 is this a level 0 fact
     */


    private LinkedList<TPFact> makePlan(SingleTPFact topTPFact, boolean level0) {
        log.start("makePlan");
        log.output("looking at " + topTPFact);
        LinkedList<TPFact> newPlan = new LinkedList<TPFact>();
        // Never aggregate anything before a top level "type" fact
        String topPredName = topTPFact.getPredicateName();
        if (level0 && topPredName.equals("type")) {
            topTPFact.setAggregatableBefore(false);
        }
        List<CPFact> dependents = topTPFact.getCPDependents();
        LinkedList<LinkedList<TPFact>> depList = new LinkedList<LinkedList<TPFact>>();

        // make a singleTPFact for each dep and do its plan (recursively)
        for (Iterator<CPFact>depIter = dependents.iterator(); depIter.hasNext();) {
            CPFact depCPFact = depIter.next();
            Fact depFact = depCPFact.getFact();
            SingleTPFact depTPFact = new SingleTPFact(depFact, um, adverbMap, log);
            depTPFact.addCPDependents(depCPFact.getChosen());
            topTPFact.addDependent(depTPFact);
            LinkedList<TPFact> dep = makePlan(depTPFact, false);
            depList.add(dep);
        }

        // if the fact is one that can potentially have another sentence added after it
        // and the arg2 is a single entity
        
        if (topTPFact.isAggregatableAfter() && topTPFact.getArg2().getEntities().size() == 1) {
            HashMap<String, LinkedList> possAggFacts = new HashMap<String, LinkedList>();
            // go through any dependents, checking whether they can be joined to this one
            // only consider them if they are single facts
            // make a list of all possibles for selection below

            for (Iterator<LinkedList<TPFact>> depIter = depList.iterator(); depIter.hasNext();) {
                LinkedList<?> l = depIter.next();
                TPFact depTPFact = (TPFact)l.getFirst();
                if (depTPFact.isSingle() && depTPFact.isAggregatableBefore()) {
                    SingleTPFact singleDepTPFact = (SingleTPFact)depTPFact;
                    if (singleDepTPFact.getPredicateName().equals("type")) {
                        //                        possAggFacts.put("type", singleDepTPFact);
                        possAggFacts.put("type", l);
                    }
                    else {
                        Expression e = singleDepTPFact.getExpression();
                        String verb = singleDepTPFact.getVerb();
                        String arg1Form = e.getAnything("arg1RefExp");
                        if (verb != null) {
                            //                            possAggFacts.put("verb", singleDepTPFact);
                            possAggFacts.put("verb", l);

                        }
                        else if (arg1Form != null) {
                            //                            possAggFacts.put("arg1Form", singleDepTPFact);
                            possAggFacts.put("arg1Form", l);

                        }
                    }
                }
            }

            // prioritize aggregations - type, verb, arg1Form

            TPFact factToAdd = null;
            //            SingleTPFact aggFact = possAggFacts.get("type");
            LinkedList<?> aggFact = possAggFacts.get("type");
            MultipleTPFact newMFact = null;
            if (aggFact != null) {
                log.output("adding type group");
                newMFact  = new MultipleTPFact(topTPFact, (SingleTPFact)aggFact.removeFirst(), "type", log);
                depList.remove(aggFact);
            }
            else {
                aggFact = possAggFacts.get("verb");
                if (aggFact != null) {
                    log.output("adding verb dep-group group");
                    newMFact  = new MultipleTPFact(topTPFact, (SingleTPFact)aggFact.removeFirst(), "dep-group", log);
                    depList.remove(aggFact);
                }
                else {
                    aggFact = possAggFacts.get("arg1Form");
                    if (aggFact != null) {
                        log.output("adding arg1Form dep-group group");
                        newMFact  = new MultipleTPFact(topTPFact, (SingleTPFact)aggFact.removeFirst(), "dep-group", log);
                        depList.remove(aggFact);
                    }
                }
            }

            // add comparison once we've done the other aggregations

            if (newMFact != null) {
                if (topTPFact.isNewComp()) {
                    log.output("resetting firstCompTPFact  to multiple " + newMFact);
                    firstCompTPFact = newMFact;
                }
                log.output("adding to plan as multiple");
                factToAdd = newMFact;
            }
            else {
                if (!alreadyExpressed(newPlan, topTPFact)) {
                    log.output("adding to plan as single");
                    factToAdd = topTPFact;
                }

            }

            // add the fact we've just created, which has possible aggregations and comparisons, to the text plan
            // then add any deps of the dep which got aggregated
            newPlan.add(factToAdd);
            if (aggFact != null) {
                for (Iterator<?> depdepIter = aggFact.iterator(); depdepIter.hasNext();) {
                    TPFact ddtpf = (TPFact)depdepIter.next();
                    log.output("adding dep dep " + ddtpf);
                    newPlan.add(ddtpf);
                }
            }
            log.output("text plan now " + newPlan);

        }
        // if there's anything left on the dep list, make sure it will stay next to its parent fact
        if (!depList.isEmpty()) {
            topTPFact.setAggregatableAfter(false);
        }

        // if the top fact made it through without being added to the
        // plan, put it there now 

        if (!alreadyExpressed(newPlan, topTPFact)) {
            log.output("adding to plan as single");
            newPlan.add(topTPFact);
            log.output("text plan now " + newPlan);

        }

        // add any deps which haven't already been added

        for (Iterator<LinkedList<TPFact>> depIter = depList.iterator(); depIter.hasNext();) {
            LinkedList<?> deps = depIter.next();
            for (Iterator<?> dIter = deps.iterator(); dIter.hasNext();) {
                TPFact dep = (TPFact)dIter.next();
                if (dep.isSingle() && !alreadyExpressed(newPlan, (SingleTPFact)dep)) {
                    log.output("adding unattached dep to plan as single");
                    newPlan.add(dep);
                    log.output("text plan now " + newPlan);
                }
                else if (!dep.isSingle()) {
                    log.output("adding  unattached dep to plan as multiple");
                    newPlan.add(dep);
                    log.output("text plan now " + newPlan);
                }
            }
        }

        log.output("plan is " + newPlan);
        log.end("makePlan");
        return newPlan;
    }

    /** once the dependent aggregations have been done, add same verb
     * aggregations if possible
     *
     * for single facts, check whether there is a later single fact
     * with the same verb, and make a group
     *
     * for multiple facts, check whether the first fact is single, and
     * if it is, check whether there is a later single fact with the
     * same verb, and make a group
     *
     */

    private LinkedList<TPFact> addComparisons(LinkedList<TPFact> tp) {
        LinkedList<TPFact> planToReturn = tp;
        if (firstCompTPFact == null) {
            return planToReturn;
        }
        log.start("addComparisons");
//      boolean isLike = false;
        String compKind = "unlike";
        if (comparison.isLike()) {
//          isLike = true;
            compKind = "like";
        }

        planToReturn.remove(firstCompTPFact);
        TPFact compFact = firstCompTPFact;
        SingleTPFact firstOldFact = new SingleTPFact(comparison.getOtherFact(), um, adverbMap, log);
        TPEntity compEnts = new TPEntity(comparison.getOtherEntities(), log);

        if (comparison.getVariety() == Comparisons.NAME) {
            compEnts.setForm("name");
        }
        else {
            if (comparison.getVariety() == Comparisons.PREV) {
                log.output("setting most recent to true for" + compEnts);
                compEnts.setMostRecent(true);
            }
            compEnts.setType(comparison.getRefType());
            compEnts.setForm("def");
        }
        firstOldFact.setArg1(compEnts);

        TPFact oldFact = firstOldFact;

        oldFact.setOldComp(true);
        if (comparison.hasMultipleFacts() && secondCompTPFact != null) {
            planToReturn.remove(secondCompTPFact);
            String multType = "and";
            if (((SingleTPFact)firstCompTPFact).getVerb().equals(((SingleTPFact)secondCompTPFact).getVerb())) {
                multType = "same-verb";
            }
            //   compFact = new MultipleTPFact(firstCompTPFact, secondCompTPFact, "and", log);
            compFact = new MultipleTPFact(firstCompTPFact, secondCompTPFact, multType, log);
            SingleTPFact secondOldFact = new SingleTPFact(comparison.getOtherComparison().getOtherFact(), um, adverbMap, log);
            secondOldFact.setArg1(compEnts);

            String join = "list";
            if (comparison.isLike()) {
                join = "and";
            }
            oldFact = new MultipleTPFact(firstOldFact, secondOldFact, join, log);
        }

        log.output("comparison is " + comparison);
        //	log.output("comparison stpf has ref " + oldFact.getRefType() +  " and num " + oldFact.getArg1Num());


        MultipleTPFact mtpf = new MultipleTPFact(oldFact, compFact, compKind, log);
        LinkedList<TPFact> compAndDeps = new LinkedList<TPFact>();
        compAndDeps.add(mtpf);
        HashSet<SingleTPFact> compDependents = mtpf.getDependents();
        log.output("dependents of comp fact are " + mtpf.getDependents());
        for (Iterator <SingleTPFact>depFactIter = compDependents.iterator(); depFactIter.hasNext();) {
            SingleTPFact compDepFact = depFactIter.next();
            compAndDeps.add(compDepFact);
            planToReturn.remove(compDepFact);
        }
        if (where == 0) {
            //where = planToReturn.size();
            where = 1;
        }
        planToReturn.addAll(where, compAndDeps);

        //go through text plan
        // when we find comparison fact
        // if it's a single comp, wrap a comp fact round it
        // if it's a multiple comp
        // get the other fact from further down the list
        // make an and fact from them
        // wrap a comp fact round the and fact
        log.end("addComparisons");
        return planToReturn;
    }

    
    // if there's same verb and no adverb, choose first
    // otherwise if same verb and different adverb, order correctly
    // add new rule for aggregating this type
    
    private LinkedList<TPFact> addSameVerbAggregations() {
        log.start("addSameVerbAggregations");
        LinkedList<TPFact> newPlan = new LinkedList<TPFact>();

        // iterate through the initial plan
        for (int i = 0; i < textPlan.size(); i++) {
            TPFact tpf = textPlan.get(i);
            if (tpf.getSize() < max) {
                log.output("same-verb considering " + tpf);
                SingleTPFact factToCheck;
                TPFact factToAdd;

                if (tpf.isSingle()) { // it's a single fact
                    factToAdd = tpf;
                    factToCheck = (SingleTPFact)tpf;
                }

                else { // it's a multiple fact
                    factToAdd = tpf;
                    TPFact potential = ((MultipleTPFact)factToAdd).getFact1();
                    if (potential.isSingle()) {
                        factToCheck = (SingleTPFact)potential;
                    }
                    else {
                        log.output("transferring multiple with multiple first fact to new plan");
                        newPlan.add(factToAdd);
                        continue;
                    }
                }

                String verb = factToCheck.getVerb();


                // Don't do this with be-verb it doesn't make any sense
                // if it can't be aggregated, put on new plan as
                // single

                if (verb == null || !factToCheck.isAggregatableBefore() || verb.equals("be-verb")) {
                    log.output("transferring null verb or no agg or be-verb to new plan");
                    newPlan.add(factToAdd);
                }
                else {
                    String adverb = factToCheck.getAdverb();
                    Expression e = factToCheck.getExpression();
                    String tense = e.getAnything("tense");
                    TPEntity arg1 = factToCheck.getArg1();
                    List<TPFact> restOfList = textPlan.subList(i+1, textPlan.size());
                    boolean dependents = factToAdd.hasDependents();
                    HashMap<String, Object> sameVerbHash = searchForSameVerbFact(verb, tense, adverb, arg1, restOfList, dependents);
                    // if there is no later single fact with same verb, put factToCheck on new plan
                    if (sameVerbHash == null) {
                        log.output("transferring fact with no verb match");
                        newPlan.add(factToAdd);
                    }
                    // if there is later single fact with same verb, put on new plan as group
                    else {
                        TPFact matchingFact = (TPFact)sameVerbHash.get("fact");
                        String sameAdverbString = (String)sameVerbHash.get("sameadverbs");
                        boolean sameAdverb = (Boolean.getBoolean(sameAdverbString) || sameAdverbString.equalsIgnoreCase("none"));
                        boolean newFirst = (Boolean)sameVerbHash.get("newfirst");

                        log.output("adding new same-verb group with adverbs " + sameAdverbString + " and new first " + newFirst);
                        // if factToAdd has dependents, put it second so that dependents will stay in right order
                        MultipleTPFact newMFact;
                        if (dependents) {
                            if (sameAdverb) {
                                newMFact = new MultipleTPFact(matchingFact, factToAdd, "same-verb", log);
                            }
                            else {
                                newMFact = new MultipleTPFact(matchingFact, factToAdd, "and", log);
                            }
                        }
                        else {
                            if (sameAdverb) {
                                newMFact = new MultipleTPFact(factToAdd, matchingFact, "same-verb", log);
                            }
                            else {
                                if (newFirst) {
                                    newMFact = new MultipleTPFact(matchingFact, factToAdd, "and", log);
                                    newMFact = new MultipleTPFact(matchingFact, factToAdd, "and", log);
                                }
                                else {
                                    newMFact = new MultipleTPFact(factToAdd, matchingFact, "and", log);
                                }
                            }
                        }
                        newPlan.add(newMFact);
                    }
                }
                log.output("new plan is " + newPlan);
            }
            else {
                newPlan.add(tpf);
            }
        }
        log.end("addSameVerbAggregations");
        return newPlan;
    }

    private LinkedList<TPFact> addAndAggregations(int thisMax) {
        log.start("addAndAggregations");
        LinkedList<TPFact> newPlan = new LinkedList<TPFact>();
        // iterate through the current plan
        for (int i = 0; i < textPlan.size(); i++) {
            TPFact tpf = textPlan.get(i);
            
            boolean added = false;
            int thisFactSize = tpf.getSize();
            List<TPFact> restOfList = textPlan.subList(i+1, textPlan.size());
            // if this fact hasn't reached max size and can be aggregated and there are facts left
            if (thisFactSize < thisMax && tpf.isAggregatable() &&!restOfList.isEmpty()) {
                log.output("and considering " + tpf);
                log.output("this size " + thisFactSize + " thismax " + thisMax);

                int firstAdverb = tpf.getFirstAdverb();
                int lastAdverb = tpf.getLastAdverb();
                
                // is there fact we can add after this one?
                if (tpf.isAggregatableAfter()) {
                    log.output("looking for fact to agg after this one");
                    TPFact matchingFact = searchForAndFact(tpf, restOfList, thisMax - thisFactSize, "before", lastAdverb);
                    if (matchingFact != null) {
                        if (matchingFact.getType().equals("and")) {
                            log.output("adding new semicolon group");
                            MultipleTPFact newMFact = new MultipleTPFact(tpf, matchingFact, ";", log);
                            newPlan.add(newMFact);
                        }
                        else if (tpf.getType().equals("and")) {
                            log.output("reorganizing");
                            TPFact fact1 = ((MultipleTPFact)tpf).getFact1();
                            TPFact fact2 = ((MultipleTPFact)tpf).getFact2();
                            log.output("adding new and group");
                            MultipleTPFact firstNewMFact = new MultipleTPFact(fact2, matchingFact, "and", log);
                            log.output("adding new semicolon group");
                            MultipleTPFact secondNewMFact = new MultipleTPFact(fact1, firstNewMFact, ";", log);
                            newPlan.add(secondNewMFact);
                        }
                        else {
                            log.output("adding new and group");
                            MultipleTPFact newMFact = new MultipleTPFact(tpf, matchingFact, "and", log);
                            newPlan.add(newMFact);
                            
                        }
                        added = true;
                        // have to bring matching fact's
                        // dependents with it since we could be
                        // getting it from several positions away
                        HashSet<?> matchingDependents = matchingFact.getDependents();
                        for (Iterator<?> depIter = matchingDependents.iterator(); depIter.hasNext();) {
                            TPFact depFact = (TPFact)depIter.next();
                            log.output("text plan is " + textPlan);
                            log.output("removing dep from its original position in plan");
                            textPlan.remove(depFact);
                            log.output("text plan is " + textPlan);
                            int whereWeAreNow = textPlan.indexOf(tpf);
                            log.output("putting it next in plan");
                            log.output("text plan is " + textPlan);
                            textPlan.add(whereWeAreNow+1, depFact);
                        }
                    }
                }

                // is there a fact we can add before this one?
                if (!added && tpf.isAggregatableBefore()) {
                    log.output("looking for fact to agg before this one");
                    TPFact matchingFact = searchForAndFact(tpf, restOfList, thisMax - thisFactSize, "after", firstAdverb);
                    if (matchingFact != null) {
                        if (tpf.getType().equals("and")) {
                            log.output("adding new semicolon group and switching order");
                            MultipleTPFact newMFact = new MultipleTPFact(matchingFact, tpf, ";", log);
                            newPlan.add(newMFact);
                        }
                        else {
                            log.output("adding new and group and switching order");
                            MultipleTPFact newMFact = new MultipleTPFact(matchingFact, tpf, "and", log);
                            newPlan.add(newMFact);
                        }
                        added = true;
                    }
                }
            }
            if (!added) {
                // if no aggregation possibilities, just keep fact as it is
                newPlan.add(tpf);
            }
            log.output("text plan is " + newPlan);
        }
        log.end("addAndAggregations");
        return newPlan;
    }
    
    private LinkedList<TPFact> makeSureNotCannedFirst() {
        LinkedList<TPFact> newPlan = (LinkedList<TPFact>)textPlan.clone();
        TPFact tpf = newPlan.get(0);
        if (tpf != null && tpf.isSingle()) {
            SingleTPFact stpf = (SingleTPFact)tpf;
            TPEntity thisArg2 = stpf.getArg2();
            if (thisArg2.isCanned()) {
                newPlan.remove(tpf);
                newPlan.addLast(tpf);
            }
        }
        return newPlan;
    }

    /** check if a fact is already in the plan
     * @param plan the current plan
     * @param tfp the TPFact under consideration
     */

    private boolean alreadyExpressed(List<TPFact> plan, SingleTPFact tpf) {
        for (Iterator<TPFact> planIter = plan.iterator(); planIter.hasNext();) {
            TPFact existing = planIter.next();
            HashSet<?> exFacts = existing.getFacts();
            if (exFacts.contains(tpf)) {
                return true;
            }
        }
        return false;
    }

    private HashMap<String, Object> searchForSameVerbFact(String verb, String tense, String adverb, TPEntity arg, List<TPFact> rest, boolean dependents) {
        // search through the remaining single facts for
        // one which has the same verb
        HashMap<String, Object> returnHash = new HashMap<String, Object>();
        for (Iterator<TPFact> restIter = rest.iterator(); restIter.hasNext();) {
            TPFact newTPF = restIter.next();
            if (newTPF.isSingle()) {
                SingleTPFact newFact = (SingleTPFact)newTPF;
                Expression newExp = newFact.getExpression();
                String newVerb = newFact.getVerb();
                String newTense = newExp.getAnything("tense");
                String newAdverb = newExp.getAnything("adverb");
                TPEntity newArg1 = newFact.getArg1();
                boolean newDependents = newFact.hasDependents();
                // if both facts have dependents, can't aggregate as things will get out of order
                if (dependents && newDependents) {
                    continue;
                }
                
                
                String sameAdverbs = "false";
                if (newAdverb == null && adverb == null) {
                    sameAdverbs = "none";
                }
                else if (newAdverb != null && adverb != null && newAdverb.equalsIgnoreCase(adverb)) {
                    sameAdverbs = "true";
                }
                else if (newAdverb != null && adverb != null) {
                    sameAdverbs = "different";
                }
                

                // if same verb, add as new group
                Boolean newFirst = false;

                if (!sameAdverbs.equalsIgnoreCase("false") && verb.equals(newVerb) && tense.equals(newTense) && newFact.isAggregatableBefore() && arg.equals(newArg1)) {
                    if (sameAdverbs.equals("different") && (adverbMap.get(newAdverb) < adverbMap.get(adverb))) {
                        if (newDependents) {
                            continue;
                        }
                        newFirst = true;
                    }
                    restIter.remove();
                    returnHash.put("fact", newFact);
                    returnHash.put("newfirst", newFirst);
                    returnHash.put("sameadverbs", sameAdverbs);
                    return returnHash;
                }
            }
        }
        return null;
    }
    
    private TPFact searchForAndFact(TPFact fact, List<TPFact> rest, int size, String beforeAfter, int adverb) {
        log.start("searchForAndFact");
        TPEntity arg1 = fact.getTopArg1();
        TPEntity arg2 = fact.getTopArg2();
        Type arg2Type = arg2.getType();
        //boolean and = fact.getType().equals("and");
        boolean dependents = fact.hasDependents();
        boolean distanceMatters = !arg1.equals(arg2);
        TPFact foundFact = null;
        boolean test;
        int shortestDistance = 1000000;
        int smallestSize = 100;
        
        // search through the remaining facts for
        // one with the same arg1 which is aggregatable and small enough to add
        for (Iterator<TPFact> restIter = rest.iterator(); restIter.hasNext();) {
            TPFact newFact = restIter.next();
            boolean newDependents = newFact.hasDependents();
            log.output("trying " + newFact);
 /**
            if (newDependents) {
                log.output("new fact has dependents, can't use until I fix bug with duplicate facts");
                continue;
            }
            **/
            // if both facts have dependents, can't aggregate as things will get out of order
            if (dependents && newDependents) {
                log.output("both have dependents, can't aggregate");
                continue;
            }
            // if different arg1, can't aggregate or it won't make sense
            TPEntity newArg1 = newFact.getTopArg1();
            if (!newArg1.equals(arg1)) {
                log.output("different arg1s, can't aggregate");
                continue;
            }
            int newSize = newFact.getSize();
            // if this fact will make the resulting fact too big
            if (newSize > size) {
                log.output("new fact too big, can't aggregate");
                continue;
            }
            int newAdv;
            if (beforeAfter.equals("before")) {
                newAdv = newFact.getFirstAdverb();
                test = newFact.isAggregatableBefore() && newAdv >= adverb;
            }
            else {
                newAdv = newFact.getLastAdverb();
                test = newFact.isAggregatableAfter() && newAdv <= adverb;
            }
            log.output("adv is " + adverb + " new adv is " + newAdv);
            if (!test) {
                log.output("can't aggregate " + beforeAfter + " new fact");
                continue;
            }
            

            log.output("can aggregate, doing arg2 distances");
            TPEntity newTopArg2 = newFact.getTopArg2();
            Type newArg2Type = newTopArg2.getType();
            log.output("new arg2 type is " + newArg2Type);
            int distance;
            if (distanceMatters) {
                
                // distance is meaningless with canned texts
                if (newTopArg2.isCanned() || arg2.isCanned()) {
                    distance = 50000;
                }
                else {
                    distance = newArg2Type.getDistance(arg2Type);
                }
            }
            else {
                distance = 1;
            }
            log.output("distance between " + arg2Type.getName() + " and " + newArg2Type.getName() + " is" + distance);
            log.output("size is " + newSize);
            log.output("smallest size is " + smallestSize);
            if (distance < shortestDistance || newSize < smallestSize) {
                log.output("found new best and");
                foundFact = newFact;
                shortestDistance = distance;
                smallestSize = newSize;
            }
        }
        if (foundFact != null) {
            rest.remove(foundFact);
        }
        else {
            log.output("didn't find one");
        }
        log.end("searchForAndFact");
        return foundFact;
    }

    private List<CPFact> reorder(List<?> plan) {
        log.start("reorder");
        List<CPFact> typeFirst = new LinkedList<CPFact>();

        for (Iterator<?> planIter = plan.iterator(); planIter.hasNext();) {
            CPFact cpf = (CPFact)planIter.next();
            String name = cpf.getFact().getPredicateName();
            if (name.equalsIgnoreCase("type")) {
                log.output("found type fact");
                typeFirst.add(0, cpf);
                where = 1;
            }
            else {
                typeFirst.add(cpf);
            }
        }

        List<CPFact> genericToFront = new LinkedList<CPFact>();
        for (Iterator<CPFact> typeFirstIter = typeFirst.iterator(); typeFirstIter.hasNext();) {

            CPFact cpf = typeFirstIter.next();
            if (cpf.getFact().getArg1().isGeneric()) {
                log.output("found generic fact");
                genericToFront.add(where, cpf);
                where++;
            }
            else {
                genericToFront.add(cpf);
            }
        }

        log.end("reorder");    
        return genericToFront;
    }


    public List<TPFact> getPlan() {
        return textPlan;
    }

    public int getRealSize(LinkedList<TPFact> plan) {
        int size = 0;
        for (Iterator<TPFact> planIter = plan.iterator(); planIter.hasNext();) {
            TPFact fact = planIter.next();
            size = size + fact.getSize();
        }
        return size;
    }

    public String toString() {
        String me = "TextPlan{\n";
        Iterator<TPFact> iter = textPlan.iterator();
        while(iter.hasNext()) {
            Object f = iter.next();
            me = me + f.toString();
        }
        me = me + "\n}";
        return me;
    }
    
    public Element toXML() {
        Element rstEl = new Element("content-plan");
        for (TPFact fact : textPlan) {
            log.output("fact is a " + fact.getClass());
            Element factEl = fact.toXml();
            if (factEl == null) {
                factEl = new Element("nullfact");
            }
            rstEl.addContent(factEl);
        }
        investigateXML(rstEl);
        return rstEl;
    }
    
    private void investigateXML(Element rstEl) {
        Element newRstEl = new Element("content-plan");
        
        List childEls = rstEl.getChildren();
        log.output(childEls.size() + " children");
        Element newJointEl = new Element("rst");
        newJointEl.setAttribute("type", "joint");

        for (int i = 0; i < childEls.size(); i++) {
            Element childEl = (Element) childEls.get(i);
            String elType = childEl.getName();
            String rstType = null;
            if (elType.equals("rst")) {
                rstType = childEl.getAttributeValue("type");
            }
            log.output("child " + i + " " + childEl.getName() + " " + childEl.getAttributeValue("type"));
            // first element
            if (i == 0) {
                // first Element is an elaboration
                if (rstType != null && rstType.equals("elaboration")) {
                    Element newElabEl = new Element("rst");
                    newRstEl.addContent(newElabEl);
                    newElabEl.setAttribute("type", "elaboration");
                    List firstElabChildren = childEl.getChildren();

                    // children of elaboration
                    for (int j = 0; j < firstElabChildren.size(); j++) {
                        Element elabChild = (Element)firstElabChildren.get(j);
                        Element newElabChild = (Element)elabChild.clone();
                        if (j == 0) {
                            // first child of the elaboration

                            newElabEl.addContent(newElabChild);
                            newElabEl.addContent(newJointEl);
                        }
                        else {
                            // other children of the elaboration
                            newJointEl.addContent(newElabChild);
                        }
                    }
                }
            }
            // subsequent element
            else {
                Element newOtherEl = (Element) childEl.clone();
                newJointEl.addContent(newOtherEl);
            }
        }
        
        Format f = Format.getPrettyFormat();
        f.setLineSeparator("\n");
        XMLOutputter xo = new XMLOutputter(f);
        log.output("munged");
        log.output(xo.outputString(newRstEl));
    }
}
