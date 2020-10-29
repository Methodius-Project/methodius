package uk.ac.ed.methodius;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;

/** Implements a single fact in the text planning process */

@SuppressWarnings("unchecked")
public class SingleTPFact extends TPFact {

    private Fact fact;
    private Expression expression;
    private String predName;
    private String verb;
    private String adverb;
    private TPEntity arg1;
    private TPEntity arg2;
    private Type arg1Type;
    //    private String arg2Form;
    private List cpDeps;
    private Log log;
    private UserModel um;

    /** we decide which expression to use to express the fact at this
     * stage, so that we know which verb we will use, and can
     * aggregate with other facts which have the same verb.
     * Methods isSingle() and getFacts() inherited from TPFact.java
     */

    public SingleTPFact(Fact f, UserModel um, HashMap<String,Integer> adverbMap, Log log) {
        this.log = log;
        log.start("SingleTPFact");
        fact = f;
        this.um = um;
        single = true;
        facts = new HashSet();
        arg1 = new TPEntity(fact.getArg1(), log);
        topArg1 = arg1;
        arg2 = new TPEntity(fact.getArg2(), log);
        topArg2 = arg2;
        predName = fact.getPredicateName();
        arg1Type = arg1.getType();
        Predicate pred = um.getPredicate(predName, f.getFactId());
        expression = chooseExpression(pred.getExpressions(arg1Type));
        verb = expression.getAnything("verb");
        adverb = expression.getAnything("adverb");
        aggBefore = expression.getAnything("aggregationAllowedBefore").equals("true");
        log.output("aggbefore is " + aggBefore);
        aggAfter = expression.getAnything("aggregationAllowedAfter").equals("true");
        log.output("aggafter is " +  aggAfter);
        if (arg1.isGeneric()) {
            //	    generic = true;
            aggBefore = false;
            aggAfter = false;
        }
        
        if (adverb != null) {
            firstAdverb = adverbMap.get(adverb);
            lastAdverb = adverbMap.get(adverb);
        }
        if (firstAdverb == null) {
            firstAdverb = -1;
        }
        if (lastAdverb == null) {
            lastAdverb = -1;
        }

        dependents = new HashSet();
        facts.add(this);
        log.output("single fact facts is " + facts);
        log.end("SingleTPFact");
    }

    /**
     * based on the UserModel choose which Expression to use.
     * 
     */

    private Expression chooseExpression(HashSet expressions) {
        log.start("chooseExpression");
        log.output("for predicate " + predName + " and type " + arg1Type);
        Expression thisExpr = null;
        if (expressions == null || expressions.isEmpty()) {
            log.output("creating dummy expression 1");
            thisExpr = createDummyExpression();
        }
        else {
            int topSig = -100;
            for (Iterator expIter = expressions.iterator(); expIter.hasNext();) {
                Expression e = (Expression)expIter.next();
                String expId = e.getId();
                int sig = um.getExpressionSignificance(expId);
                if (sig > 0) {
                    int used = um.getUseCount(e);
                    log.output("exp " + expId + " sig " + sig + " used " + used);
                    int usedSig = sig - used;
                    if (usedSig > topSig) {
                        log.output("chosen expression " + expId);
                        thisExpr = e;
                        topSig = usedSig;
                    }
                }
            }
            if (thisExpr == null) {
                log.output("creating dummy expression 2");
                thisExpr = createDummyExpression();
            }
            else {
                um.incUseCount(thisExpr);
            }
        }
        log.end("chooseExpression");
        return thisExpr;
    }
    
    private Expression createDummyExpression() {
        Expression thisExpr = new Expression("dummy");
        thisExpr.setAnything("arg1Type", "basic");
        thisExpr.setAnything("arg2Type", "basic");
        return thisExpr;
    }
    
    public void addCPDependents(List deps) {
        cpDeps = deps;
    }

    public List getCPDependents() {
        return cpDeps;
    }

    public void setArg1(TPEntity ent) {
        arg1 = ent;
        topArg1 = arg1;
    }

    public TPEntity getArg1() {
        return arg1;
    }

    public TPEntity getArg2() {
        return arg2;
    }

    public Expression getExpression() {
        return expression;
    }

    public String getVerb() {
        return verb;
    }

    public String getAdverb() {
        return adverb;
    }

    public Fact getFact() {
        return fact;
    }

    public String getPredicateName() {
        return predName;
    }

    /*
    public Type getArg1Type() {
	return arg1Type;
    }

    public void setArg2Form(String f) {
	arg2Form = f;
    }

    public String getArg2Form() {
	return arg2Form;
    }
     */
    public String toString() {
        String me = "SingleTPFact: arg1 " + arg1.getId() + " arg2 " + arg2.getId() + " exp " + expression.getId() + " verb " + verb + " adverb " + adverb + " aggbeg " + aggBefore + " aggaf " + aggAfter;
        return me;
    }
    @Override
    public Element toXml() {
        Element factEl = new Element("fact");
        if (another) {
            factEl.setAttribute("compare", "additive");
        }
        factEl.setAttribute("pred", predName);
        factEl.setAttribute("arg1", arg1.getCorpusXMLId());
        if (!predName.equals("type")) {
            factEl.setAttribute("arg2", arg2.getCorpusXMLId());
        }
        return factEl;
    }

}