package uk.ac.ed.methodius;

import java.io.IOException;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.JDOMException;

import uk.ac.ed.methodius.Exceptions.PredicateFormException;
import uk.ac.ed.methodius.Expression;
import uk.ac.ed.methodius.Configuration;
import uk.ac.ed.methodius.Entity;
import uk.ac.ed.methodius.SignificanceHandler;
import uk.ac.ed.methodius.Type;
@SuppressWarnings("unchecked")
public class UserModel {

    private int searchWidth;
    private Entity currentFocus;
    private int targetNumFacts = 7;

//    private String userType = null;

    private PredicateHandler predHandler;
    private Set compPreds;
    private Configuration config = null;
    private int factsPerPage;
    private int factsPerSentence;
    private boolean doComparisons = true;
    private String description;

    private SignificanceHandler sigH = null;

    /*
     * History of focal object ids and the facts expressed.
     * maxHistory limits how many of these we keep.
     * First in list = the newest.
     */
    private LinkedList factHistory = null;
    private LinkedList focalHistory = null;
    private HashMap focalFactHash = null;
    private List seenFacts = null;
    private List genericSeenFacts = null;
    private int maxHistory = 1000;
    private String userType;

    /*
     * counters for keeping track of what expressions and NPs have been used.
     * Map from expression object to int
     * Map from NP object to int.
     */
    private Map exprCounts = null;
    private Map npCounts = null;


    /*
     * Referring Expression history object
     */

    private ReferringExpressionHistory rehist = null;

    public UserModel(Configuration c,
            String userTypeName,
            int factspp,
            int factsps,
            int cpd,
            String desc) throws JDOMException, IOException, PredicateFormException {
        config = c;
        sigH=config.getSignificanceHandler();
        userType = userTypeName;
        factsPerPage = factspp;
        factsPerSentence = factsps;
        searchWidth = cpd;
        description = desc;
        
//        predHandler = new PredicateHandler(config);
        predHandler = config.getPredicateHandler();
//        compPreds = predHandler.getCompPreds();
        compPreds = config.getCompPreds();

        exprCounts = new HashMap();
        npCounts = new HashMap();
        factHistory = new LinkedList();
        focalHistory = new LinkedList();
        focalFactHash = new HashMap();
 //       comparisonHistory = new LinkedList();
        rehist = new ReferringExpressionHistory();

        seenFacts = new LinkedList();
        genericSeenFacts = new LinkedList();
        
        doComparisons = c.getComparisons();

    }

    public String getUserType() {
        return userType;
    }

    public int getSearchWidth() {
        return searchWidth;
    }

    public void setSearchWidth(int searchWidth) {
        this.searchWidth = searchWidth;
    }

    public int getTargetSize() {
        return targetNumFacts;
    }

    public void setTargetSize(int n) {
        targetNumFacts = n;
    }

    public int getOverrunSize() {
        return 3;
    }

    public void setOverrunSize(int numFacts) {

    }

    public Entity getPageFocus() {
        return currentFocus;
    }

    public void setPageFocus(Entity pageFocus) {
        this.currentFocus = pageFocus;
    }


    public String getDescription() {
        return description;
    }

    public int getFactsPerPage() {
        return factsPerPage;
    }

    public void setFactsPerPage(int p) {
        factsPerPage = p;
    }
    
    public int getFactsPerSentence() {
        return factsPerSentence;
    }

    public void setFactsPerSentence(int f) {
        factsPerSentence = f;
    }
    
    public Predicate getPredicate(String predName, String factId) {
        return predHandler.getPredicate(predName, factId);
    }
    
    public boolean doComparisons() {
        return doComparisons;
    }

    /*
     * methods for expression and np counting.
     */
    public int getUseCount(Expression e){
        Integer i = (Integer)exprCounts.get(e);
        int n = i == null ? 0 : i.intValue();
        return n;
    }

    public void incUseCount(Expression e) {
        Integer i = (Integer)exprCounts.get(e);
        int n = i == null ? 0 : i.intValue();
        n++;
        exprCounts.put(e,new Integer(n));
    }

    public int getUseCount(String np){
        Integer i = (Integer)npCounts.get(np);
        int n = i == null ? 0 : i.intValue();
        return n;
    }

    public void incUseCount(String np) {
        Integer i = (Integer)npCounts.get(np);
        int n = i == null ? 0 : i.intValue();
        n++;
        exprCounts.put(np,new Integer(n));
    }

    /*
     * methods for Fact history.
     * The fact history is in two parts, both with most recent first:
     *    a list of the focal entities
     *    a list of the ContentLists selected for expression
     */
    public void storeFactsHistory(Entity focalEnt, List cl) {
        focalHistory.addFirst(focalEnt);
        factHistory.addFirst(cl);
        List factList = (List)focalFactHash.get(focalEnt);
        if (factList == null) {
            factList = new LinkedList();
        }
        factList.addAll(cl);
        focalFactHash.put(focalEnt, factList);
        if(focalHistory.size() > maxHistory) {
            Entity lastEnt = (Entity)focalHistory.getLast();
            List lastHist = (List)factHistory.getLast();
            List lastEntFactList = (List)focalFactHash.get(lastEnt);
            lastEntFactList.removeAll(lastHist);
            focalHistory.removeLast();
            factHistory.removeLast();
        }
    }

    public int factHistorySize() {
        return factHistory.size();
    }

    public List getAllFocalHistory() {
        return focalHistory;
    }

    public Entity getFocalHistory(int i){
        if (i < focalHistory.size()) {
            Entity ent = (Entity)focalHistory.get(i);
            return ent;
        }
        return null;
    }

    public List getAllContentListHistory() {
        return factHistory;
    }

    public List getContenListHistory(int i ){
        if (i < factHistory.size()) {
            List cl = (List)factHistory.get(i);
            return cl;
        }
        return null;
    }

    public List getContentListHistory(Entity e) {
        return (List)focalFactHash.get(e);
    }


    public int getPredicateSignificance(String pred, Type arg1Type) {
        return sigH.getPredicateSignificance(pred,arg1Type,userType);
    }

    public int getExpressionSignificance(String exprId) {
        return sigH.getExpressionSignificance(exprId, userType);
    }

    public int getNpSignificance(String np) {
        return sigH.getNpSignificance(np,userType);
    }


    public void setReferringExpressionHistory(ReferringExpressionHistory reh) {
        rehist = reh;
    }

    public ReferringExpressionHistory getReferringExpressionHistory() {
        return rehist;
    }

    public boolean isComparable(String predicate) {
        return compPreds.contains(predicate);
    }

    public void nowSeenFact(String id) {
        seenFacts.add(id);
    }

    public boolean alreadySeenFact(String id) {
        return seenFacts.contains(id);
    }

    public void nowSeenGenericFact(String id) {
        genericSeenFacts.add(id);
    }

    public boolean alreadySeenGenericFact(String id) {
        return genericSeenFacts.contains(id);
    }

}
