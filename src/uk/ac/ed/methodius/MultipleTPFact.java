package uk.ac.ed.methodius;

import org.jdom.Element;

/**
 * Implements a multiple Fact in the text planning process.
 * 
 *
 */
    
@SuppressWarnings("unchecked")

public class MultipleTPFact extends TPFact{
    TPFact fact1 = null;
    TPFact fact2 = null;
    Log log = null;

    /**
     * A MultipleTPFact has pointers to two other TPFact objects,
     * which may themselves be single or multiple.  In this way we can
     * build up complex aggregation structures one stage at a time.
     * Methods isSingle() and getFacts() inherited from TPFact.java
     */
    
    public MultipleTPFact(TPFact f1, TPFact f2, String type, Log log) {
	this.log = log;
	log.start("MultipleTPFact");
	this.type = type;
	fact1 = f1;
	fact2 = f2;
	oldComp = fact1.isOldComp();
	newComp = fact1.isNewComp();
	aggBefore = fact1.isAggregatableBefore();
	aggAfter = fact2.isAggregatableAfter();
	topArg1 = fact1.getTopArg1();
	topArg2 = fact1.getTopArg2();

	facts = fact1.getFacts();
	facts.addAll(fact2.getFacts());
	log.output("fact1 is " + fact1);
	log.output("fact2 is " + fact2);
	dependents = fact1.getDependents();
	dependents.addAll(fact2.getDependents());
	dependents.removeAll(fact1.getFacts());
	dependents.removeAll(fact2.getFacts());
	log.output("new dependents " + dependents);
	// can't aggregate after this fact if it has dependents
	if (!dependents.isEmpty()) {
	    aggAfter = false;
	}
	firstAdverb = fact1.getFirstAdverb();
	lastAdverb = fact2.getLastAdverb();

	log.end("MultipleTPFact");

    }



    public TPFact getFact1() {
	return fact1;
    }

    public TPFact getFact2() {
	return fact2;
    }

    public String toString() {
	String me = "(MultipleTPFact " + facts.size() + " " + type + " fact1 " + fact1 + " fact2 " + fact2 + " aggbeg " + aggBefore + " aggaf " + aggAfter + ")";
	return me;
    }
    @Override
    public Element toXml() {
        Element factEl = new Element("rst");
        String rstType = "unknown-" + type;
        

        if (type.equals("and") || type.equals("same-verb") || type.equals(";") || type.equals("list")) {
            rstType = "joint";
        }
        else if (type.equals("like")) {
            rstType = "similarity";
        }
        else if (type.equals("unlike")) {
            rstType = "contrast";
        }
        else if (type.equals("dep-group")) {
            rstType = "elaboration";
        }
        if (fact1 instanceof SingleTPFact) {
            if (((SingleTPFact)fact1).getPredicateName().equals("type")) {
                rstType = "elaboration";
            }
        }
        factEl.setAttribute("type", rstType);
        
        if (another) {
            fact1.setAnother(true);
            fact2.setAnother(true);
        }
        log.output("fact1 is a " + fact1.getClass());
        Element fact1El = fact1.toXml();
        factEl.addContent(fact1El);
        log.output("fact2 is a " + fact2.getClass());
        Element fact2El = fact2.toXml();
        factEl.addContent(fact2El);
 /*       for (TPFact subFact : facts) {
            log.output("sub fact is a " + subFact.getClass());
            Element subFactEl = subFact.toXml();
            factEl.addContent(subFactEl);
        }*/
        return factEl;
    }
}
	