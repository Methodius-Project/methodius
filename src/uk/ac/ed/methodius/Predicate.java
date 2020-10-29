package uk.ac.ed.methodius;

import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

//import uk.ac.ed.methodius.Exceptions.PredicateFormException;

@SuppressWarnings("unchecked")

public class Predicate {

    /* List of Expression objects */
    
    HashMap<String,HashSet<Expression>> expressions = null;

    /* the parts of the predicate tag */
    private String predicateId;
    private String factToExpress;
    private Log log;
//  private boolean comparable;

    public Predicate(String fact, String id, Log log) {
        factToExpress = fact;
        predicateId = id;
        this.log = log;
        expressions = new HashMap<String,HashSet<Expression>>();
    }
    
    public String getFactToExpress() {
        return factToExpress;
    }

    public String getPredicateId() {
        return predicateId;
    }

    public void addExpression(Expression e) {
        log.start("addExpression " + e.getId());
        String newType = e.getAnything("arg1Type");
        if (newType == null) {
            newType = "NONE";
        }
        HashSet<Expression> existingExpressions = expressions.get(newType);
        if (existingExpressions == null) {
           existingExpressions = new HashSet<Expression>(); 
        }
        existingExpressions.add(e);
        expressions.put(newType, existingExpressions);
        log.output("expressions now " + expressions);
        log.end("addExpression");
    }

    public HashSet<Expression> getExpressions(Type arg1Type) {
        log.start("getExpressions for type " + arg1Type.getName());
        log.output("expressions are " + expressions);
        HashSet expressionsToReturn = expressions.get(arg1Type.getName());
        if (expressionsToReturn == null) {
            ArrayList typesToTry = new ArrayList();
            typesToTry.addAll(arg1Type.getParents());

            while(!typesToTry.isEmpty() && expressionsToReturn == null) {
                log.output("types to try now " + typesToTry);
                Type tryNext = (Type)typesToTry.get(0);
                expressionsToReturn = expressions.get(tryNext.getName());
                if (expressionsToReturn == null) {
                    typesToTry.remove(0);
                    typesToTry.addAll(tryNext.getParents());
                }
                else {
                    log.output("found type " + tryNext.getName());
                }
            }
        }
        else {
            log.output("found type " + arg1Type.getName());
        }
        if (expressionsToReturn == null) {
            log.output("trying NONE");
            expressionsToReturn = expressions.get("NONE");
        }
        log.end("getExpressions");
        return expressionsToReturn;
    }

    public String toString() {
        String me = "predicateId = " + predicateId + "\n" +
        "factToExpress = " + factToExpress + "\n";
        int n = 1;
        Iterator iter = expressions.keySet().iterator();
        while(iter.hasNext()){
            Expression expr = (Expression)iter.next();
            me = me + "Expression" + n + ":";
            n++;
            me = me + expr.toString();
        }
        return me;
    }
}
